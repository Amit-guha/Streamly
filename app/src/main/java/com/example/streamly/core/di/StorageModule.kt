package com.example.streamly.core.di

import com.example.streamly.core.data.storage.datastore.AppPreferencesImpl
import com.example.streamly.core.domain.storage.datastore.AppPreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    abstract fun bindAppPreferences(impl: AppPreferencesImpl): AppPreferences
}