package com.ashipo.metropolitanmuseum.koin

import com.ashipo.metropolitanmuseum.data.network.model.Artwork
import com.ashipo.metropolitanmuseum.di.appModule
import com.ashipo.metropolitanmuseum.ui.artwork.ArtworkScreenModel
import org.junit.jupiter.api.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

class CheckModulesTest : KoinTest {

    @Test
    @OptIn(KoinExperimentalAPI::class)
    fun checkAllModules() {
        appModule.verify(
            injections = injectedParameters(
                definition<ArtworkScreenModel>(Artwork::class),
            )
        )
    }
}
