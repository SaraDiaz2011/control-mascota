package com.upb.controlmascota.recordatorios

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.upb.controlmascota.ui.components.ChipEstado
import com.upb.controlmascota.ui.components.EstadoVacuna
import com.upb.controlmascota.ui.components.EstadosVacuna
import com.upb.controlmascota.util.Fechas
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun ProximasScreen(
    viewModel: ProximasViewModel,
    usuarioId: Long,
    alAbrirMenu: () -> Unit
) {
    val flujo = remember(usuarioId) { viewModel.proximos(usuarioId) }
    val proximos by flujo.collectAsStateWithLifecycle(initialValue = emptyList())
    val hoy = remember { Fechas.hoy() }

    // Sin barra inferior propia: la pone MenuPrincipal
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = alAbrirMenu) {
                    Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                }
                Text(
                    "Próximas vacunas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            AvisoPermiso()

            if (proximos.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay fechas próximas.\nAgrega la próxima dosis en el historial de cada mascota.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val vencidas = proximos.filter {
                    EstadosVacuna.desde(it.registro.proximaFecha) == EstadoVacuna.VENCIDA
                }
                val estaSemana = proximos.filter {
                    val fecha = it.registro.proximaFecha ?: return@filter false
                    val dias = Recordatorios.diasHasta(fecha, hoy)
                    dias in 0..6
                }
                val idsEstaSemana = estaSemana.map { it.registro.id }.toSet()
                val masAdelante = proximos.filter {
                    val estado = EstadosVacuna.desde(it.registro.proximaFecha)
                    estado != EstadoVacuna.VENCIDA && it.registro.id !in idsEstaSemana
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (vencidas.isNotEmpty()) {
                        item { TituloGrupo("Vencida", EstadoVacuna.VENCIDA) }
                        items(vencidas, key = { it.registro.id }) { TarjetaProximo(it) }
                    }
                    if (estaSemana.isNotEmpty()) {
                        item { TituloGrupo("Esta semana", EstadoVacuna.PROXIMA) }
                        items(estaSemana, key = { it.registro.id }) { TarjetaProximo(it) }
                    }
                    if (masAdelante.isNotEmpty()) {
                        item { TituloGrupo("Más adelante", EstadoVacuna.AL_DIA) }
                        items(masAdelante, key = { "adelante-${it.registro.id}" }) {
                            TarjetaProximo(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TituloGrupo(titulo: String, estado: EstadoVacuna) {
    Text(
        titulo,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = EstadosVacuna.colorTexto(estado),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun TarjetaProximo(item: RegistroConMascota) {
    val fecha = item.registro.proximaFecha ?: return
    val estado = EstadosVacuna.desde(fecha) ?: EstadoVacuna.PROXIMA
    val (dia, mes) = diaYMes(fecha)
    val colorTexto = EstadosVacuna.colorTexto(estado)
    val colorFondo = EstadosVacuna.colorFondo(estado)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .width(56.dp)
                    .background(colorFondo, RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(dia, fontWeight = FontWeight.Bold, color = colorTexto)
                Text(
                    mes,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorTexto
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "${item.mascotaNombre} · ${item.mascotaEspecie}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(item.registro.nombre, fontWeight = FontWeight.Bold)
                Text(
                    Recordatorios.textoTipo(item.registro.tipo),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            ChipEstado(estado)
        }
    }
}

private fun diaYMes(millis: Long): Pair<String, String> {
    val utc = TimeZone.getTimeZone("UTC")
    val locale = Locale("es", "CO")
    val dia = SimpleDateFormat("dd", locale).apply { timeZone = utc }.format(Date(millis))
    val mes = SimpleDateFormat("MMM", locale).apply { timeZone = utc }
        .format(Date(millis))
        .replace(".", "")
        .replaceFirstChar { it.uppercase() }
    return dia to mes
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
