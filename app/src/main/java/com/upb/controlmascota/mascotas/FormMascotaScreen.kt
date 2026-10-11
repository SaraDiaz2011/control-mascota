package com.upb.controlmascota.mascotas

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
import com.upb.controlmascota.util.FechaVisualTransformation
import com.upb.controlmascota.util.Fechas

@Composable
fun FormMascotaScreen(
    viewModel: MascotasViewModel,
    mascotaId: Long?,          // null = mascota nueva
    alTerminar: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var especie by rememberSaveable { mutableStateOf("Perro") }
    var raza by rememberSaveable { mutableStateOf("") }
    var fechaDigitos by rememberSaveable { mutableStateOf("") }
    var peso by rememberSaveable { mutableStateOf("") }
    var cargado by rememberSaveable { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Al editar, carga los datos actuales una sola vez
    LaunchedEffect(mascotaId) {
        if (mascotaId != null && !cargado) {
            viewModel.obtener(mascotaId)?.let {
                nombre = it.nombre
                especie = it.especie
                raza = it.raza
                fechaDigitos = it.fechaNacimiento?.let { f -> Fechas.aDigitos(f) } ?: ""
                peso = it.pesoKg?.let { p ->
                    if (p % 1.0 == 0.0) p.toInt().toString() else p.toString()
                } ?: ""
            }
            cargado = true
        }
    }

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
                if (mascotaId == null) "Nueva mascota" else "Editar mascota",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; error = null },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Especie", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Perro", "Gato", "Otro").forEach { opcion ->
                    FilterChip(
                        selected = especie == opcion,
                        onClick = { especie = opcion },
                        label = { Text(opcion) }
                    )
                }
            }

            OutlinedTextField(
                value = raza,
                onValueChange = { raza = it },
                label = { Text("Raza") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fechaDigitos,
                onValueChange = { nuevo ->
                    fechaDigitos = nuevo.filter { it.isDigit() }.take(8)
                    error = null
                },
                label = { Text("Fecha de nacimiento (opcional)") },
                placeholder = { Text("dd/mm/aaaa") },
                singleLine = true,
                visualTransformation = FechaVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it; error = null },
                label = { Text("Peso en kg (opcional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                        val pesoNum = peso.replace(',', '.').toDoubleOrNull()
                        val f = if (fechaDigitos.isBlank()) null else Fechas.desdeDigitos(fechaDigitos)
                        error = when {
                            nombre.isBlank() -> "Escribe el nombre de la mascota"
                            fechaDigitos.isNotBlank() && f == null ->
                                "La fecha no es válida (usa dd/mm/aaaa)"
                            f != null && f > System.currentTimeMillis() ->
                                "La fecha de nacimiento no puede ser futura"
                            peso.isNotBlank() && (pesoNum == null || pesoNum <= 0.0) ->
                                "El peso no es válido"
                            else -> null
                        }
                        if (error == null) {
                            guardando = true
                            viewModel.guardar(
                                id = mascotaId,
                                nombre = nombre,
                                especie = especie,
                                raza = raza,
                                fechaNacimiento = f,
                                pesoKg = if (peso.isBlank()) null else pesoNum,
                                alTerminar = alTerminar
                            )
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Guardar") }
            }
        }
    }
}