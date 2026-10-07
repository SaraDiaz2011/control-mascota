package com.upb.controlmascota.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val contrasenaHash: String // nunca guardes la contraseña en texto plano
)

@Entity(
    tableName = "mascotas",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId")]
)
data class Mascota(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val nombre: String,
    val especie: String,
    val raza: String = "",
    val fechaNacimiento: Long? = null, // milisegundos desde 1970
    val pesoKg: Double? = null,
    val fotoUri: String? = null
)

object TipoRegistro {
    const val VACUNA = "VACUNA"
    const val DESPARASITACION = "DESPARASITACION"
    const val CITA = "CITA"
}

@Entity(
    tableName = "registros_salud",
    foreignKeys = [
        ForeignKey(
            entity = Mascota::class,
            parentColumns = ["id"],
            childColumns = ["mascotaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mascotaId")]
)
data class RegistroSalud(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mascotaId: Long,
    val tipo: String, // usa TipoRegistro.VACUNA, etc.
    val nombre: String,
    val fechaAplicada: Long,
    val proximaFecha: Long? = null,
    val notas: String = ""
)