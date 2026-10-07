package com.upb.controlmascota.ui.theme.auth

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upb.controlmascota.data.AppDatabase
import com.upb.controlmascota.data.Seguridad
import com.upb.controlmascota.data.SesionManager
import com.upb.controlmascota.data.Usuario
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.obtener(app)
    private val sesion = SesionManager(app)

    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun haySesion() = sesion.usuarioActual() != -1L
    fun usuarioActualId() = sesion.usuarioActual()
    fun cerrarSesion() = sesion.cerrar()
    fun limpiarError() { error = null }

    fun iniciarSesion(correo: String, contrasena: String, alExito: () -> Unit) {
        if (correo.isBlank() || contrasena.isBlank()) {
            error = "Completa todos los campos"
            return
        }
        viewModelScope.launch {
            cargando = true
            val usuario = db.usuarioDao().buscarPorCorreo(correo.trim().lowercase())
            if (usuario != null && Seguridad.verificar(contrasena, usuario.contrasenaHash)) {
                sesion.guardar(usuario.id)
                error = null
                alExito()
            } else {
                error = "Correo o contraseña incorrectos"
            }
            cargando = false
        }
    }

    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        confirmar: String,
        aceptaTerminos: Boolean,
        alExito: () -> Unit
    ) {
        val correoLimpio = correo.trim().lowercase()
        error = when {
            nombre.isBlank() -> "Escribe tu nombre"
            !Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches() -> "Correo no válido"
            contrasena.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            contrasena != confirmar -> "Las contraseñas no coinciden"
            !aceptaTerminos -> "Debes aceptar los términos y condiciones"
            else -> null
        }
        if (error != null) return

        viewModelScope.launch {
            cargando = true
            if (db.usuarioDao().buscarPorCorreo(correoLimpio) != null) {
                error = "Ya existe una cuenta con ese correo"
            } else {
                val id = db.usuarioDao().insertar(
                    Usuario(
                        nombre = nombre.trim(),
                        correo = correoLimpio,
                        contrasenaHash = Seguridad.hashear(contrasena)
                    )
                )
                sesion.guardar(id)
                alExito()
            }
            cargando = false
        }
    }
}