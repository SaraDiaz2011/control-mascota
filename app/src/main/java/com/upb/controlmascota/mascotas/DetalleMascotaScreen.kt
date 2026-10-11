package com.upb.controlmascota.mascotas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upb.controlmascota.salud.HistorialSalud
import com.upb.controlmascota.util.Fechas
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults

@Composable
fun DetalleMascotaScreen(
    viewModel: MascotasViewModel,
    mascotaId: Long,
    alVolver: () -> Unit,
    alEditar: () -> Unit,
    alAgregarRegistro: () -> Unit
) {
    val flujo = remember(mascotaId) { viewModel.observar(mascotaId) }
    val mascota by flujo.collectAsStateWithLifecycle(initialValue = null)
    var confirmarEliminar by remember { mutableStateOf(false) }
    val m = mascota

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = alVolver) { Text("← Volver") }
                Spacer(Modifier.weight(1f))
                if (m != null) TextButton(onClick = alEditar) { Text("Editar") }
            }

            if (m == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando…")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AvatarMascota(m.nombre, 76.dp)
                        Column {
                            Text(
                                m.nombre,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                listOfNotNull(m.especie, m.raza.ifBlank { null })
                                    .joinToString(" · "),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilaDato("Nacimiento", m.fechaNacimiento?.let { Fechas.formatear(it) } ?: "—")
                            FilaDato("Edad", m.fechaNacimiento?.let { Fechas.edad(it) } ?: "—")
                            FilaDato("Peso", m.pesoKg?.let { textoPeso(it) } ?: "—")
                        }
                    }

                    HistorialSalud(mascotaId = m.id, alAgregar = alAgregarRegistro)

                    if (!confirmarEliminar) {
                        OutlinedButton(
                            onClick = { confirmarEliminar = true },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Eliminar mascota") }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("¿Eliminar a ${m.nombre}?", fontWeight = FontWeight.Bold)
                                Text("También se borrarán sus vacunas y registros de salud.")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { confirmarEliminar = false },
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Cancelar") }
                                    Button(
                                        onClick = { viewModel.eliminar(m, alVolver) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Eliminar") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, fontWeight = FontWeight.Bold)
    }
}

private fun textoPeso(kg: Double): String =
    if (kg % 1.0 == 0.0) "${kg.toInt()} kg" else "$kg kg"