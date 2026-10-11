package com.upb.controlmascota.salud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Sección "Historial de salud" del detalle de mascota.
 * STUB: la implementa quien tenga feature/vacunas.
 * Usa Column (no LazyColumn), porque vive dentro de una pantalla con scroll.
 */
@Composable
fun HistorialSalud(mascotaId: Long, alAgregar: () -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Historial de salud",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(onClick = alAgregar) { Text("+ Agregar") }
        }
        Text(
            "Aquí irá el historial de vacunas (pendiente).",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}