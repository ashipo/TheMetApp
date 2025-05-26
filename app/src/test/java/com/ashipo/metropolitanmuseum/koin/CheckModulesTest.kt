package com.ashipo.metropolitanmuseum.koin

import androidx.lifecycle.SavedStateHandle
import com.ashipo.metropolitanmuseum.di.appModule
import org.junit.jupiter.api.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class CheckModulesTest : KoinTest {

    @Test
    @OptIn(KoinExperimentalAPI::class)
    fun checkAllModules() {
        appModule.verify(
            extraTypes = listOf(SavedStateHandle::class),
        )
    }
}
