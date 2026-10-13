package com.upb.controlmascota.recordatorios

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upb.controlmascota.MainActivity
import com.upb.controlmascota.R
import com.upb.controlmascota.data.AppDatabase
import com.upb.controlmascota.data.SesionManager
import kotlinx.coroutines.flow.first

/** Se ejecuta una vez al día y avisa de las fechas que están por llegar. */
class RecordatorioWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val usuarioId = SesionManager(context).usuarioActual()
        if (usuarioId == -1L || !puedeNotificar()) return Result.success()

        val hoy = Recordatorios.hoyUtc()
        val proximos = AppDatabase.obtener(context)
            .registroSaludDao()
            .proximos(usuarioId, hoy)
            .first()

        Recordatorios.crearCanal(context)
        proximos.forEach { item ->
            val fecha = item.registro.proximaFecha ?: return@forEach
            val dias = Recordatorios.diasHasta(fecha, hoy)
            if (dias in Recordatorios.DIAS_AVISO) {
                notificar(
                    id = item.registro.id,
                    mascota = item.mascotaNombre,
                    tipo = Recordatorios.textoTipo(item.registro.tipo),
                    nombre = item.registro.nombre,
                    dias = dias
                )
            }
        }
        return Result.success()
    }

    private fun puedeNotificar(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return false
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun notificar(id: Long, mascota: String, tipo: String, nombre: String, dias: Long) {
        val abrirApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val cuando = if (dias == 0L) "es hoy" else "es en $dias ${if (dias == 1L) "día" else "días"}"

        val notificacion = NotificationCompat.Builder(context, Recordatorios.CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle("$tipo de $mascota")
            .setContentText("$nombre $cuando")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()

        try {
            // El id del registro como id de la notificación: no se repite el mismo aviso
            NotificationManagerCompat.from(context).notify(id.toInt(), notificacion)
        } catch (e: SecurityException) {
            // El usuario quitó el permiso justo ahora; no hay nada más que hacer
        }
    }
}
