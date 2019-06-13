package com.anandp.persistence

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PersistenceModule {
    @Provides
    @Singleton
    fun proExp(config: Config) = ExpensiveObject(config.interval)
}