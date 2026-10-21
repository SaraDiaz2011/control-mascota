package com.upb.controlmascota.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = Primario,
    onPrimary = SobrePrimario,
    primaryContainer = PrimarioSuave,
    onPrimaryContainer = SobrePrimarioSuave,
    secondary = SobreDurazno,
    onSecondary = SobrePrimario,
    secondaryContainer = Durazno,
    onSecondaryContainer = SobreDurazno,
    tertiary = Morado,
    onTertiary = SobrePrimario,
    tertiaryContainer = MoradoSuave,
    onTertiaryContainer = Morado,
    background = Fondo,
    onBackground = Tinta,
    surface = Superficie,
    onSurface = Tinta,
    surfaceVariant = PrimarioSuave,
    onSurfaceVariant = TextoSecundario,
    outline = BordeCampos,
    outlineVariant = Borde,
    error = Rojo,
    onError = SobrePrimario,
    errorContainer = RojoSuave,
    onErrorContainer = Rojo,
    surfaceContainerLowest = Superficie,
    surfaceContainerLow = Superficie,
    surfaceContainer = Superficie,
    surfaceContainerHigh = Superficie,
    surfaceContainerHighest = Superficie,
)

@Composable
fun ControlMascotaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Typography,
        content = content
    )
}
