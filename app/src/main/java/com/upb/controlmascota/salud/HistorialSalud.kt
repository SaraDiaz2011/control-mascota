package com.upb.controlmascota.salud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upb.controlmascota.data.RegistroSalud
import com.upb.controlmascota.data.TipoRegistro
import com.upb.controlmascota.util.Fechas

/**
 * Sección "Historial de salud" del detalle de mascota.
 * Usa Column (no LazyColumn), porque vive dentro de una pantalla con scroll.
 * Tocar un registro lo abre para editarlo o eliminarlo.
 */
@Composable
fun HistorialSalud(
    mascotaId: Long,
    alAgregar: () -> Unit,
    alEditar: (registroId: Long) -> Unit = {}
) {
    val viewModel: SaludViewModel = viewModel()
    val flujo = remember(mascotaId) { viewModel.listar(mascotaId) }
    val registros by flujo.collectAsStateWithLifecycle(initialValue = emptyList())

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

        if (registros.isEmpty()) {
            Text(
                "Todavía no hay vacunas, desparasitaciones ni citas registradas.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            registros.forEach { registro ->
                TarjetaRegistro(registro, alTocar = { alEditar(registro.id) })
            }
        }
    }
}

@Composable
private fun TarjetaRegistro(registro: RegistroSalud, alTocar: () -> Unit) {
    val hoy = Fechas.hoy()
    val vencido = registro.proximaFecha != null && registro.proximaFecha < hoy

    Card(onClick = alTocar, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    registro.nombre,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                EtiquetaTipo(registro.tipo)
            }
            Text(
                "${if (registro.tipo == TipoRegistro.CITA) "Fecha" else "Aplicada"}: " +
                        Fechas.formatear(registro.fechaAplicada),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            registro.proximaFecha?.let {
                Text(
                    "Próxima: ${Fechas.formatear(it)}" + if (vencido) " (vencida)" else "",
                    color = if (vencido) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            if (registro.notas.isNotBlank()) {
                Text(registro.notas, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun EtiquetaTipo(tipo: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(
            textoTipo(tipo),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
