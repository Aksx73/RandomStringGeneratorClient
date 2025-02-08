package com.absut.randomstringgeneratorclient.data.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.absut.randomstringgeneratorclient.util.Constants

@Keep
@Entity(tableName = Constants.DATABASE_TABLE_NAME)
data class RandomString(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val value: String,
    val length: Int,
    val created: String
)
