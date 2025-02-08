package com.absut.randomstringgeneratorclient.ui.viewmodel

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.absut.randomstringgeneratorclient.data.model.RandomString
import com.absut.randomstringgeneratorclient.data.model.RandomTextResponse
import com.absut.randomstringgeneratorclient.data.repository.RandomStringRepository
import com.absut.randomstringgeneratorclient.ui.viewmodel.ResultSate
import com.absut.randomstringgeneratorclient.util.Constants
import com.absut.randomstringgeneratorclient.util.toTimeMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: RandomStringRepository
) : ViewModel() {

    private val uri = Uri.parse(Constants.CONTENT_PROVIDER_DATA_URL)
    private val json = Json { ignoreUnknownKeys = true } // json parser

    private val _queryResult = MutableStateFlow<ResultSate<String>?>(null)
    val queryResult: StateFlow<ResultSate<String>?> = _queryResult

    fun fetchRandomStringFromProvider(contentResolver: ContentResolver, length: Int) {
        viewModelScope.launch {
            _queryResult.value = ResultSate.Loading()
            _queryResult.value = queryRandomString(contentResolver, length)
        }
    }

    private suspend fun queryRandomString(
        contentResolver: ContentResolver,
        length: Int,
        timeoutMillis: Long = 10_000 //10sec timeout
    ): ResultSate<String> {
        return withContext(Dispatchers.IO) {
            try {
                withTimeout(timeoutMillis) {
                    val bundle = bundleOf(
                        ContentResolver.QUERY_ARG_LIMIT to length
                    )
                    val cursor = contentResolver.query(uri, null, bundle, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val dataColumnIndex =
                                it.getColumnIndex(Constants.CONTENT_PROVIDER_DATA_COLUMN_NAME)
                            if (dataColumnIndex != -1) {
                                val jsonString = it.getString(dataColumnIndex)
                                Log.d("ContentProviderQuery", "Result: $jsonString")
                                return@withTimeout ResultSate.Success(jsonString)
                            } else {
                                Log.e("ContentProviderQuery", "'data' column not found")
                                return@withTimeout ResultSate.Error("Column 'data' not found")
                            }
                        } else {
                            Log.d("ContentProviderQuery", "No data found")
                            return@withTimeout ResultSate.Error("No data found")
                        }
                    }
                    return@withTimeout ResultSate.Error("Cursor is null")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("ContentProviderQuery", "Query timed out")
                return@withContext ResultSate.Error("Query timed out. Please try reducing length.")
            } catch (e: Exception) {
                Log.e("ContentProviderQuery", "Error querying Content Provider", e)
                return@withContext ResultSate.Error("Error querying Content Provider: ${e.message}")
            }
        }
    }

    fun parseRandomString(jsonString: String): RandomString {
        val response = json.decodeFromString<RandomTextResponse>(jsonString)
        return RandomString(
            value = response.randomText.value,
            length = response.randomText.length,
            created = response.randomText.created
        )
    }

    // get list of strings from local db
    val getRandomStrings: Flow<List<RandomString>> = repository.getAllRandomStrings()

    fun insertRandomString(randomString: RandomString) {
        viewModelScope.launch {
            repository.insertRandomString(randomString)
        }
    }

    fun deleteRandomString(id: Int) {
        viewModelScope.launch {
            repository.deleteRandomString(id)
        }
    }

    fun deleteAllRandomStrings() {
        viewModelScope.launch {
            repository.deleteAllRandomStrings()
        }
    }
}

sealed class ResultSate<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : ResultSate<T>(data)
    class Loading<T>() : ResultSate<T>()
    class Error<T>(message: String) : ResultSate<T>(message = message)
}