package com.ashipo.metropolitanmuseum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.ashipo.metropolitanmuseum.ui.home.HomeScreen
import com.ashipo.metropolitanmuseum.ui.theme.MetropolitanMuseumTheme
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetropolitanMuseumTheme {
                KoinAndroidContext {
                    Box(
                        Modifier.background(MaterialTheme.colorScheme.background)
                            .windowInsetsPadding(WindowInsets.safeDrawing),
                    ) {
                        Navigator(HomeScreen())
                    }
                }
            }
        }
    }
}
