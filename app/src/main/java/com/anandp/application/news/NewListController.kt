package com.anandp.application.news

import com.anandp.application.API_KEY
import com.anandp.application.UiChange
import com.anandp.application.UiEvent
import com.anandp.application.api.NewsApiService
import com.anandp.application.model.News
import com.anandp.application.model.NewsResponse
import com.anandp.application.persistance.NewsDao
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import javax.inject.Inject

class NewsListController @Inject constructor(val api: NewsApiService, val dao: NewsDao) :
    ObservableTransformer<UiEvent, UiChange<NewsListUi>> {

    override fun apply(upstream: Observable<UiEvent>): ObservableSource<UiChange<NewsListUi>> {
        val replayedStream = upstream
            .ofType(NewsListScreenCreateEvent::class.java)
            .replay()
            .refCount()

        val localObs = replayedStream
            .map { dao.getAllNews() }
            .filter { it.isNotEmpty() }
            .flatMap { newsListReceived(it) }

        val networkObs = replayedStream
            .flatMap {
                api.getNews("in", API_KEY)
                    .onErrorReturnItem(NewsResponse())
            }
            .map { dao.insert(it.articles) }
            .map { dao.getAllNews() }


        val noData = networkObs
            .filter { it.isEmpty() }
            .flatMap { noDataReceived() }

        val withData = networkObs
            .filter { it.isNotEmpty() }
            .flatMap { newsListReceived(it) }

        return Observable.merge(localObs, withData, noData)
    }

    private fun newsListReceived(news: List<News>): ObservableSource<UiChange<NewsListUi>> {
        return Observable.just(object : UiChange<NewsListUi> {
            override fun render(t: NewsListUi) {
                t.loadData(news)
            }
        })
    }

    private fun noDataReceived(): ObservableSource<UiChange<NewsListUi>> {
        return Observable.just(object : UiChange<NewsListUi> {
            override fun render(t: NewsListUi) {
                t.noData()
            }
        })
    }

}