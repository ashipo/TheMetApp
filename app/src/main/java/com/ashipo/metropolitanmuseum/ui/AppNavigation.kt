package com.ashipo.metropolitanmuseum.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation.artworkDetailScreen
import com.ashipo.metropolitanmuseum.ui.artworkdetail.navigation.navigateToArtworkDetail
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
            onShowArtwork = navController::navigateToArtworkDetail,
        )
        artworkDetailScreen(
            onNavigateUp = navController::navigateUp,
            onShowImages = navController::navigateToImageViewer,
        )
        imageViewerScreen(
            onNavigateUp = navController::navigateUp,
        )
    }
}
