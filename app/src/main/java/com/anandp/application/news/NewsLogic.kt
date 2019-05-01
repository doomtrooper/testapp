package com.anandp.application.news

import com.anandp.application.API_KEY
import com.anandp.application.api.NewsApiService
import com.anandp.application.model.News
import com.anandp.application.model.NewsResponse
import com.anandp.application.news.NewsListAction.LoadNewsAction
import com.anandp.application.news.NewsListResult.Error
import com.anandp.application.news.NewsListResult.Success
import com.anandp.application.persistance.NewsDao
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

sealed class NewsListEvent {
    object ScreenCreateEvent : NewsListEvent()
    object RetryEvent : NewsListEvent()
}

sealed class NewsListAction {
    object LoadNewsAction : NewsListAction()
}

sealed class NewsListResult {
    data class Success(val articles: List<News>) : NewsListResult()
    data class Error(val err: Throwable) : NewsListResult()
}

data class NewsListViewState(val isLoading: Boolean, val news: List<News>, val err: Throwable?){
    companion object Factory{
        fun default() = NewsListViewState(isLoading = false, news = listOf(), err = null)
    }
}

class Composer @Inject constructor(val api: NewsApiService, val dao: NewsDao) :
    ObservableTransformer<NewsListAction, NewsListResult> {
    override fun apply(upstream: Observable<NewsListAction>): ObservableSource<NewsListResult> {
        val replayedStream = upstream
            .ofType(LoadNewsAction::class.java)
            .replay()
            .refCount()

        val localObs = localStream(replayedStream)

        val networkObs = networkStream(replayedStream)

        return Observable.merge(localObs, networkObs)
    }

    private fun localStream(replayedStream: Observable<LoadNewsAction>): Observable<NewsListResult> {
        return replayedStream
            .map { dao.getAllNews() }
            .filter { it.isNotEmpty() }
            .map { Success(it) }
    }

    private fun networkStream(replayedStream: Observable<LoadNewsAction>): Observable<NewsListResult> {
        val networkObs = replayedStream
            .flatMap {
                api.getNews("in", API_KEY)
                    .onErrorReturnItem(NewsResponse())
            }
            .observeOn(Schedulers.newThread())
            .map { dao.insert(it.articles) }
            .map { dao.getAllNews() }


        val noData = networkObs
            .filter { it.isEmpty() }
            .map { Error(IllegalStateException("no data")) }

        val withData = networkObs
            .filter { it.isNotEmpty() }
            .map { Success(it) }
        return Observable.merge(noData, withData)
    }

}