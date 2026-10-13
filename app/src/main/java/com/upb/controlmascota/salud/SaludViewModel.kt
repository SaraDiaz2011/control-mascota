package com.upb.controlmascota.salud

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upb.controlmascota.data.AppDatabase
import com.upb.controlmascota.data.RegistroSalud
import com.upb.controlmascota.data.TipoRegistro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SaludViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.obtener(app).registroSaludDao()

    fun listar(mascotaId: Long): Flow<List<RegistroSalud>> = dao.listarPorMascota(mascotaId)

    suspend fun obtener(id: Long): RegistroSalud? = dao.obtenerPorId(id)

    /** id == null crea un registro nuevo; si trae id, actualiza el existente. */
    fun guardar(
        id: Long?,
        mascotaId: Long,
        tipo: String,
        nombre: String,
        fechaAplicada: Long,
        proximaFecha: Long?,
        notas: String,
        alTerminar: () -> Unit
    ) {
        viewModelScope.launch {
            if (id == null) {
                dao.insertar(
                    RegistroSalud(
                        mascotaId = mascotaId,
                        tipo = tipo,
                        nombre = nombre.trim(),
                        fechaAplicada = fechaAplicada,
                        proximaFecha = proximaFecha,
                        notas = notas.trim()
                    )
                )
            } else {
                val actual = dao.obtenerPorId(id)
                if (actual != null) {
                    dao.actualizar(
                        actual.copy(
                            tipo = tipo,
                            nombre = nombre.trim(),
                            fechaAplicada = fechaAplicada,
                            proximaFecha = proximaFecha,
                            notas = notas.trim()
                        )
                    )
                }
            }
            alTerminar()
        }
    }

    fun eliminar(id: Long, alTerminar: () -> Unit) {
        viewModelScope.launch {
            dao.obtenerPorId(id)?.let { dao.eliminar(it) }
            alTerminar()
        }
    }
}

/** Texto para mostrar cada tipo de registro. */
fun textoTipo(tipo: String): String = when (tipo) {
    TipoRegistro.VACUNA -> "Vacuna"
    TipoRegistro.DESPARASITACION -> "Desparasitación"
    TipoRegistro.CITA -> "Cita"
    else -> tipo
}
