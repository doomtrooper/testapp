package com.anandp.application.persistance

import androidx.room.TypeConverter

class DataConverters {
    @TypeConverter
    fun fromNull(author: String?): String = author ?: ""
}