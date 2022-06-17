package com.paypal.android.core.northstarjsonparser

import com.paypal.android.core.json.JsonName
import com.paypal.android.core.json.NorthstarJsonParser
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class NorthstarJsonParserObjectsUnitTest {

    @Test
    fun `it should map json string to string`() {
        val mockValue = "mock value"

        val expectedValue = StringParamObject(valueString = mockValue)

        val json = """
            {
              "value_string": $mockValue
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), StringParamObject::class)
        assertEquals(expectedValue, sut)
    }

    @Test
    fun `it should map json object to given type`() {
        val mockValue = "mock value"

        val expectedValue =
            SimpleObjectParam(valueObject = StringParamObject(valueString = mockValue))

        val json = """
            {
              "value_object":
                {
                   "value_string": $mockValue
                }
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), SimpleObjectParam::class)
        assertEquals(expectedValue, sut)
    }

    @Test
    fun `it should map json array of strings list of strings`() {
        val firstItemValue = "1st value"
        val secondItemValue = "2nd value"
        val thirdItemValue = "3rd value"

        val list = listOf(firstItemValue, secondItemValue, thirdItemValue)

        val expectedValue = ListStringParamObject(valueList = list)

        val json = """
            {
              "value_list": [
                $firstItemValue,
                $secondItemValue,
                $thirdItemValue
              ]
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), ListStringParamObject::class)
        assertEquals(expectedValue, sut)
    }

    @Test
    fun `it should map json array of type list of same type`() {
        val firstValue = "1st value"
        val secondValue = "2nd value"
        val thirdValue = "3rd value"

        val firstItem = StringParamObject(firstValue)
        val secondItem = StringParamObject(secondValue)
        val thirdItem = StringParamObject(thirdValue)

        val list = listOf(firstItem, secondItem, thirdItem)

        val expectedValue = ListOfTypesParamObject(valueList = list)

        val json = """
            {
              "value_list": [
                {
                  "value_string": $firstValue
                },
                {
                  "value_string": $secondValue
                },
                {
                  "value_string": $thirdValue
                }
              ]
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), ListOfTypesParamObject::class)

        assertEquals(expectedValue, sut)
    }

    @Test
    fun `given an object with objects, string and list, it should return the same values`() {
        val mockAnnotatedParamValue = "mock Value 1"
        val mockMultiParamValue = "mock Value 2"
        val mockValueString = "mock Value 3"

        val json = """
            {
                "annotated_param": $mockAnnotatedParamValue,
                "list_of_complex_items": [
                    {
                        "multi_param_object": {
                            "param1": $mockMultiParamValue,
                            "param2": $mockMultiParamValue,
                            "param3": $mockMultiParamValue,    
                        }
                    },
                    {
                        "multi_param_object": {
                            "param1": $mockMultiParamValue,
                            "param2": $mockMultiParamValue,
                            "param3": $mockMultiParamValue,    
                        }
                    },
                    {
                        "multi_param_object": {
                            "param1": $mockMultiParamValue,
                            "param2": $mockMultiParamValue,
                            "param3": $mockMultiParamValue,    
                        }
                    },
                ],
                "multi_param_object": {
                    "param1": $mockMultiParamValue,
                    "param2": $mockMultiParamValue,
                    "param3": $mockMultiParamValue,    
                },
                "list_of_list": [
                    [
                        { "value_string": $mockValueString },
                        { "value_string": $mockValueString },
                    ],
                    [
                        { "value_string": $mockValueString },
                        { "value_string": $mockValueString },
                        { "value_string": $mockValueString },
                    ],
                    [
                        { "value_string": $mockValueString },
                        { "value_string": $mockValueString },
                        { "value_string": $mockValueString },
                        { "value_string": $mockValueString },
                    ],
                ],
                "empty_list": [],
                "simple_object_in_json": {
                    "value_object": {
                        "value_string": $mockValueString
                    }
                }
            }
        """.trimIndent()

        val expectedObject = ComplexObject(
            simpleValue = mockAnnotatedParamValue,
            multiParamObject = MultiParamObject(
                mockMultiParamValue,
                mockMultiParamValue,
                mockMultiParamValue
            ),
            simpleObject = SimpleObjectParam(StringParamObject(mockValueString)),
            listOfItems = listOf(
                MultiParamObjectWrapper(MultiParamObject(mockMultiParamValue, mockMultiParamValue, mockMultiParamValue)),
                MultiParamObjectWrapper(MultiParamObject(mockMultiParamValue, mockMultiParamValue, mockMultiParamValue)),
                MultiParamObjectWrapper(MultiParamObject(mockMultiParamValue, mockMultiParamValue, mockMultiParamValue))
            ),
            listOfList = listOf(
                listOf(
                    StringParamObject(mockValueString),
                    StringParamObject(mockValueString),
                ),
                listOf(
                    StringParamObject(mockValueString),
                    StringParamObject(mockValueString),
                    StringParamObject(mockValueString)
                ),
                listOf(
                    StringParamObject(mockValueString),
                    StringParamObject(mockValueString),
                    StringParamObject(mockValueString),
                    StringParamObject(mockValueString)
                ),
            ),
            emptyList = listOf()
        )

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), ComplexObject::class)
        assertEquals(sut, expectedObject)
    }

}

data class StringParamObject(val valueString: String)

data class SimpleObjectParam(val valueObject: StringParamObject)

data class ListStringParamObject(val valueList: List<String>)

data class ListOfTypesParamObject(val valueList: List<StringParamObject>)

data class MultiParamObject(
    val param1: String,
    val param2: String,
    val param3: String,
)

data class MultiParamObjectWrapper(
    @JsonName("multi_param_object") val wrapped: MultiParamObject
)

data class ComplexObject(
    @JsonName("annotated_param") val simpleValue: String,
    val multiParamObject: MultiParamObject,
    @JsonName("simple_object_in_json") val simpleObject: SimpleObjectParam,
    @JsonName("object_not_in_json") val simpleObjectAbsent: SimpleObjectParam? = SimpleObjectParam(
        StringParamObject("default")
    ),
    @JsonName("list_of_complex_items") val listOfItems: List<MultiParamObjectWrapper>,
    val listOfList: List<List<StringParamObject>>,
    val emptyList: List<StringParamObject>
)
