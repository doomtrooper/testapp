package com.anandp.application.news

import com.anandp.application.model.News

interface NewsListUi{
    fun loadData(news: List<News>)
    fun noData()
}