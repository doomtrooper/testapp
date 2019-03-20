package com.anandp.application.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anandp.application.model.News

@Database(entities = [News::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun getRepoDao(): NewsDao
}
