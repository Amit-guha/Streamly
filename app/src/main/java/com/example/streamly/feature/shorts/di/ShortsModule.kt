package com.example.streamly.feature.shorts.di

import com.example.streamly.feature.shorts.data.repository.ShortsRepositoryImpl
import com.example.streamly.feature.shorts.domain.repository.ShortsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ShortsModule {

    @Binds
    abstract fun bindShortsRepository(impl: ShortsRepositoryImpl): ShortsRepository

    @Binds
    abstract fun bindShortsPlayerPool(impl: Media3ShortsPlayerPool): ShortsPlayerPool
}