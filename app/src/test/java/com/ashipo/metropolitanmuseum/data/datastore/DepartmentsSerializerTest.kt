package com.ashipo.metropolitanmuseum.data.datastore

import androidx.datastore.core.CorruptionException
import com.ashipo.metropolitanmuseum.department
import com.ashipo.metropolitanmuseum.departments
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertContains
import kotlin.test.assertEquals

class DepartmentsSerializerTest {

    @Test
    fun `default departments contain correct values`() {
        assertContains(
            DepartmentsSerializer.defaultValue.departmentsList,
            department { id = 1; name = "American Decorative Arts" },
        )
        assertContains(
            DepartmentsSerializer.defaultValue.departmentsList,
            department { id = 13; name = "Greek and Roman Art" },
        )
    }

    @Test
    fun `reading invalid data throws CorruptionException`() = runTest {
        assertThrows<CorruptionException> {
            DepartmentsSerializer.readFrom(ByteArrayInputStream(byteArrayOf(0)))
        }
    }

    @Test
    fun `writing and reading last updated outputs correct value`() = runTest {
        val expectedDepartments = departments {
            departments.add(
                department { id = 15; name = "Romantic Expressionism" }
            )
            departments.add(
                department { id = 23; name = "Modern Warfare" }
            )
        }

        val outputStream = ByteArrayOutputStream()
        expectedDepartments.writeTo(outputStream)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        val actualDepartments = DepartmentsSerializer.readFrom(inputStream)

        assertEquals(
            expectedDepartments,
            actualDepartments,
        )
    }
}
