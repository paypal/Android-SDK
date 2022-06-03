package com.paypal.android.core.northstarjsonparser

import com.paypal.android.core.json.NorthstarJsonParser
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class NorthstarJsonParserUnitTest {

    @Test
    fun `it should return param with correct value for mandatory param` () {
        val mockValue = "mock value"
        val json = """
            {
              "param": $mockValue
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), MandatoryParamObject::class)
        assertEquals(sut?.param, mockValue)
    }

    @Test
    fun `if mandatory param is not found, throw exception`() {
        val mockValue = "mock value"
        val json = """
            {
              "not_found": $mockValue
            }
        """.trimIndent()

        val exception = assertThrows(JSONException::class.java) {
            NorthstarJsonParser().fromJson(JSONObject(json), MandatoryParamObject::class)
        }

        assertEquals(exception.message, "No value for param")
    }

    @Test
    fun `it should return param with correct value for nullable param` () {
        val mockValue = "mock value"
        val json = """
            {
              "nullable_param": $mockValue
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), NullableParamObject::class)
        assertEquals(sut?.nullableParam, mockValue)
    }

    @Test
    fun `it should return param with null if value not found for nullable param` () {
        val mockValue = "mock value"
        val json = """
            {
              "not_found": $mockValue
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), NullableParamObject::class)
        assertNull(sut?.nullableParam)
    }

    @Test
    fun `it should return param with correct value for optional param` () {
        val mockValue = "mock value"
        val json = """
            {
              "optional_param": $mockValue
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), OptionalParamObject::class)
        assertEquals(sut?.optionalParam, mockValue)
    }

    @Test
    fun `it should return param with default value if value not found for optional param` () {
        val mockValue = "mock value"
        val json = """
            {
              "not_found": $mockValue
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), OptionalParamObject::class)
        assertEquals(sut?.optionalParam, "default")
    }

    @Test
    fun `it should return param with correct value for optional nullable param` () {
        val mockValue = "mock value"
        val json = """
            {
              "null_and_optional_param": $mockValue
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), OptionalNullableParamObject::class)
        assertEquals(sut?.nullAndOptionalParam, mockValue)
    }

    @Test
    fun `it should return param with default value if value not found for optional nullable param` () {
        val mockValue = "mock value"
        val json = """
            {
              "not_found": $mockValue
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), OptionalNullableParamObject::class)
        assertNull(sut?.nullAndOptionalParam)
    }
}

data class MandatoryParamObject(val param: String)

data class NullableParamObject(val nullableParam: String?)

data class OptionalParamObject(val optionalParam: String = "default")

data class OptionalNullableParamObject(val nullAndOptionalParam: String? = null)
