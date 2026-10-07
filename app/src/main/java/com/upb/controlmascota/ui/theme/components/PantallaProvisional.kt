package com.upb.controlmascota.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Pantalla de relleno: muestra el título y botones para probar la navegación. */
@Composable
fun PantallaProvisional(
    titulo: String,
    enlaces: List<Pair<String, () -> Unit>> = emptyList()
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = titulo, style = MaterialTheme.typography.headlineMedium)
            enlaces.forEach { (texto, accion) ->
                Button(onClick = accion, modifier = Modifier.fillMaxWidth()) {
                    Text(texto)
                }
            }
        }
    }
}