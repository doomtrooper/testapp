package com.anandp.application.news

import com.anandp.application.model.News
import io.reactivex.Observable

interface NewsListUi{
    fun loadData(news: List<News>)
    fun noData()

    fun intent() : Observable<NewsListEvent>
    fun render(newsListViewState: NewsListViewState)

    fun isLoading(boolean: Boolean)
}