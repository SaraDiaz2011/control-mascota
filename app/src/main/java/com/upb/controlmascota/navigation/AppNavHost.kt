package com.upb.controlmascota.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.upb.controlmascota.ui.theme.auth.AuthViewModel
import com.upb.controlmascota.ui.theme.auth.LoginScreen
import com.upb.controlmascota.ui.theme.auth.RegistroScreen
import com.upb.controlmascota.ui.theme.auth.SplashScreen
import com.upb.controlmascota.ui.theme.components.PantallaProvisional
import com.upb.controlmascota.mascotas.DetalleMascotaScreen
import com.upb.controlmascota.mascotas.FormMascotaScreen
import com.upb.controlmascota.mascotas.ListaMascotasScreen
import com.upb.controlmascota.mascotas.MascotasViewModel

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(alComenzar = {
                val destino = if (authViewModel.haySesion()) Routes.MASCOTAS else Routes.LOGIN
                navController.navigate(destino) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                alExito = {
                    navController.navigate(Routes.MASCOTAS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                irARegistro = { navController.navigate(Routes.REGISTRO) }
            )
        }

        composable(Routes.REGISTRO) {
            RegistroScreen(
                viewModel = authViewModel,
                alExito = {
                    navController.navigate(Routes.MASCOTAS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                alVolver = { navController.popBackStack() }
            )
        }

        composable(Routes.MASCOTAS) {
            val vm: MascotasViewModel = viewModel()
            ListaMascotasScreen(
                viewModel = vm,
                alAbrir = { id -> navController.navigate(Routes.detalle(id)) },
                alAgregar = { navController.navigate(Routes.formMascota()) },
                alIrAProximas = { navController.navigate(Routes.PROXIMAS) },
                alIrAConfiguracion = { navController.navigate(Routes.CONFIGURACION) }
            )
        }

        composable(
            route = Routes.DETALLE,
            arguments = listOf(navArgument(Routes.ARG_MASCOTA_ID) { type = NavType.LongType })
        ) { entrada ->
            val id = entrada.arguments?.getLong(Routes.ARG_MASCOTA_ID) ?: 0L
            val vm: MascotasViewModel = viewModel()
            DetalleMascotaScreen(
                viewModel = vm,
                mascotaId = id,
                alVolver = { navController.popBackStack() },
                alEditar = { navController.navigate(Routes.formMascota(id)) },
                alAgregarRegistro = { navController.navigate(Routes.formRegistro(id)) }
            )
        }

        composable(
            route = Routes.FORM_MASCOTA,
            arguments = listOf(
                navArgument(Routes.ARG_MASCOTA_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { entrada ->
            val id = entrada.arguments?.getLong(Routes.ARG_MASCOTA_ID) ?: -1L
            val vm: MascotasViewModel = viewModel()
            FormMascotaScreen(
                viewModel = vm,
                mascotaId = if (id == -1L) null else id,
                alTerminar = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.FORM_REGISTRO,
            arguments = listOf(navArgument(Routes.ARG_MASCOTA_ID) { type = NavType.LongType })
        ) { entrada ->
            val id = entrada.arguments?.getLong(Routes.ARG_MASCOTA_ID) ?: 0L
            PantallaProvisional(
                "Nuevo registro de salud (mascota #$id)",
                listOf(
                    "Guardar" to { navController.popBackStack() },
                    "Cancelar" to { navController.popBackStack() }
                )
            )
        }

        composable(Routes.PROXIMAS) {
            PantallaProvisional(
                "Próximas vacunas",
                listOf(
                    "Mis mascotas" to { navController.navigate(Routes.MASCOTAS) },
                    "Volver" to { navController.popBackStack() }
                )
            )
        }

        composable(Routes.CONFIGURACION) {
            PantallaProvisional(
                "Configuración",
                listOf(
                    "Créditos" to { navController.navigate(Routes.CREDITOS) },
                    "Cerrar sesión" to {
                        authViewModel.cerrarSesion()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.MASCOTAS) { inclusive = true }
                        }
                    },
                    "Volver" to { navController.popBackStack() }
                )
            )
        }

        composable(Routes.CREDITOS) {
            PantallaProvisional(
                "Créditos",
                listOf("Volver" to { navController.popBackStack() })
            )
        }
    }
}