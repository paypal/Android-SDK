package com.paypal.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.paypal.android.core.Http
import com.paypal.android.core.HttpRequest
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paypal.android.ui.CardFields
import com.paypal.android.ui.theme.DemoTheme

class DemoActivity : AppCompatActivity() {

    val http = Http()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        lifecycleScope.launch {
            val request = HttpRequest("https://www.google.com")
            val result = http.send(request)
            print(result.responseCode)
        }

        setContent {
            DemoTheme {
                Column {
                    CardFields(Modifier.padding(16.dp))

                    Button(
                        onClick = {

                        },
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .fillMaxWidth()
                    ) { Text("Submit") }
                }
            }
        }
    }
}
