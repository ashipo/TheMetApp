package com.ashipo.metropolitanmuseum.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.ashipo.metropolitanmuseum.LastUpdated
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object LastUpdatedSerializer : Serializer<LastUpdated> {

    override val defaultValue: LastUpdated = LastUpdated.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): LastUpdated = try {
        LastUpdated.parseFrom(input)
    } catch (e: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read Last updated", e)
    }

    override suspend fun writeTo(t: LastUpdated, output: OutputStream) {
        t.writeTo(output)
    }
}
