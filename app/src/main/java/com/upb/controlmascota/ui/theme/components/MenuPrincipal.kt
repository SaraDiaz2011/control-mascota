package com.upb.controlmascota.ui.theme.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.upb.controlmascota.navigation.Routes
import kotlinx.coroutines.launch

private data class Destino(
    val ruta: String,
    val corto: String,   // texto del menú inferior
    val largo: String,   // texto del menú lateral
    val icono: ImageVector
)

private val destinosPrincipales = listOf(
    Destino(Routes.MASCOTAS, "Mascotas", "Mis mascotas", Icons.Filled.Home),
    Destino(Routes.PROXIMAS, "Próximas", "Próximas vacunas", Icons.Filled.DateRange),
    Destino(Routes.CONFIGURACION, "Ajustes", "Configuración", Icons.Filled.Settings)
)

/**
 * Envuelve toda la navegación con el menú inferior y el menú lateral.
 * Solo se muestran en las pantallas principales (Mascotas, Próximas, Ajustes).
 */
@Composable
fun MenuPrincipal(
    navController: NavHostController,
    alCerrarSesion: () -> Unit,
    contenido: @Composable (modificador: Modifier, abrirMenu: () -> Unit) -> Unit
) {
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val esPrincipal = destinosPrincipales.any { it.ruta == rutaActual }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun irA(ruta: String) {
        if (ruta == rutaActual) return
        navController.navigate(ruta) {
            popUpTo(Routes.MASCOTAS)
            launchSingleTop = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = esPrincipal,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Control Mascota",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
                )
                HorizontalDivider()
                Spacer(Modifier.padding(top = 8.dp))

                destinosPrincipales.forEach { d ->
                    NavigationDrawerItem(
                        label = { Text(d.largo) },
                        icon = { Icon(d.icono, contentDescription = null) },
                        selected = rutaActual == d.ruta,
                        onClick = {
                            scope.launch { drawerState.close() }
                            irA(d.ruta)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
                NavigationDrawerItem(
                    label = { Text("Créditos") },
                    icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        irA(Routes.CREDITOS)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                Spacer(Modifier.weight(1f))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        alCerrarSesion()
                    },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .padding(vertical = 8.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.error,
                        unselectedTextColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (esPrincipal) {
                    NavigationBar {
                        destinosPrincipales.forEach { d ->
                            NavigationBarItem(
                                selected = rutaActual == d.ruta,
                                onClick = { irA(d.ruta) },
                                icon = { Icon(d.icono, contentDescription = d.corto) },
                                label = { Text(d.corto) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        ) { relleno ->
            contenido(
                Modifier.padding(relleno).consumeWindowInsets(relleno),
                { scope.launch { drawerState.open() } }
            )
        }
    }
}