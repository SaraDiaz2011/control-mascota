package com.upb.controlmascota.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): Usuario?
}

@Dao
interface MascotaDao {
    @Insert
    suspend fun insertar(mascota: Mascota): Long

    @Update
    suspend fun actualizar(mascota: Mascota)

    @Delete
    suspend fun eliminar(mascota: Mascota)

    @Query("SELECT * FROM mascotas WHERE usuarioId = :usuarioId ORDER BY nombre")
    fun listarPorUsuario(usuarioId: Long): Flow<List<Mascota>>

    @Query("SELECT * FROM mascotas WHERE id = :id")
    fun observarPorId(id: Long): Flow<Mascota?>

    @Query("SELECT * FROM mascotas WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Mascota?
}

// Registro + datos de la mascota, para la pantalla "Próximas vacunas"
data class RegistroConMascota(
    @Embedded val registro: RegistroSalud,
    val mascotaNombre: String,
    val mascotaEspecie: String
)

@Dao
interface RegistroSaludDao {
    @Insert
    suspend fun insertar(registro: RegistroSalud): Long

    @Update
    suspend fun actualizar(registro: RegistroSalud)

    @Delete
    suspend fun eliminar(registro: RegistroSalud)

    @Query("SELECT * FROM registros_salud WHERE mascotaId = :mascotaId ORDER BY fechaAplicada DESC")
    fun listarPorMascota(mascotaId: Long): Flow<List<RegistroSalud>>

    @Query("SELECT * FROM registros_salud WHERE id = :id")
    suspend fun obtenerPorId(id: Long): RegistroSalud?

    @Query(
        """
        SELECT r.*, m.nombre AS mascotaNombre, m.especie AS mascotaEspecie
        FROM registros_salud r
        INNER JOIN mascotas m ON r.mascotaId = m.id
        WHERE m.usuarioId = :usuarioId
          AND r.proximaFecha IS NOT NULL
          AND r.proximaFecha >= :desde
        ORDER BY r.proximaFecha ASC
        """
    )
    fun proximos(usuarioId: Long, desde: Long): Flow<List<RegistroConMascota>>
}