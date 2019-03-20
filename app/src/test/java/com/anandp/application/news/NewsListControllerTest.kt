package com.anandp.application.news

import com.anandp.application.API_KEY
import com.anandp.application.UiEvent
import com.anandp.application.api.NewsApiService
import com.anandp.application.model.News
import com.anandp.application.model.NewsResponse
import com.anandp.application.persistance.NewsDao
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit

/**
 * Created by Anand Parshuramka on 20/03/19.
 */
class NewsListControllerTest {

    @get:Rule
    var mockitoRule = MockitoJUnit.rule()
    @get:Rule
    var testSchedulerRule = TestSchedulerRule()
    @Mock
    lateinit var dao: NewsDao
    @Mock
    lateinit var api: NewsApiService
    @Mock
    lateinit var ui: NewsListUi

    private val events = PublishSubject.create<UiEvent>()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val controller = NewsListController(api, dao)
        events.compose(controller).subscribe { change -> change.render(ui) }
    }

    @Test
    fun given_event_when_data_not_present_locally_then_do_nothing() {
        Mockito.`when`(dao.getAllNews()).thenReturn(mutableListOf())
        Mockito.`when`(api.getNews("in", API_KEY)).thenReturn(Observable.empty())
        events.onNext(NewsListScreenCreateEvent())
        Mockito.verifyNoMoreInteractions(ui)
    }

    @Test
    fun given_event_when_data_present_locally_then_show_news() {
        val news = News("Anand", "TestApp", "abcd", "xyz")
        val list = mutableListOf<News>()
        list.add(news)
        Mockito.`when`(dao.getAllNews()).thenReturn(list)
        Mockito.`when`(api.getNews("in", API_KEY)).thenReturn(Observable.empty())
        events.onNext(NewsListScreenCreateEvent())
        Mockito.verify(ui).loadData(list)
        Mockito.verifyNoMoreInteractions(ui)
    }

    @Test
    fun given_event_when_data_not_fetched_then_do_nothing() {
        Mockito.`when`(dao.getAllNews()).thenReturn(mutableListOf())
        Mockito.`when`(api.getNews("in", API_KEY)).thenReturn(Observable.empty())
        events.onNext(NewsListScreenCreateEvent())
        Mockito.verifyNoMoreInteractions(ui)
    }

    @Test
    fun given_event_when_data_fetched_then_show_news() {
        val news = News("Anand", "TestApp", "abcd", "xyz")
        val list = mutableListOf<News>()
        list.add(news)
        Mockito.`when`(dao.getAllNews()).thenReturn(list)
        Mockito.`when`(api.getNews("in", API_KEY)).thenReturn(Observable.just(NewsResponse(list)))
        events.onNext(NewsListScreenCreateEvent())
        Mockito.verify(ui).loadData(list)
        Mockito.verifyNoMoreInteractions(ui)
    }
}