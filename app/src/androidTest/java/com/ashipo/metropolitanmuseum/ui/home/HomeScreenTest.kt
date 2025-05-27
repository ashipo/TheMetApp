package com.ashipo.metropolitanmuseum.ui.home

import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.ashipo.metropolitanmuseum.department
import com.ashipo.metropolitanmuseum.searchPrefs
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // by title

    @Test
    fun byTitle_isBackedBySearchPrefs() {
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { byTitle = true },
                onAction = {},
            )
        }

        composeTestRule.onNodeWithTag("byTitle").assertIsOn()
    }

    @Test
    fun byTitle_onClick_updatesByTitle() {
        var performedAction: HomeScreenAction? = null
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs {},
                onAction = { performedAction = it },
            )
        }

        composeTestRule.onNodeWithTag("byTitle").apply {
            performScrollTo()
            performClick()
        }

        assertEquals(HomeScreenAction.SetByTitle(true), performedAction)
    }

    // by artist or culture

    @Test
    fun byArtistOrCulture_isBackedBySearchPrefs() {
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { byArtistOrCulture = true },
                onAction = {},
            )
        }

        composeTestRule.onNodeWithTag("byArtistOrCulture").assertIsOn()
    }

    @Test
    fun byArtistOrCulture_onClick_updatesByArtistOrCulture() {
        var performedAction: HomeScreenAction? = null
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs {},
                onAction = { performedAction = it },
            )
        }

        composeTestRule.onNodeWithTag("byArtistOrCulture").apply {
            performScrollTo()
            performClick()
        }

        assertEquals(HomeScreenAction.SetByArtistOrCulture(true), performedAction)
    }

    // by tags

    @Test
    fun byTags_isBackedBySearchPrefs() {
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { byTags = true },
                onAction = {},
            )
        }

        composeTestRule.onNodeWithTag("byTags").assertIsOn()
    }

    @Test
    fun byTags_onClick_updatesByTags() {
        var performedAction: HomeScreenAction? = null
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs {},
                onAction = { performedAction = it },
            )
        }

        composeTestRule.onNodeWithTag("byTags").apply {
            performScrollTo()
            performClick()
        }

        assertEquals(HomeScreenAction.SetByTags(true), performedAction)
    }

    // with image

    @Test
    fun withImage_isBackedBySearchPrefs() {
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { withImage = true },
                onAction = {},
            )
        }

        composeTestRule.onNodeWithTag("withImage").assertIsOn()
    }

    @Test
    fun withImage_onClick_updatesWithImage() {
        var performedAction: HomeScreenAction? = null
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs {},
                onAction = { performedAction = it },
            )
        }

        composeTestRule.onNodeWithTag("withImage").apply {
            performScrollTo()
            performClick()
        }

        assertEquals(HomeScreenAction.SetWithImage(true), performedAction)
    }

    // is highlight

    @Test
    fun isHighlight_isBackedBySearchPrefs() {
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { isHighlight = true },
                onAction = {},
            )
        }

        composeTestRule.onNodeWithTag("isHighlight").assertIsOn()
    }

    @Test
    fun isHighlight_onClick_updatesIsHighlight() {
        var performedAction: HomeScreenAction? = null
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs {},
                onAction = { performedAction = it },
            )
        }

        composeTestRule.onNodeWithTag("isHighlight").apply {
            performScrollTo()
            performClick()
        }

        assertEquals(HomeScreenAction.SetIsHighlight(true), performedAction)
    }

    // Department

    @Test
    fun department_isBackedBySearchPrefs() {
        val departments = listOf(
            department { id = 1; name = "Egyptian" },
            department { id = 2; name = "Chinese" },
            department { id = 3; name = "European" },
        )
        composeTestRule.setContent {
            HomeScreen(
                departments = departments,
                searchPrefs = searchPrefs { departmentId = 2 },
                onAction = {},
            )
        }

        composeTestRule.onNodeWithTag("departmentId").assertTextContains("Chinese")
    }

    @Test
    fun department_onClick_showsDepartmentSelectDialog() {
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs {},
                onAction = {},
            )
        }

        // open dialog
        composeTestRule.apply {
            onNodeWithTag("departmentId").apply {
                performScrollTo()
                performClick()
            }
        }

        composeTestRule.onNodeWithTag("department:none").assertExists()
    }

    @Test
    fun departmentSelectionDialog_onConfirm_updatesDepartment() {
        var performedAction: HomeScreenAction? = null
        val departments = listOf(
            department { id = 1; name = "Egyptian" },
            department { id = 2; name = "Chinese" },
            department { id = 3; name = "European" },
        )
        composeTestRule.setContent {
            HomeScreen(
                departments = departments,
                searchPrefs = searchPrefs {},
                onAction = { performedAction = it },
            )
        }

        composeTestRule.apply {
            // open dialog
            onNodeWithTag("departmentId").apply {
                performScrollTo()
                performClick()
            }
            // select department
            onNodeWithText("European").apply {
                performScrollTo()
                performClick()
            }
            onNodeWithTag("confirm").performClick()
        }

        assertEquals(HomeScreenAction.SetDepartmentId(3), performedAction)
    }

    // byDate

    @Test
    fun byDate_isBackedBySearchPrefs() {
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { byDate = true },
                onAction = {},
            )
        }

        composeTestRule.onNode(hasTestTag("periodPicker:checkbox") and hasParent(hasTestTag("byDate")))
            .assertIsOn()
    }

    @Test
    fun byDate_onClick_showsPeriodSelectDialog() {
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs {},
                onAction = {},
            )
        }
        composeTestRule.onNode(hasTestTag("periodPicker:text") and hasParent(hasTestTag("byDate")))
            .apply {
                performScrollTo()
                performClick()
            }

        composeTestRule.onNodeWithTag("fromYear").assertExists()
    }

    // fromYear

    @Test
    fun fromYear_isBackedBySearchPrefs() {
        val expected = 168

        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { fromYear = expected },
                onAction = {},
            )
        }

        composeTestRule.onNode(hasTestTag("periodPicker:text") and hasParent(hasTestTag("byDate")))
            .assertTextContains("$expected", true)
    }

    @Test
    fun byDateSelectDialog_onConfirm_updatesFromYear() {
        val year = 789
        var performedAction: HomeScreenAction? = null
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs {
                    fromYear = 500
                    toYear = 1000
                },
                onAction = { performedAction = it },
            )
        }

        composeTestRule.apply {
            // open dialog
            onNode(hasTestTag("periodPicker:text") and hasParent(hasTestTag("byDate"))).apply {
                performScrollTo()
                performClick()
            }
            // modify "from year"
            onNode(hasTestTag("year") and hasParent(hasTestTag("fromYear")))
                .performTextReplacement("$year")
            // click OK
            onNodeWithTag("confirm").performClick()
        }

        assertEquals(HomeScreenAction.SetFromYear(year), performedAction)
    }

    // toYear

    @Test
    fun toYear_isBackedBySearchPrefs() {
        val expected = 1689

        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { toYear = expected },
                onAction = {},
            )
        }

        composeTestRule.onNode(hasTestTag("periodPicker:text") and hasParent(hasTestTag("byDate")))
            .assertTextContains("$expected", true)
    }

    @Test
    fun byDateSelectDialog_onConfirm_updatesToYear() {
        val year = 789
        var performedAction: HomeScreenAction? = null
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs {
                    fromYear = 500
                    toYear = 1000
                },
                onAction = { performedAction = it },
            )
        }

        composeTestRule.apply {
            // open dialog
            onNode(hasTestTag("periodPicker:text") and hasParent(hasTestTag("byDate"))).apply {
                performScrollTo()
                performClick()
            }
            // modify "to year"
            onNode(hasTestTag("year") and hasParent(hasTestTag("toYear")))
                .performTextReplacement("$year")
            onNodeWithTag("confirm").performClick()
        }

        assertEquals(HomeScreenAction.SetToYear(year), performedAction)
    }

    // medium

    @Test
    fun medium_isBackedBySearchPrefs() {
        val mediums = listOf("Glass", "Cement")

        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { medium.addAll(mediums) },
                onAction = {},
            )
        }

        composeTestRule.onNodeWithTag("medium").apply {
            for (medium in mediums) {
                assertTextContains(medium, true)
            }
        }
    }

    @Test
    fun mediumSelectDialog_onConfirm_updatesMedium() {
        val mediums = listOf("Glass", "Cement")
        var performedAction: HomeScreenAction? = null
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { medium.addAll(mediums) },
                onAction = { performedAction = it },
            )
        }

        composeTestRule.apply {
            // open dialog
            onNodeWithTag("medium").apply {
                performScrollTo()
                performClick()
            }
            // remove a tag
            onNodeWithText("Glass").performClick()
        }

        assertEquals(HomeScreenAction.SetMedium(listOf("Cement")), performedAction)
    }

    // geo location

    @Test
    fun geoLocation_isBackedBySearchPrefs() {
        val locations = listOf("Island", "City")

        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { geoLocation.addAll(locations) },
                onAction = {},
            )
        }

        composeTestRule.onNodeWithTag("geoLocation").apply {
            for (location in locations) {
                assertTextContains(location, true)
            }
        }
    }

    @Test
    fun geoLocationSelectDialog_onConfirm_updatesGeoLocation() {
        val locations = listOf("Island", "City")
        var performedAction: HomeScreenAction? = null
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs { geoLocation.addAll(locations) },
                onAction = { performedAction = it },
            )
        }

        composeTestRule.apply {
            // open dialog
            onNodeWithTag("geoLocation").apply {
                performScrollTo()
                performClick()
            }
            // remove a tag
            onNodeWithText("Island").performClick()
        }

        assertEquals(HomeScreenAction.SetGeoLocation(listOf("City")), performedAction)
    }

    // search button

    @Test
    fun searchButton_onClick_executesSearchAction() {
        var performedAction: HomeScreenAction? = null
        composeTestRule.setContent {
            HomeScreen(
                departments = emptyList(),
                searchPrefs = searchPrefs {},
                onAction = { performedAction = it },
            )
        }

        composeTestRule.onNodeWithText("Search").performTextInput("Giraffe")
        composeTestRule.onNodeWithTag("searchButton").performClick()

        assertEquals(HomeScreenAction.Search("Giraffe"), performedAction)
    }
}
