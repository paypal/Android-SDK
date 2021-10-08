package com.paypal.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun DemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        content = content
    )

//    val systemUiController = rememberSystemUiController()
//    if (darkTheme) {
//        systemUiController.setSystemBarsColor(
//            color = MaterialTheme.colors.secondary
//        )
//    } else {
//        systemUiController.setSystemBarsColor(
//            color = MaterialTheme.colors.secondary
//        )
//    }
}
