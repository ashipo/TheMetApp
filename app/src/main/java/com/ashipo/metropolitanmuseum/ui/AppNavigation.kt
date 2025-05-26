package com.ashipo.metropolitanmuseum.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ashipo.metropolitanmuseum.ui.artwork.navigation.artworkScreen
import com.ashipo.metropolitanmuseum.ui.artwork.navigation.navigateToArtwork
import com.ashipo.metropolitanmuseum.ui.home.navigation.HomeRoute
import com.ashipo.metropolitanmuseum.ui.home.navigation.homeScreen
import com.ashipo.metropolitanmuseum.ui.imageviewer.navigation.imageViewerScreen
import com.ashipo.metropolitanmuseum.ui.imageviewer.navigation.navigateToImageViewer
import com.ashipo.metropolitanmuseum.ui.searchresult.navigation.navigateToSearchResult
import com.ashipo.metropolitanmuseum.ui.searchresult.navigation.searchResultScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        homeScreen(
            onSearch = navController::navigateToSearchResult,
        )
        searchResultScreen(
            onNavigateUp = navController::navigateUp,
            onShowArtwork = navController::navigateToArtwork,
        )
        artworkScreen(
            onNavigateUp = navController::navigateUp,
            onShowFullscreen = navController::navigateToImageViewer,
        )
        imageViewerScreen(
            onNavigateUp = navController::navigateUp,
        )
    }
}
