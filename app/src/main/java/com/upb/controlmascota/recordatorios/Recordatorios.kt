package com.upb.controlmascota.recordatorios

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.upb.controlmascota.util.Fechas
import com.upb.controlmascota.data.TipoRegistro
import java.util.concurrent.TimeUnit

object Recordatorios {
    const val CANAL_ID = "recordatorios_salud"
    private const val TRABAJO = "revisar_recordatorios"
    private const val DIA_MS = 24L * 60 * 60 * 1000

    /** Avisa cuando falten estos días para la próxima fecha (0 = hoy). */
    val DIAS_AVISO = setOf(3L, 1L, 0L)

    /** Programa la revisión diaria. Si ya estaba programada, la deja igual. */
    fun programar(context: Context) {
        crearCanal(context)
        val trabajo = PeriodicWorkRequestBuilder<RecordatorioWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            TRABAJO,
            ExistingPeriodicWorkPolicy.KEEP,
            trabajo
        )
    }

    /** Revisa una vez ya mismo (por ejemplo, justo después de conceder el permiso). */
    fun revisarAhora(context: Context) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            "$TRABAJO-ahora",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<RecordatorioWorker>().build()
        )
    }

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Recordatorios de salud",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Avisos de próximas vacunas, desparasitaciones y citas" }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(canal)
        }
    }

    fun diasHasta(fecha: Long, hoy: Long = Fechas.hoy()): Long = (fecha - hoy) / DIA_MS

    /** "Hoy", "Mañana" o "En 5 días". */
    fun textoDias(dias: Long): String = when (dias) {
        0L -> "Hoy"
        1L -> "Mañana"
        else -> "En $dias días"
    }

    fun textoTipo(tipo: String): String = when (tipo) {
        TipoRegistro.VACUNA -> "Vacuna"
        TipoRegistro.DESPARASITACION -> "Desparasitación"
        TipoRegistro.CITA -> "Cita"
        else -> tipo
    }
}
