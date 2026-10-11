package com.upb.controlmascota.ui.theme.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFechaDialog(
    inicial: Long?,
    alElegir: (Long) -> Unit,
    alCerrar: () -> Unit
) {
    val estado = rememberDatePickerState(initialSelectedDateMillis = inicial)
    DatePickerDialog(
        onDismissRequest = alCerrar,
        confirmButton = {
            TextButton(onClick = {
                estado.selectedDateMillis?.let(alElegir)
                alCerrar()
            }) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = alCerrar) { Text("Cancelar") } }
    ) {
        DatePicker(state = estado)
    }
}