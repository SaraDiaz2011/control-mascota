package com.upb.controlmascota.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upb.controlmascota.ui.theme.Morado
import com.upb.controlmascota.ui.theme.MoradoSuave
import com.upb.controlmascota.ui.theme.Rojo
import com.upb.controlmascota.ui.theme.RojoSuave
import com.upb.controlmascota.ui.theme.Verde
import com.upb.controlmascota.ui.theme.VerdeSuave
import com.upb.controlmascota.util.Fechas

enum class EstadoVacuna(val texto: String) {
    AL_DIA("Al día"),
    PROXIMA("Próxima"),
    VENCIDA("Vencida")
}

object EstadosVacuna {
    private const val DIA = 86_400_000L
    private const val DIAS_PARA_PROXIMA = 30

    // Estado según la próxima fecha (milisegundos UTC).
    // Sin fecha devuelve null y no se muestra chip.
    fun desde(
        proximaFecha: Long?,
        ahora: Long = Fechas.hoy()
    ): EstadoVacuna? {
        if (proximaFecha == null) return null
        val hoy = (ahora / DIA) * DIA
        return when {
            proximaFecha < hoy -> EstadoVacuna.VENCIDA
            proximaFecha - hoy <= DIAS_PARA_PROXIMA * DIA -> EstadoVacuna.PROXIMA
            else -> EstadoVacuna.AL_DIA
        }
    }

    fun colorFondo(estado: EstadoVacuna): Color = when (estado) {
        EstadoVacuna.AL_DIA -> VerdeSuave
        EstadoVacuna.PROXIMA -> MoradoSuave
        EstadoVacuna.VENCIDA -> RojoSuave
    }

    fun colorTexto(estado: EstadoVacuna): Color = when (estado) {
        EstadoVacuna.AL_DIA -> Verde
        EstadoVacuna.PROXIMA -> Morado
        EstadoVacuna.VENCIDA -> Rojo
    }
}

@Composable
fun ChipEstado(estado: EstadoVacuna, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    val fondo: Color
    val texto: Color
    val icono: ImageVector
    when (estado) {
        EstadoVacuna.AL_DIA -> {
            fondo = VerdeSuave
            texto = Verde
            icono = Icons.Filled.Check
        }
        EstadoVacuna.PROXIMA -> {
            fondo = colores.tertiaryContainer
            texto = colores.onTertiaryContainer
            icono = Icons.Filled.DateRange
        }
        EstadoVacuna.VENCIDA -> {
            fondo = colores.errorContainer
            texto = colores.onErrorContainer
            icono = Icons.Filled.Warning
        }
    }
    Row(
        modifier = modifier
            .background(fondo, CircleShape)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            icono,
            contentDescription = null,
            tint = texto,
            modifier = Modifier.size(12.dp)
        )
        Text(
            estado.texto,
            color = texto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
