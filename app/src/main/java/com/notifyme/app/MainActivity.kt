package com.notifyme.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.notifyme.app.ui.navigation.NavGraph
import com.notifyme.app.ui.theme.NotifyMeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotifyMeTheme {
                NavGraph()
            }
        }
    }
}
