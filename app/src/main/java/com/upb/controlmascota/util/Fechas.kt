package com.upb.controlmascota.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Las fechas se guardan como milisegundos en UTC (así las entrega el selector de fecha). */
object Fechas {
    private val utc = TimeZone.getTimeZone("UTC")

    /**
     * Hoy, guardado igual que las demás fechas: medianoche UTC del día local.
     * Úsalo para comparar fechas en vez de System.currentTimeMillis(): en Colombia (UTC-5),
     * después de las 7 p. m. la hora actual ya cae en el día siguiente en UTC.
     */
    fun hoy(): Long {
        val local = Calendar.getInstance()
        return Calendar.getInstance(utc).apply {
            clear()
            set(
                local.get(Calendar.YEAR),
                local.get(Calendar.MONTH),
                local.get(Calendar.DAY_OF_MONTH)
            )
        }.timeInMillis
    }

    fun formatear(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy", Locale("es", "CO"))
            .apply { timeZone = utc }
            .format(Date(millis))

    /** Devuelve "3 años", "8 meses" o "Menos de 1 mes". */
    fun edad(nacimientoMillis: Long, ahoraMillis: Long = System.currentTimeMillis()): String {
        val nac = Calendar.getInstance(utc).apply { timeInMillis = nacimientoMillis }
        val hoy = Calendar.getInstance(utc).apply { timeInMillis = ahoraMillis }
        var meses = (hoy.get(Calendar.YEAR) - nac.get(Calendar.YEAR)) * 12 +
                (hoy.get(Calendar.MONTH) - nac.get(Calendar.MONTH))
        if (hoy.get(Calendar.DAY_OF_MONTH) < nac.get(Calendar.DAY_OF_MONTH)) meses--
        return when {
            meses < 0 -> "—"
            meses < 1 -> "Menos de 1 mes"
            meses < 12 -> "$meses ${if (meses == 1) "mes" else "meses"}"
            else -> {
                val anios = meses / 12
                "$anios ${if (anios == 1) "año" else "años"}"
            }
        }
    }
    /** 15/04/2023 -> "15042023" (para mostrar al editar). */
    fun aDigitos(millis: Long): String = formatear(millis).replace("/", "")

    /** "15042023" -> milisegundos UTC, o null si no es una fecha real. */
    fun desdeDigitos(digitos: String): Long? {
        if (digitos.length != 8) return null
        val dia = digitos.substring(0, 2).toInt()
        val mes = digitos.substring(2, 4).toInt()
        val anio = digitos.substring(4, 8).toInt()
        if (anio < 1980) return null
        return try {
            val c = Calendar.getInstance(utc)
            c.isLenient = false
            c.clear()
            c.set(anio, mes - 1, dia)
            c.timeInMillis
        } catch (e: IllegalArgumentException) {
            null
        }
    }

}