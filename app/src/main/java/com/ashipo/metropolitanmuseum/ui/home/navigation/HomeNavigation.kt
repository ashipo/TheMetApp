package com.ashipo.metropolitanmuseum.ui.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ashipo.metropolitanmuseum.ui.home.HomeScreenAction
import com.ashipo.metropolitanmuseum.ui.home.HomeScreen
import com.ashipo.metropolitanmuseum.ui.home.HomeViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object HomeRoute

fun NavGraphBuilder.homeScreen(
    onSearch: (String) -> Unit,
) {
    composable<HomeRoute> {
        HomeRoute(onSearch)
    }
}

@Composable
fun HomeRoute(
    onSearch: (String) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val departments by viewModel.departments.collectAsStateWithLifecycle()
    val searchPrefs by viewModel.searchPrefs.collectAsStateWithLifecycle()
    HomeScreen(
        departments = departments,
        searchPrefs = searchPrefs,
        onAction = { action ->
            if (action is HomeScreenAction.Search) {
                onSearch(action.query)
            } else {
                viewModel.onAction(action)
            }
        },
    )
}
