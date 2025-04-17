package com.ashipo.metropolitanmuseum.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ashipo.metropolitanmuseum.Department
import com.ashipo.metropolitanmuseum.R
import com.ashipo.metropolitanmuseum.SearchPrefs
import com.ashipo.metropolitanmuseum.department
import com.ashipo.metropolitanmuseum.searchPrefs
import com.ashipo.metropolitanmuseum.ui.home.components.DepartmentPicker
import com.ashipo.metropolitanmuseum.ui.home.components.PeriodPicker
import com.ashipo.metropolitanmuseum.ui.home.components.TagsPicker
import com.ashipo.metropolitanmuseum.ui.home.components.ToggleField
import com.ashipo.metropolitanmuseum.ui.util.GEO_LOCATION_MAX_COUNT
import com.ashipo.metropolitanmuseum.ui.util.GEO_LOCATION_MAX_LENGTH
import com.ashipo.metropolitanmuseum.ui.util.MEDIUM_MAX_COUNT
import com.ashipo.metropolitanmuseum.ui.util.MEDIUM_MAX_LENGTH
import com.ashipo.metropolitanmuseum.ui.util.SEARCH_MAX_LENGTH

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    departments: List<Department>,
    searchPrefs: SearchPrefs,
    onAction: (HomeScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    val search = {
        onAction(HomeScreenAction.Search(searchQuery.text))
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = search,
                modifier = Modifier.testTag("searchButton")
            ) {
                Icon(Icons.Default.Search, stringResource(R.string.search))
            }
        },
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = if (it.text.length <= SEARCH_MAX_LENGTH) {
                        it
                    } else {
                        it.copy(text = it.text.take(SEARCH_MAX_LENGTH))
                    }
                },
                placeholder = { Text(stringResource(R.string.search)) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = if (searchQuery.text.isEmpty()) {
                    null
                } else {
                    {
                        IconButton({ searchQuery = TextFieldValue() }) {
                            Icon(Icons.Default.Clear, stringResource(R.string.clear))
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { search() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            ToggleField(
                checked = searchPrefs.byTitle,
                onCheckedChange = { onAction(HomeScreenAction.SetByTitle(it)) },
                text = stringResource(R.string.search_title),
                modifier = optionModifier.testTag("byTitle")
            )
            ToggleField(
                checked = searchPrefs.byArtistOrCulture,
                onCheckedChange = { onAction(HomeScreenAction.SetByArtistOrCulture(it)) },
                text = stringResource(R.string.search_artist_culture),
                modifier = optionModifier.testTag("byArtistOrCulture")
            )
            ToggleField(
                checked = searchPrefs.byTags,
                onCheckedChange = { onAction(HomeScreenAction.SetByTags(it)) },
                text = stringResource(R.string.search_tags),
                modifier = optionModifier.testTag("byTags")
            )
            ToggleField(
                checked = searchPrefs.withImage,
                onCheckedChange = { onAction(HomeScreenAction.SetWithImage(it)) },
                text = stringResource(R.string.search_has_images),
                modifier = optionModifier.testTag("withImage")
            )
            ToggleField(
                checked = searchPrefs.isHighlight,
                onCheckedChange = { onAction(HomeScreenAction.SetIsHighlight(it)) },
                text = stringResource(R.string.search_highlight),
                description = stringResource(R.string.search_highlight_desc),
                modifier = optionModifier.testTag("isHighlight")
            )
            DepartmentPicker(
                selectedDepartmentId = searchPrefs.departmentId,
                onSelectDepartment = { onAction(HomeScreenAction.SetDepartmentId(it)) },
                departments = departments,
                modifier = optionModifier.testTag("departmentId")
            )
            PeriodPicker(
                fromYear = searchPrefs.fromYear,
                toYear = searchPrefs.toYear,
                checked = searchPrefs.byDate,
                onPeriodChange = { period ->
                    if (period.first != searchPrefs.fromYear) {
                        onAction(HomeScreenAction.SetFromYear(period.first))
                    }
                    if (period.last != searchPrefs.toYear) {
                        onAction(HomeScreenAction.SetToYear(period.last))
                    }
                },
                onCheckedChange = { onAction(HomeScreenAction.SetByDate(it)) },
                modifier = optionModifier.testTag("byDate")
            )
            TagsPicker(
                title = stringResource(R.string.search_medium),
                description = stringResource(R.string.search_medium_desc),
                tags = searchPrefs.mediumList,
                onTagsChange = { onAction(HomeScreenAction.SetMedium(it)) },
                maxTagLength = MEDIUM_MAX_LENGTH,
                maxTags = MEDIUM_MAX_COUNT,
                modifier = optionModifier.testTag("medium")
            )
            TagsPicker(
                title = stringResource(R.string.search_geolocation),
                description = stringResource(R.string.search_geolocation_desc),
                tags = searchPrefs.geoLocationList,
                onTagsChange = { onAction(HomeScreenAction.SetGeoLocation(it)) },
                maxTagLength = GEO_LOCATION_MAX_LENGTH,
                maxTags = GEO_LOCATION_MAX_COUNT,
                modifier = optionModifier.testTag("geoLocation")
            )
            Spacer(Modifier.height(80.dp))
        }
    }
}

private val optionModifier = Modifier.fillMaxWidth()

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreenContent(
        departments = buildList {
            repeat(10) { i ->
                add(department { id = i; name = "Department $i" })
            }
        },
        searchPrefs = searchPrefs {
            byTags = true
            byDate = true
            fromYear = -500
            toYear = 1500
        },
        onAction = {},
    )
}
