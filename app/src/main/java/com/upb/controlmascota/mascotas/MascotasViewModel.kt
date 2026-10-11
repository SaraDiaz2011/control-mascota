package com.upb.controlmascota.mascotas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upb.controlmascota.data.AppDatabase
import com.upb.controlmascota.data.Mascota
import com.upb.controlmascota.data.SesionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MascotasViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.obtener(app).mascotaDao()
    private val usuarioId = SesionManager(app).usuarioActual()

    val busqueda = MutableStateFlow("")

    val mascotas: StateFlow<List<Mascota>> =
        combine(dao.listarPorUsuario(usuarioId), busqueda) { lista, texto ->
            if (texto.isBlank()) lista
            else lista.filter { it.nombre.contains(texto.trim(), ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun observar(id: Long): Flow<Mascota?> = dao.observarPorId(id)

    suspend fun obtener(id: Long): Mascota? = dao.obtenerPorId(id)

    /** id == null crea una mascota nueva; si trae id, actualiza la existente. */
    fun guardar(
        id: Long?,
        nombre: String,
        especie: String,
        raza: String,
        fechaNacimiento: Long?,
        pesoKg: Double?,
        alTerminar: () -> Unit
    ) {
        viewModelScope.launch {
            if (id == null) {
                dao.insertar(
                    Mascota(
                        usuarioId = usuarioId,
                        nombre = nombre.trim(),
                        especie = especie,
                        raza = raza.trim(),
                        fechaNacimiento = fechaNacimiento,
                        pesoKg = pesoKg
                    )
                )
            } else {
                val actual = dao.obtenerPorId(id)
                if (actual != null) {
                    dao.actualizar(
                        actual.copy(
                            nombre = nombre.trim(),
                            especie = especie,
                            raza = raza.trim(),
                            fechaNacimiento = fechaNacimiento,
                            pesoKg = pesoKg
                        )
                    )
                }
            }
            alTerminar()
        }
    }

    fun eliminar(mascota: Mascota, alTerminar: () -> Unit) {
        viewModelScope.launch {
            dao.eliminar(mascota)
            alTerminar()
        }
    }
}