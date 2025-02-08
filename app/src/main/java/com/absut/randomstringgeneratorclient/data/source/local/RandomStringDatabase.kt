package com.absut.randomstringgeneratorclient.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.absut.randomstringgeneratorclient.data.model.RandomString
import com.absut.randomstringgeneratorclient.util.Constants

@Database(entities = [RandomString::class], version = 1, exportSchema = false)
abstract class RandomStringDatabase() : RoomDatabase() {

    abstract fun getRandomStringDao(): RandomStringDao

    /*companion object {
        @Volatile
        private var INSTANCE: RandomStringDatabase? = null

        fun getRandomStringDatabase(context: Context): RandomStringDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RandomStringDatabase::class.java,
                    Constants.DATABASE_TABLE_NAME
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }*/
}