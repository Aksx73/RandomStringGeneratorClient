package com.absut.randomstringgeneratorclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.absut.randomstringgeneratorclient.ui.theme.RandomStringGeneratorClientTheme
import com.absut.randomstringgeneratorclient.ui.view.MainScreen
import com.absut.randomstringgeneratorclient.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RandomStringGeneratorClientTheme {
                MainScreen(
                    viewModel = viewModel<MainViewModel>(),
                    contentResolver = contentResolver,
                )
            }
        }
    }
}

