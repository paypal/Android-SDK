package com.paypal.android.core.json

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

class NorthstarJsonParser {

    private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
    private val snakeRegex = "_[a-zA-Z]".toRegex()

    fun <T : Any> fromJson(jsonObject: JSONObject, clazz: KClass<T>): T? {
        val constructor = clazz.primaryConstructor
        val parameterMap = mutableMapOf<KParameter, Any?>()
        constructor?.parameters?.forEach { parameter ->
            // we look for annotation, if it doesn't exist, we turn param name to snake case.
            val annotation = parameter.annotations.firstOrNull { it is JsonName } as JsonName?
            val keyPath = annotation?.name ?: parameter.name?.camelToSnakeCase() ?: ""

            val keys = keyPath.split(".").toMutableList()
            var node = jsonObject
            while (keys.size > 1) {
                node = node.getJSONObject(keys[0])
                keys.removeFirst()
            }
            val key = keys[0]
            //if param is string, we look for value, if not, we recursively go down the json tree
            val value = getValueFromParameter(node, key, parameter)
            if (value != null || !parameter.isOptional) {
                parameterMap[parameter] = value
            }
        }
        return constructor?.callBy(parameterMap)
    }

    private fun getValueFromParameter(jsonObject: JSONObject, key: String?, parameter: KParameter) = when (parameter.type.classifier as KClass<*>) {
        String::class -> {
            checkParameterForValue(key, parameter) {
                jsonObject.optOrNullString(key)
            }
        }
        List::class -> {
            checkParameterForValue(key, parameter) {
                val optJsonArray = jsonObject.optJSONArray(key)
                optJsonArray?.let {
                    parseJsonArrayToList(
                        it,
                        parameter.type.arguments[0].type!!
                    )
                }
            }
        }
        else -> {
            checkParameterForValue(key, parameter) {
                val optJsonObject = jsonObject.optJSONObject(key)
                optJsonObject?.let {
                    fromJson(
                        it,
                        parameter.type.classifier!! as KClass<*>
                    )
                }
            }
        }
    }

    private fun parseJsonArrayToList(jsonArray: JSONArray, type: KType): List<Any?> {
        val list = mutableListOf<Any?>()
        for (i in 0 until jsonArray.length()) {
            val item = when (type.classifier) {
                String::class -> {
                    jsonArray.getString(i)
                }
                List::class -> {
                    parseJsonArrayToList(
                        jsonArray.getJSONArray(i),
                        type.arguments[0].type!!
                    )
                }
                else -> {
                    fromJson(jsonArray.getJSONObject(i), type.classifier as KClass<*>)
                }
            }
            list.add(item)
        }
        return list
    }

    private fun <T> checkParameterForValue(
        key: String?,
        parameter: KParameter,
        getValue: () -> T?
    ): T? {
        val isOptional = parameter.isOptional
        val isNullable = parameter.type.isMarkedNullable
        val value = getValue()
        if (!isOptional && !isNullable && value == null) {
            throw JSONException("No value for $key")
        }
        return value
    }

    // String extensions
    private fun String.camelToSnakeCase(): String {
        return camelRegex.replace(this) {
            "_${it.value}"
        }.lowercase(Locale.getDefault())
    }

    private fun String.snakeToLowerCamelCase(): String {
        return snakeRegex.replace(this) {
            it.value.replace("_", "")
                .uppercase(Locale.getDefault())
        }
    }

    fun String.snakeToUpperCamelCase(): String {
        return this.snakeToLowerCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    private fun JSONObject.optOrNullString(key: String?): String? =
        if (this.has(key) && !this.isNull(key)) this.optString(key) else null
}