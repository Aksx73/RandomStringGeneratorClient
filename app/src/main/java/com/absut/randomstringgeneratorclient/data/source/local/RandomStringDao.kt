package com.absut.randomstringgeneratorclient.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.absut.randomstringgeneratorclient.data.model.RandomString
import kotlinx.coroutines.flow.Flow

@Dao
interface RandomStringDao {
    //todo get all, insert, delete, delete all

    //order desc by id
    @Query("SELECT * FROM RandomStringTable ORDER BY id DESC")
    fun getAllRandomStrings(): Flow<List<RandomString>>

    @Insert
    suspend fun insertRandomString(randomString: RandomString)

    @Query("DELETE FROM RandomStringTable WHERE id = :id")
    suspend fun deleteRandomStringById(id: Int)

    @Query("DELETE FROM RandomStringTable")
    suspend fun deleteAllRandomStrings()

}