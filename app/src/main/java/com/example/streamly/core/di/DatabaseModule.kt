package com.example.streamly.core.di

import android.content.Context
import androidx.room.Room
import com.example.streamly.core.common.constant.DatabaseConstants
import com.example.streamly.core.local.database.StreamlyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStreamlyDatabase(
        @ApplicationContext context: Context,
    ): StreamlyDatabase = Room.databaseBuilder(
        context,
        StreamlyDatabase::class.java,
        DatabaseConstants.DATABASE_NAME,
    ).build()
}