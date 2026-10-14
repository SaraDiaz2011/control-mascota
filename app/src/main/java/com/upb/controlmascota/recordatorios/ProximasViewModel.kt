package com.upb.controlmascota.recordatorios

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.upb.controlmascota.util.Fechas
import com.upb.controlmascota.data.AppDatabase
import com.upb.controlmascota.data.RegistroConMascota
import kotlinx.coroutines.flow.Flow

class ProximasViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.obtener(app).registroSaludDao()

    /** Registros con próxima fecha desde hoy en adelante, de todas las mascotas del usuario. */
    fun proximos(usuarioId: Long): Flow<List<RegistroConMascota>> =
        dao.proximos(usuarioId, Fechas.hoy())
}
