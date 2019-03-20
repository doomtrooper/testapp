/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = news[position]
        holder.itemView.author.text = news.author
        holder.itemView.title.text = news.title
        picasso.load(news.urlToImage).into(holder.itemView.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val row = LayoutInflater.from(parent.context).inflate(R.layout.news_row_item, parent, false)
        return ViewHolder(row)
    }

    override fun getItemCount() = news.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

class NewsDiffCallback @Inject constructor() : DiffUtil.ItemCallback<News>() {

    override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
        return oldItem == newItem
    }
}