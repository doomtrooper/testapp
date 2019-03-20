
package com.anandp.application.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anandp.application.R
import com.anandp.application.model.News
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_row_item.view.*
import javax.inject.Inject


class NewsAdapter @Inject constructor(diffCallback: NewsDiffCallback, val picasso: Picasso): ListAdapter<News, NewsAdapter.ViewHolder>(diffCallback) {

    var news = mutableListOf<News>()
    lateinit var listener: NewsListFragment.RecyclerViewClickListener

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = news[position]
        holder.itemView.author.text = news.author
        holder.itemView.title.text = news.title
        picasso.load(news.urlToImage).into(holder.itemView.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val row = LayoutInflater.from(parent.context).inflate(R.layout.news_row_item, parent, false)
        return ViewHolder(row).listen { listener.onClick(it) }
    }

    override fun getItemCount() = news.size

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)
}

class NewsDiffCallback @Inject constructor() : DiffUtil.ItemCallback<News>() {

    override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
        return oldItem == newItem
    }
}

fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition)
    }
    return this
}