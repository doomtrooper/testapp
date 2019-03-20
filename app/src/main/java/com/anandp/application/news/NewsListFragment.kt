package com.anandp.application.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
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
    lateinit var app: App
    @Inject lateinit var controller: NewsListController
    @Inject
    lateinit var viewAdapter: NewsAdapter
    lateinit var disposable: Disposable
    lateinit var listener: RecyclerViewClickListener


    override fun loadData(news: List<News>) {
        if (viewAdapter.news.size == 0) {
            retry.visibility = View.GONE
            progress.visibility = View.GONE
            recycler_view.visibility = View.VISIBLE
            viewAdapter.news = news.toMutableList()
        } else {
            val filter = news.filter { !viewAdapter.news.contains(it) }
            viewAdapter.news.addAll(filter)
            viewAdapter.notifyDataSetChanged()
        }
    }

    override fun noData() {
        progress.visibility = View.GONE
        retry.visibility = View.VISIBLE
        retry.text = getString(R.string.no_data)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.news_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val clicks = RxView.clicks(retry)
            .map {
                progress.visibility = View.VISIBLE
                retry.visibility = View.GONE
                NewsListScreenCreateEvent()
            }
        listener = object : RecyclerViewClickListener{
            override fun onClick(position: Int) {
                val bundle = bundleOf(
                    "url" to viewAdapter.news[position].url
                )
                findNavController().navigate(R.id.news_to_details, bundle)
            }
        }
        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(app)
            adapter = viewAdapter
        }
        viewAdapter.listener = listener
        disposable =  Observable.merge(Observable.just(NewsListScreenCreateEvent()), clicks)
            .observeOn(Schedulers.io())
            .compose(controller)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ it.render(this) }) { throw it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewAdapter.news.clear()
        disposable.dispose()
    }

    interface RecyclerViewClickListener {

        fun onClick(position: Int)
    }

}