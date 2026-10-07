package com.upb.controlmascota.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val MASCOTAS = "mascotas"
    const val PROXIMAS = "proximas"
    const val CONFIGURACION = "configuracion"
    const val CREDITOS = "creditos"

    // Pantallas con argumentos
    const val ARG_MASCOTA_ID = "mascotaId"
    const val DETALLE = "detalle/{$ARG_MASCOTA_ID}"
    const val FORM_MASCOTA = "form_mascota?$ARG_MASCOTA_ID={$ARG_MASCOTA_ID}"
    const val FORM_REGISTRO = "form_registro/{$ARG_MASCOTA_ID}"

    // Funciones para construir la ruta con el id real
    fun detalle(mascotaId: Long) = "detalle/$mascotaId"
    fun formMascota(mascotaId: Long? = null) =
        if (mascotaId == null) "form_mascota" else "form_mascota?$ARG_MASCOTA_ID=$mascotaId"
    fun formRegistro(mascotaId: Long) = "form_registro/$mascotaId"
}