package com.upb.controlmascota.configuracion

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.upb.controlmascota.data.Usuario

/** Sin barra inferior propia: la pone MenuPrincipal. */
@Composable
fun ConfiguracionScreen(
    viewModel: ConfiguracionViewModel,
    alAbrirMenu: () -> Unit,
    alIrACreditos: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var confirmarSalida by remember { mutableStateOf(false) }
    val version = remember { versionApp(context) }

    LaunchedEffect(Unit) { usuario = viewModel.usuarioActual() }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = alAbrirMenu) {
                    Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                }
                Text(
                    "Configuración",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Seccion("Cuenta") {
                    FilaDato("Nombre", usuario?.nombre ?: "—")
                    FilaDato("Correo", usuario?.correo ?: "—")
                }

                Seccion("Notificaciones") {
                    Text(
                        "Recordatorios de vacunas, desparasitaciones y citas.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = {
                            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            } else {
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.fromParts("package", context.packageName, null))
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Ajustes de notificaciones") }
                }

                Seccion("Acerca de") {
                    FilaDato("Versión", version)
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = alIrACreditos)
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Créditos", modifier = Modifier.weight(1f))
                        Text("›", style = MaterialTheme.typography.titleLarge)
                    }
                }

                if (!confirmarSalida) {
                    OutlinedButton(
                        onClick = { confirmarSalida = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cerrar sesión") }
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
                            Text("¿Cerrar sesión?", fontWeight = FontWeight.Bold)
                            Text("Tus mascotas quedan guardadas en este teléfono.")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { confirmarSalida = false },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Cancelar") }
                                Button(
                                    onClick = alCerrarSesion,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) { Text("Salir") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) { contenido() }
        }
    }
}

@Composable
internal fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            valor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
