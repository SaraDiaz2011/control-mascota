package com.upb.controlmascota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.upb.controlmascota.navigation.AppNavHost
import com.upb.controlmascota.recordatorios.Recordatorios
import com.upb.controlmascota.ui.theme.ControlMascotaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            )
        )
        Recordatorios.programar(this)
        setContent {
            ControlMascotaTheme {
                AppNavHost()
            }
        }
    }
}
