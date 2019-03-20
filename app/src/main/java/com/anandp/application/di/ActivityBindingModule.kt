package com.anandp.application.di

import com.anandp.application.MainActivity
import com.anandp.application.news.di.NewsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [
        NewsModule::class
    ])
    abstract fun mainActivity(): MainActivity
}