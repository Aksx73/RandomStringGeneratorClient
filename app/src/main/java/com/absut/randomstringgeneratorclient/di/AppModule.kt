package com.absut.randomstringgeneratorclient.di

import android.app.Application
import androidx.room.Room
import com.absut.randomstringgeneratorclient.data.repository.RandomStringRepository
import com.absut.randomstringgeneratorclient.data.repository.RandomStringRepositoryImpl
import com.absut.randomstringgeneratorclient.data.source.local.RandomStringDao
import com.absut.randomstringgeneratorclient.data.source.local.RandomStringDatabase
import com.absut.randomstringgeneratorclient.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun providesDatabase(app: Application): RandomStringDatabase {
        return Room.databaseBuilder(
            app,
            RandomStringDatabase::class.java,
            Constants.DATABASE_TABLE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideRandomStringRepository(dao: RandomStringDao): RandomStringRepository =
        RandomStringRepositoryImpl(dao)

    @Singleton
    @Provides
    fun providesRandomStringDao(database: RandomStringDatabase) = database.getRandomStringDao()

}