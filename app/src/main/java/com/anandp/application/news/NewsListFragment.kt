package com.anandp.application.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anandp.application.App
import com.anandp.application.R
import com.anandp.application.model.News
import com.anandp.application.news.NewsListEvent.RetryEvent
import com.anandp.application.news.NewsListEvent.ScreenCreateEvent
import com.anandp.persistence.ExpensiveObject
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.support.DaggerFragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.news_list_fragment.*
import javax.inject.Inject


class NewsListFragment: DaggerFragment(), NewsListUi {
    override fun isLoading(boolean: Boolean) = if (boolean) progress.visibility = View.GONE else progress.visibility = View.GONE

    override fun render(state: NewsListViewState) {
        isLoading(state.isLoading)
        if (state.news.isNotEmpty()) loadData(state.news)
        if (state.news.isEmpty()) noData()
        if (state.err!=null) noData()
    }

    override fun intent(): Observable<NewsListEvent> {
        val clicks = RxView.clicks(retry)
            .map { RetryEvent }
        val observable = Observable.just<NewsListEvent>(ScreenCreateEvent)
        return observable.mergeWith(clicks)
    }

    @Inject
    lateinit var app: App
    @Inject lateinit var controller: Composer
    @Inject
    lateinit var viewAdapter: NewsAdapter
    @Inject
    lateinit var obj: ExpensiveObject
    private lateinit var listener: RecyclerViewClickListener
    private lateinit var presenter: NewsListPresenter


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
        Log.d("Dagger: ", obj.toString())
        presenter = NewsListPresenter(this, controller)
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
        presenter.binds()
        viewAdapter.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewAdapter.news.clear()
        presenter.dispose()
    }

    interface RecyclerViewClickListener {

        fun onClick(position: Int)
    }

}