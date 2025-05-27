package com.ashipo.metropolitanmuseum.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ashipo.metropolitanmuseum.ui.theme.MetropolitanMuseumTheme
import org.koin.androidx.compose.KoinAndroidContext

@Composable
fun TheMetApp() {
    MetropolitanMuseumTheme {
        KoinAndroidContext {
            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
            ) { padding ->
                AppNavigation(
                    modifier = Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding)
                )
            }
        }
    }
}
