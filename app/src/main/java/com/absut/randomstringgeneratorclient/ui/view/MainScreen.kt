package com.absut.randomstringgeneratorclient.ui.view

import android.content.ContentResolver
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.absut.randomstringgeneratorclient.data.model.RandomString
import com.absut.randomstringgeneratorclient.ui.viewmodel.MainViewModel
import com.absut.randomstringgeneratorclient.ui.viewmodel.ResultSate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    contentResolver: ContentResolver
) {
    var length by rememberSaveable { mutableStateOf("5") }
    var showProgress by rememberSaveable { mutableStateOf(false) }
    val randomStringState by viewModel.queryResult.collectAsState() //value returned from content provider
    val randomStrings by viewModel.getRandomStrings.collectAsState(emptyList()) //list of all generated strings from db

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showMenu by rememberSaveable { mutableStateOf(false) }

    var isError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.imePadding()
            )
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "Random String Generator") },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Delete All") },
                            onClick = {
                                viewModel.deleteAllRandomStrings()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete All"
                                )
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                value = length,
                onValueChange = {
                    isError = false
                    length = it
                },
                label = {
                    Text(text = "Enter length")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = modifier.fillMaxWidth(),
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = errorMessage,
                            color =  MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (length.toIntOrNull() == null) {
                        isError = true
                        errorMessage = "Enter valid length"
                        return@Button
                    }
                    if (length.toInt() <= 0) {
                        isError = true
                        errorMessage = "Enter length greater than zero"
                        return@Button
                    }
                    isError = false
                    errorMessage = ""
                    viewModel.fetchRandomStringFromProvider(contentResolver, length.toInt())
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate Random String")
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = showProgress
            ) {
                CircularProgressIndicator()
            }
           /* if (showProgress) {
                CircularProgressIndicator()
            }*/
            LazyColumn {
                items(
                    items = randomStrings,
                    key = { it.id }) { randomString ->
                    RandomStringItem(
                        randomString,
                        onDelete = {
                            viewModel.deleteRandomString(randomString.id)
                        }
                    )
                }
            }

            // observe result from content provider
            when (randomStringState) {
                is ResultSate.Loading -> {
                    //show loading progress
                    showProgress = true
                }

                is ResultSate.Success -> {
                    //parse to the json string then insert in db
                    showProgress = false
                    LaunchedEffect(this) {
                        val jsonString = (randomStringState as ResultSate.Success).data
                        if (jsonString!=null){
                            val parsedData = viewModel.parseRandomString(jsonString)
                            viewModel.insertRandomString(parsedData)
                        }else{
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Error: No data found")
                            }
                        }
                    }
                }

                is ResultSate.Error -> {
                    showProgress = false
                    //show snack bar with error message
                    val errorMessage = (randomStringState as ResultSate.Error).message
                    errorMessage?.let {
                        LaunchedEffect(this) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Error: $it")
                            }
                        }
                    }
                }

                else -> {}
            }

        }
    }
}


@Composable
fun RandomStringItem(randomString: RandomString, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Value: ${randomString.value}")
            Text("Length: ${randomString.length}")
            Text("Created: ${randomString.created}")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDelete,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RandomStringItemPreview() {
    RandomStringItem(
        RandomString(
            value = "dfghfdf",
            length = 9,
            created = "12/05/2025 12:32 AM"
        )
    ) {
        //do nothing
    }
}
