package com.example.streamly.feature.player.di

import com.example.streamly.feature.player.data.repository.PlayerRepositoryImpl
import com.example.streamly.feature.player.domain.repository.PlayerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    abstract fun bindPlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository

    @Binds
    abstract fun bindPlayerController(impl: Media3PlayerController): PlayerController
}