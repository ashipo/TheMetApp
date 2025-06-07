@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ashipo.metropolitanmuseum.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.ashipo.metropolitanmuseum.ui.LocalAnimatedVisibilityScope
import com.ashipo.metropolitanmuseum.ui.LocalSharedTransitionScope

/**
 * A wrapper composable that provides [SharedTransitionScope] and [AnimatedVisibilityScope] for the
 * [content]. Can be used in previews or tests.
 *
 * @param content that needs the scopes
 */
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
