package com.ashipo.metropolitanmuseum.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.ashipo.metropolitanmuseum.Departments
import com.ashipo.metropolitanmuseum.department
import com.ashipo.metropolitanmuseum.departments
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object DepartmentsSerializer : Serializer<Departments> {

    override val defaultValue = departments {
        departments.addAll(
            listOf(
                department {
                    id = 1
                    name = "American Decorative Arts"
                },
                department {
                    id = 3
                    name = "Ancient Near Eastern Art"
                },
                department {
                    id = 4
                    name = "Arms and Armor"
                },
                department {
                    id = 5
                    name = "Arts of Africa, Oceania, and the Americas"
                },
                department {
                    id = 6
                    name = "Asian Art"
                },
                department {
                    id = 7
                    name = "The Cloisters"
                },
                department {
                    id = 8
                    name = "The Costume Institute"
                },
                department {
                    id = 9
                    name = "Drawings and Prints"
                },
                department {
                    id = 10
                    name = "Egyptian Art"
                },
                department {
                    id = 11
                    name = "European Paintings"
                },
                department {
                    id = 12
                    name = "European Sculpture and Decorative Arts"
                },
                department {
                    id = 13
                    name = "Greek and Roman Art"
                },
                department {
                    id = 14
                    name = "Islamic Art"
                },
                department {
                    id = 15
                    name = "The Robert Lehman Collection"
                },
                department {
                    id = 16
                    name = "The Libraries"
                },
                department {
                    id = 17
                    name = "Medieval Art"
                },
                department {
                    id = 18
                    name = "Musical Instruments"
                },
                department {
                    id = 19
                    name = "Photographs"
                },
                department {
                    id = 21
                    name = "Modern Art"
                },
            )
        )
    }

    override suspend fun readFrom(input: InputStream): Departments = try {
        Departments.parseFrom(input)
    } catch (e: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read Departments", e)
    }

    override suspend fun writeTo(t: Departments, output: OutputStream) {
        t.writeTo(output)
    }
}
