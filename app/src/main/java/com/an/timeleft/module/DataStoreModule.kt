package com.an.timeleft.module

import android.content.Context
import com.an.timeleft.data.TimeGridDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideLeftDataStore(
        @ApplicationContext context: Context
    ): TimeGridDataStore = TimeGridDataStore(context)
}