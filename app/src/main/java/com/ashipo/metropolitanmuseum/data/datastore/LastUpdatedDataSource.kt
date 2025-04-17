package com.ashipo.metropolitanmuseum.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.ashipo.metropolitanmuseum.LastUpdated
import com.ashipo.metropolitanmuseum.copy
import com.ashipo.metropolitanmuseum.data.toLocalDate
import com.ashipo.metropolitanmuseum.data.toTimestamp
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.time.LocalDate

class LastUpdatedDataSource(
    private val dataStore: DataStore<LastUpdated>,
) {

    /**
     * Returns the last time when the list of departments was updated
     *
     * @return last update time or null if it was never updated
     */
    suspend fun getDepartmentsLastUpdate() =
        dataStore.data.first().departments.toLocalDate()

    suspend fun setDepartmentsLastUpdate(date: LocalDate) {
        try {
            dataStore.updateData { current ->
                current.copy {
                    departments = date.toTimestamp()
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to set departments last update", e)
        }
    }

    companion object {
        const val TAG = "LastUpdatedDataSource"
    }
}