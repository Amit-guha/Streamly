package com.example.streamly.feature.home.di

import com.example.streamly.feature.home.data.repository.HomeRepositoryImpl
import com.example.streamly.feature.home.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository
}