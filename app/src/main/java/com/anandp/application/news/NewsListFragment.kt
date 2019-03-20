package com.anandp.application.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.anandp.application.App
import com.anandp.application.R
import com.anandp.application.model.News
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.support.DaggerFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.news_list_fragment.*
import javax.inject.Inject


class NewsListFragment: DaggerFragment(), NewsListUi {

    @Inject
    lateinit var viewManager: LinearLayoutManager
    @Inject
    lateinit var app: App
    @Inject lateinit var controller: NewsListController
    @Inject
    lateinit var viewAdapter: NewsAdapter
    lateinit var disposable: Disposable

    override fun loadData(news: List<News>) {
        if (viewAdapter.news.size == 0) {
            retry.visibility = View.GONE
            progress.visibility = View.GONE
            recycler_view.visibility = View.VISIBLE
            viewAdapter.news = news.toMutableList()
            recycler_view.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        } else {
            viewAdapter.news.addAll(news.toMutableList())
            viewAdapter.notifyDataSetChanged()
        }
    }

    override fun noData() {
        progress.visibility = View.GONE
        retry.visibility = View.VISIBLE
        retry.text = getString(R.string.no_data)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.news_list_fragment, container, false)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val clicks = RxView.clicks(retry)
            .map {
                progress.visibility = View.VISIBLE
                retry.visibility = View.GONE
                NewsListScreenCreateEvent()
            }
        disposable =  Observable.merge(Observable.just(NewsListScreenCreateEvent()), clicks)
            .observeOn(Schedulers.io())
            .compose(controller)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it.render(this) }) { Log.d("NewsListFragment", it.message) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }

}