package com.paypal.android.core.json

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class JsonName(val name: String)