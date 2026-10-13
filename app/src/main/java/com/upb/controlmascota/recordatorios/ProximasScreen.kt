package com.upb.controlmascota.recordatorios

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.upb.controlmascota.data.RegistroConMascota
import com.upb.controlmascota.util.Fechas

@Composable
fun ProximasScreen(
    viewModel: ProximasViewModel,
    usuarioId: Long,
    alVolver: () -> Unit
) {
    val flujo = remember(usuarioId) { viewModel.proximos(usuarioId) }
    val proximos by flujo.collectAsStateWithLifecycle(initialValue = emptyList())
    val hoy = remember { Recordatorios.hoyUtc() }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = alVolver) { Text("← Volver") }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Próximas vacunas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                AvisoPermiso()

                if (proximos.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No hay fechas próximas.\nAgrega la próxima dosis en el historial de cada mascota.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(proximos, key = { it.registro.id }) { item ->
                            TarjetaProximo(item, hoy)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaProximo(item: RegistroConMascota, hoy: Long) {
    val fecha = item.registro.proximaFecha ?: return
    val dias = Recordatorios.diasHasta(fecha, hoy)
    val urgente = dias <= 3

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (urgente) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ) else CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "${item.mascotaNombre} · ${item.mascotaEspecie}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(item.registro.nombre, fontWeight = FontWeight.Bold)
                Text(
                    "${Recordatorios.textoTipo(item.registro.tipo)} · ${Fechas.formatear(fecha)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                Recordatorios.textoDias(dias),
                fontWeight = FontWeight.Bold,
                color = if (urgente) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** En Android 13+ pide el permiso de notificaciones y, si falta, muestra un aviso. */
@Composable
private fun AvisoPermiso() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    var concedido by remember { mutableStateOf(tienePermiso(context)) }
    var yaPedido by rememberSaveable { mutableStateOf(false) }
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { ok ->
        concedido = ok
        yaPedido = true
        if (ok) Recordatorios.revisarAhora(context)
    }

    // Se pide solo una vez al abrir; después el usuario usa el botón
    LaunchedEffect(Unit) {
        if (!concedido && !yaPedido) lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    // Al volver de los ajustes, revisa de nuevo
    LifecycleResumeEffect(Unit) {
        concedido = tienePermiso(context)
        onPauseOrDispose { }
    }

    if (concedido) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Activa las notificaciones", fontWeight = FontWeight.Bold)
            Text("Así te avisamos 3 días antes, 1 día antes y el mismo día de cada fecha.")
            Button(onClick = {
                if (!yaPedido) {
                    lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    // Si ya lo rechazó, Android no vuelve a mostrar el diálogo: abrir ajustes
                    context.startActivity(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    )
                }
            }) { Text(if (yaPedido) "Abrir ajustes" else "Activar") }
        }
    }
}

private fun tienePermiso(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED
