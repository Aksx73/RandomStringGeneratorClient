package com.absut.randomstringgeneratorclient.data.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RandomTextResponse(
    val randomText: RandomText
)

@Keep
@Serializable
data class RandomText(
    val value: String,
    val length: Int,
    val created: String
)

