package com.upb.controlmascota.configuracion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.upb.controlmascota.mascotas.AvatarMascota

/** Sin barra inferior propia: la pone MenuPrincipal. */
@Composable
fun CreditosScreen(alVolver: () -> Unit) {
    val context = LocalContext.current
    val version = remember { versionApp(context) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = alVolver) { Text("← Volver") }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Control Mascota",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Versión $version",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Seccion("Equipo") {
                    DatosEquipo.integrantes.forEach { nombre ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarMascota(nombre, 40.dp)
                            Text(nombre, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Seccion("Proyecto académico") {
                    FilaDato("Universidad", DatosEquipo.UNIVERSIDAD)
                    FilaDato("Programa", DatosEquipo.PROGRAMA)
                    FilaDato("Asignatura", DatosEquipo.ASIGNATURA)
                    FilaDato("Semestre", DatosEquipo.SEMESTRE)
                }

                Text(
                    "© ${DatosEquipo.ANIO} · Hecho con Kotlin y Jetpack Compose",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
