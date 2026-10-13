package com.upb.controlmascota.configuracion

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import com.upb.controlmascota.data.AppDatabase
import com.upb.controlmascota.data.SesionManager
import com.upb.controlmascota.data.Usuario

class ConfiguracionViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.obtener(app).usuarioDao()
    private val sesion = SesionManager(app)

    suspend fun usuarioActual(): Usuario? = dao.obtenerPorId(sesion.usuarioActual())
}

/** versionName de build.gradle.kts, por ejemplo "1.0". */
fun versionApp(context: Context): String = try {
    context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "—"
} catch (e: PackageManager.NameNotFoundException) {
    "—"
}
