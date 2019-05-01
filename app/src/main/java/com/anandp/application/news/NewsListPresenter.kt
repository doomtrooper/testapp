package com.anandp.application.news

import android.util.Log
import com.anandp.application.news.NewsListAction.LoadNewsAction
import com.anandp.application.news.NewsListEvent.RetryEvent
import com.anandp.application.news.NewsListEvent.ScreenCreateEvent
import com.anandp.application.news.NewsListResult.Error
import com.anandp.application.news.NewsListResult.Success
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class NewsListPresenter(private val ui: NewsListUi, private val controller: Composer) {
    private lateinit var disposable: Disposable

    fun binds() {
        disposable = ui.intent()
            .observeOn(Schedulers.computation())
            .map { actionFromIntent(it) }
            .compose(controller)
            .scan(NewsListViewState.default(), reducer)
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ui.render(it)}) { Log.d("Anand ", it.message) }
    }

    private fun actionFromIntent(event: NewsListEvent): NewsListAction =
        when (event) {
            ScreenCreateEvent -> LoadNewsAction
            RetryEvent -> LoadNewsAction
        }

    private val reducer = BiFunction { previousState: NewsListViewState, result: NewsListResult ->
        when (result) {
            is Success -> previousState.copy(isLoading = false, news = result.articles)
            is Error -> previousState.copy(isLoading = false, err = result.err)
        }
    }

    fun dispose() {
        disposable.dispose()
    }


}