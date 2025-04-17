package com.ashipo.metropolitanmuseum.ui.home

sealed interface HomeScreenAction {
    data class Search(val query: String) : HomeScreenAction
    data class SetByTitle(val value: Boolean) : HomeScreenAction
    data class SetByArtistOrCulture(val value: Boolean) : HomeScreenAction
    data class SetByTags(val value: Boolean) : HomeScreenAction
    data class SetWithImage(val value: Boolean) : HomeScreenAction
    data class SetIsHighlight(val value: Boolean) : HomeScreenAction
    data class SetIsOnView(val value: Boolean) : HomeScreenAction
    data class SetByDate(val value: Boolean) : HomeScreenAction
    data class SetFromYear(val year: Int) : HomeScreenAction
    data class SetToYear(val year: Int) : HomeScreenAction
    data class SetDepartmentId(val id: Int) : HomeScreenAction
    data class SetMedium(val medium: List<String>) : HomeScreenAction
    data class SetGeoLocation(val geoLocation: List<String>) : HomeScreenAction
}
