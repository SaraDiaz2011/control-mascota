package com.upb.controlmascota.salud

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.upb.controlmascota.data.TipoRegistro
import com.upb.controlmascota.util.FechaVisualTransformation
import com.upb.controlmascota.util.Fechas

@Composable
fun FormRegistroScreen(
    viewModel: SaludViewModel,
    mascotaId: Long,
    registroId: Long?,         // null = registro nuevo
    alTerminar: () -> Unit
) {
    var tipo by rememberSaveable { mutableStateOf(TipoRegistro.VACUNA) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var fechaDigitos by rememberSaveable { mutableStateOf("") }
    var proximaDigitos by rememberSaveable { mutableStateOf("") }
    var notas by rememberSaveable { mutableStateOf("") }
    var cargado by rememberSaveable { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }
    var confirmarEliminar by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Al editar, carga los datos actuales una sola vez
    LaunchedEffect(registroId) {
        if (registroId != null && !cargado) {
            viewModel.obtener(registroId)?.let {
                tipo = it.tipo
                nombre = it.nombre
                fechaDigitos = Fechas.aDigitos(it.fechaAplicada)
                proximaDigitos = it.proximaFecha?.let { f -> Fechas.aDigitos(f) } ?: ""
                notas = it.notas
            }
            cargado = true
        }
    }

    val esCita = tipo == TipoRegistro.CITA

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                if (registroId == null) "Nuevo registro de salud" else "Editar registro",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text("Tipo", style = MaterialTheme.typography.labelLarge)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                listOf(
                    TipoRegistro.VACUNA,
                    TipoRegistro.DESPARASITACION,
                    TipoRegistro.CITA
                ).forEach { opcion ->
                    FilterChip(
                        selected = tipo == opcion,
                        onClick = { tipo = opcion; error = null },
                        label = { Text(textoTipo(opcion)) }
                    )
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; error = null },
                label = {
                    Text(
                        when (tipo) {
                            TipoRegistro.VACUNA -> "Nombre de la vacuna"
                            TipoRegistro.DESPARASITACION -> "Producto"
                            else -> "Motivo de la cita"
                        }
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            CampoFecha(
                valor = fechaDigitos,
                alCambiar = { fechaDigitos = it; error = null },
                etiqueta = if (esCita) "Fecha de la cita" else "Fecha de aplicación"
            )

            CampoFecha(
                valor = proximaDigitos,
                alCambiar = { proximaDigitos = it; error = null },
                etiqueta = if (esCita) "Próxima cita (opcional)" else "Próxima dosis (opcional)"
            )

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = alTerminar,
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Cancelar") }

                Button(
                    enabled = !guardando,
                    onClick = {
                        val aplicada = Fechas.desdeDigitos(fechaDigitos)
                        val proxima =
                            if (proximaDigitos.isBlank()) null else Fechas.desdeDigitos(proximaDigitos)
                        error = when {
                            nombre.isBlank() -> "Escribe el nombre del registro"
                            aplicada == null -> "La fecha no es válida (usa dd/mm/aaaa)"
                            !esCita && aplicada > Fechas.hoy() ->
                                "La fecha de aplicación no puede ser futura"
                            proximaDigitos.isNotBlank() && proxima == null ->
                                "La próxima fecha no es válida (usa dd/mm/aaaa)"
                            proxima != null && proxima <= aplicada ->
                                "La próxima fecha debe ser posterior a la primera"
                            else -> null
                        }
                        if (error == null && aplicada != null) {
                            guardando = true
                            viewModel.guardar(
                                id = registroId,
                                mascotaId = mascotaId,
                                tipo = tipo,
                                nombre = nombre,
                                fechaAplicada = aplicada,
                                proximaFecha = proxima,
                                notas = notas,
                                alTerminar = alTerminar
                            )
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Guardar") }
            }

            // Eliminar solo aparece al editar
            if (registroId != null) {
                if (!confirmarEliminar) {
                    OutlinedButton(
                        onClick = { confirmarEliminar = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Eliminar registro") }
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
                            Text("¿Eliminar este registro?", fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { confirmarEliminar = false },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Cancelar") }
                                Button(
                                    enabled = !guardando,
                                    onClick = {
                                        guardando = true
                                        viewModel.eliminar(registroId, alTerminar)
                                    },
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

@Composable
private fun CampoFecha(valor: String, alCambiar: (String) -> Unit, etiqueta: String) {
    OutlinedTextField(
        value = valor,
        onValueChange = { nuevo -> alCambiar(nuevo.filter { it.isDigit() }.take(8)) },
        label = { Text(etiqueta) },
        placeholder = { Text("dd/mm/aaaa") },
        singleLine = true,
        visualTransformation = FechaVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}
