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
    fun `it should strings to correct enum type`() {
        val one = "ONE"
        val expectedValue = SimpleEnumObject(TestEnum.ONE)

        val json = """
            {
              "enum": $one
            }
        """.trimIndent()

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), SimpleEnumObject::class)

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
                        ${TestEnum.ONE},
                        ${TestEnum.TWO},
                    ],
                    [
                        ${TestEnum.ONE},
                        ${TestEnum.TWO},
                        ${TestEnum.THREE},
                    ],
                    [
                        ${TestEnum.ONE},
                        ${TestEnum.TWO},
                        ${TestEnum.THREE},
                        ${TestEnum.FOUR},
                    ],
                ],
                "empty_list": [],
                "simple_object_in_json": {
                    "value_object": {
                        "value_string": $mockValueString
                    }
                },
                "first_level": {
                    "second_level": {
                        "simple_string": $mockValueString,
                        "multi_param_object": {
                            "param1": $mockMultiParamValue,
                            "param2": $mockMultiParamValue,
                            "param3": $mockMultiParamValue,    
                        },
                        "simple_list": [
                            $mockValueString,
                            $mockValueString,
                            $mockValueString
                        ]
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
                    TestEnum.ONE,
                    TestEnum.TWO,
                ),
                listOf(
                    TestEnum.ONE,
                    TestEnum.TWO,
                    TestEnum.THREE
                ),
                listOf(
                    TestEnum.ONE,
                    TestEnum.TWO,
                    TestEnum.THREE,
                    TestEnum.FOUR
                ),
            ),
            emptyList = listOf(),
            chainedString = mockValueString,
            chainedObject = MultiParamObject(mockMultiParamValue, mockMultiParamValue, mockMultiParamValue),
            chainedList = listOf(mockValueString, mockValueString, mockValueString)
        )

        val sut = NorthstarJsonParser().fromJson(JSONObject(json), ComplexObject::class)
        assertEquals(sut, expectedObject)
    }

}

data class StringParamObject(val valueString: String)

data class SimpleObjectParam(val valueObject: StringParamObject)

data class SimpleEnumObject(val enum: TestEnum)

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
    val listOfList: List<List<TestEnum>>,
    val emptyList: List<StringParamObject>,
    @JsonName("first_level.second_level.simple_string") val chainedString: String,
    @JsonName("first_level.second_level.multi_param_object") val chainedObject: MultiParamObject,
    @JsonName("first_level.second_level.simple_list") val chainedList: List<String>,
)

enum class TestEnum {
    ONE, TWO, THREE, FOUR
}
