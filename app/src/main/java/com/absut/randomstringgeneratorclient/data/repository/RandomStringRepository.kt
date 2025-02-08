package com.absut.randomstringgeneratorclient.data.repository

import com.absut.randomstringgeneratorclient.data.model.RandomString
import kotlinx.coroutines.flow.Flow

interface RandomStringRepository {

    fun getAllRandomStrings(): Flow<List<RandomString>>

    suspend fun insertRandomString(randomString: RandomString)

    suspend fun deleteRandomString(id: Int)

    suspend fun deleteAllRandomStrings()
}