package com.ashipo.metropolitanmuseum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
                    Scaffold(
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    ) { padding ->
                        Box(
                            modifier = Modifier
                                .padding(padding)
                                .consumeWindowInsets(padding)
                        ) {
                            Navigator(HomeScreen())
                        }
                    }
                }
            }
        }
    }
}
