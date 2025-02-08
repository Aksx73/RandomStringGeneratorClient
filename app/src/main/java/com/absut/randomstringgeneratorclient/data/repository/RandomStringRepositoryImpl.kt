package com.absut.randomstringgeneratorclient.data.repository

import com.absut.randomstringgeneratorclient.data.model.RandomString
import com.absut.randomstringgeneratorclient.data.source.local.RandomStringDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RandomStringRepositoryImpl @Inject constructor(
    private val dao: RandomStringDao
) : RandomStringRepository {

    override fun getAllRandomStrings(): Flow<List<RandomString>> {
        return dao.getAllRandomStrings()
    }

    override suspend fun insertRandomString(randomString: RandomString) {
        return dao.insertRandomString(randomString)
    }

    override suspend fun deleteRandomString(id: Int) {
        return dao.deleteRandomStringById(id)
    }

    override suspend fun deleteAllRandomStrings() {
        return dao.deleteAllRandomStrings()
    }

}