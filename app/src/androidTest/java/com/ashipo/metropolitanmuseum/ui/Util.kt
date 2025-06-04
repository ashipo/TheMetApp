package com.ashipo.metropolitanmuseum.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedScopes(content: @Composable () -> Unit) {
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            AnimatedVisibility(true) {
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    content()
                }
            }
        }
    }
}
