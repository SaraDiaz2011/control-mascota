package com.upb.controlmascota.data

import android.content.Context

/** Recuerda qué usuario inició sesión, aunque se cierre la app. */
class SesionManager(context: Context) {
    private val prefs =
        context.applicationContext.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    fun guardar(usuarioId: Long) = prefs.edit().putLong(CLAVE, usuarioId).apply()
    fun usuarioActual(): Long = prefs.getLong(CLAVE, -1L)
    fun cerrar() = prefs.edit().remove(CLAVE).apply()

    companion object {
        private const val CLAVE = "usuarioId"
    }
}