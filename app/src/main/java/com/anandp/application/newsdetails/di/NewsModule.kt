package com.anandp.application.news.di

import com.anandp.application.di.FragmentScoped
import com.anandp.application.newsdetails.NewsDetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NewsDetailsModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeRootFragment(): NewsDetailsFragment
}