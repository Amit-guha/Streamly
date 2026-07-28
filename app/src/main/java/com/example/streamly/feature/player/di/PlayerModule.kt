package com.example.streamly.feature.player.di

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
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

    @OptIn(UnstableApi::class)
    @Binds
    abstract fun bindPlayerController(impl: Media3PlayerController): PlayerController
}