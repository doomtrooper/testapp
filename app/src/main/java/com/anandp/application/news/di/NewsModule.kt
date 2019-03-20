package com.anandp.application.news.di

import com.anandp.application.di.FragmentScoped
import com.anandp.application.news.NewsListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NewsModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeRootFragment(): NewsListFragment
}