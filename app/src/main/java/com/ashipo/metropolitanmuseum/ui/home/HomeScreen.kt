package com.ashipo.metropolitanmuseum.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.ashipo.metropolitanmuseum.ui.searchresult.SearchResultScreen

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<HomeScreenModel>()
        val departments by screenModel.departments.collectAsStateWithLifecycle()
        val searchPrefs by screenModel.searchPrefs.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.current

        HomeScreenContent(
            departments = departments,
            searchPrefs = searchPrefs,
            onAction = { action ->
                if (action is HomeScreenAction.Search) {
                    navigator?.push(SearchResultScreen(action.query))
                } else {
                    screenModel.onAction(action)
                }
            },
        )
    }
}
