package com.anandp.application.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "news")
data class News(
    val author: String?,
    val title: String?,
    @SerializedName("urlToImage")
    @Expose
    val urlToImage: String?,
    @PrimaryKey
    val url: String
)