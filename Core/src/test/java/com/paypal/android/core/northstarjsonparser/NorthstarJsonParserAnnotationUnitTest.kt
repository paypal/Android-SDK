package com.paypal.android.core.northstarjsonparser

import com.paypal.android.core.json.JsonName
import com.paypal.android.core.json.NorthstarJsonParser
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class NorthstarJsonParserAnnotationUnitTest {

    @Test
    fun `annotation overrides parameter name` () {
        val mockValue = "mock value"
        val json = """
            {
              "annotated_param": $mockValue
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), AnnotatedParamObject::class)
        assertEquals(sut?.param, mockValue)
    }

    @Test
    fun `it should throw exception if annotation is not found` () {
        val mockValue = "mock value"
        val json = """
            {
              "param": $mockValue
            }
        """.trimIndent()

        val exception = assertThrows(JSONException::class.java) {
            NorthstarJsonParser().fromJson(JSONObject(json), AnnotatedParamObject::class)
        }
        assertEquals("No value for annotated_param", exception.message)
    }

    @Test
    fun `it should map camel case parameter to snake case key in json` () {
        val mockValue = "mock value"
        val json = """
            {
              "camel_case_parameter": $mockValue
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), CamelCaseParameter::class)
        assertEquals(sut?.camelCaseParameter, mockValue)
    }

    @Test
    fun `it should look for chained parameters down in json tree` () {
        val mockValue = "mock value"
        val json = """
            {
              "first_level": {
                "second_level": {
                    "simple_string": $mockValue,
                    "simple_object": {
                        "annotated_param": $mockValue
                    },
                    "simple_list": [
                        $mockValue,
                        $mockValue,
                        $mockValue
                    ]
                }
              }
            }
        """.trimIndent()

        val expectedObject = ChainedParameterObject(
            mockValue,
            AnnotatedParamObject(mockValue),
            listOf(mockValue, mockValue, mockValue)
        )

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), ChainedParameterObject::class)
        assertEquals(sut, expectedObject)
    }
}

data class AnnotatedParamObject(@JsonName("annotated_param") val param: String)

data class CamelCaseParameter(val camelCaseParameter: String)

data class ChainedParameterObject(
    @JsonName("first_level.second_level.simple_string") val chainedString: String,
    @JsonName("first_level.second_level.simple_object") val chainedObject: AnnotatedParamObject,
    @JsonName("first_level.second_level.simple_list") val chainedList: List<String>
)
