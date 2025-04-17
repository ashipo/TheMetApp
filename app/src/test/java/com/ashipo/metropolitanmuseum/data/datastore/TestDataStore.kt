package com.ashipo.metropolitanmuseum.data.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet

class TestDataStore<T>(initialValue: T) : DataStore<T> {
    override val data = MutableStateFlow(initialValue)

    override suspend fun updateData(
        transform: suspend (it: T) -> T,
    ) = data.updateAndGet { transform(it) }
}
