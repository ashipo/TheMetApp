package com.ashipo.metropolitanmuseum.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ashipo.metropolitanmuseum.SearchPrefs
import com.ashipo.metropolitanmuseum.data.DepartmentRepository
import com.ashipo.metropolitanmuseum.data.SearchPrefsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeScreenModel(
    departmentRepository: DepartmentRepository,
    private val searchPrefsRepository: SearchPrefsRepository,
) : ScreenModel {

    val departments = departmentRepository.departments
        .stateIn(
            scope = screenModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5_000),
        )

    val searchPrefs = searchPrefsRepository.searchPrefs
        .stateIn(
            scope = screenModelScope,
            initialValue = SearchPrefs.getDefaultInstance(),
            started = SharingStarted.WhileSubscribed(5_000),
        )

    fun onAction(action: HomeScreenAction) = screenModelScope.launch {
        when (action) {
            is HomeScreenAction.SetByTitle ->
                searchPrefsRepository.setByTitle(action.value)

            is HomeScreenAction.SetByArtistOrCulture ->
                searchPrefsRepository.setByArtistOrCulture(action.value)

            is HomeScreenAction.SetByTags ->
                searchPrefsRepository.setByTags(action.value)

            is HomeScreenAction.SetWithImage ->
                searchPrefsRepository.setWithImage(action.value)

            is HomeScreenAction.SetIsHighlight ->
                searchPrefsRepository.setIsHighlight(action.value)

            is HomeScreenAction.SetIsOnView ->
                searchPrefsRepository.setIsOnView(action.value)

            is HomeScreenAction.SetByDate ->
                searchPrefsRepository.setByDate(action.value)

            is HomeScreenAction.SetFromYear ->
                searchPrefsRepository.setFromYear(action.year)

            is HomeScreenAction.SetToYear ->
                searchPrefsRepository.setToYear(action.year)

            is HomeScreenAction.SetDepartmentId ->
                searchPrefsRepository.setDepartmentId(action.id)

            is HomeScreenAction.SetMedium ->
                searchPrefsRepository.setMedium(action.medium)

            is HomeScreenAction.SetGeoLocation ->
                searchPrefsRepository.setGeoLocation(action.geoLocation)

            else -> Unit
        }
    }
}
