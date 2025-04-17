package com.ashipo.metropolitanmuseum.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.ashipo.metropolitanmuseum.SearchPrefs
import com.ashipo.metropolitanmuseum.searchPrefs
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SearchPrefsSerializer : Serializer<SearchPrefs> {

    override val defaultValue = searchPrefs {
        departmentId = -1
        fromYear = 1700
        toYear = 1800
    }

    override suspend fun readFrom(input: InputStream): SearchPrefs = try {
        SearchPrefs.parseFrom(input)
    } catch (e: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read Search preferences", e)
    }

    override suspend fun writeTo(t: SearchPrefs, output: OutputStream) {
        t.writeTo(output)
    }
}
