package com.upb.controlmascota.mascotas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upb.controlmascota.data.Mascota
import com.upb.controlmascota.util.Fechas

@Composable
fun ListaMascotasScreen(
    viewModel: MascotasViewModel,
    alAbrir: (Long) -> Unit,
    alAgregar: () -> Unit,
    alIrAProximas: () -> Unit,
    alIrAConfiguracion: () -> Unit
) {
    val mascotas by viewModel.mascotas.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Mis mascotas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = busqueda,
                onValueChange = { viewModel.busqueda.value = it },
                placeholder = { Text("Buscar mascota…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (mascotas.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (busqueda.isBlank()) "Aún no tienes mascotas.\nAgrega la primera."
                        else "Sin resultados",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(mascotas, key = { it.id }) { m ->
                        TarjetaMascota(m) { alAbrir(m.id) }
                    }
                }
            }

            Button(
                onClick = alAgregar,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("+ Agregar mascota") }

            // Provisional: luego se reemplaza por el menú inferior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = alIrAProximas) { Text("Próximas vacunas") }
                TextButton(onClick = alIrAConfiguracion) { Text("Configuración") }
            }
        }
    }
}

@Composable
private fun TarjetaMascota(mascota: Mascota, alClic: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = alClic)) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarMascota(mascota.nombre, 56.dp)
            Column {
                Text(
                    mascota.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    listOfNotNull(
                        mascota.especie,
                        mascota.raza.ifBlank { null },
                        mascota.fechaNacimiento?.let { Fechas.edad(it) }
                    ).joinToString(" · "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Círculo con la inicial del nombre (la foto real se agrega después). */
@Composable
fun AvatarMascota(nombre: String, tamano: Dp) {
    Box(
        modifier = Modifier
            .size(tamano)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            nombre.firstOrNull()?.uppercase() ?: "?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}