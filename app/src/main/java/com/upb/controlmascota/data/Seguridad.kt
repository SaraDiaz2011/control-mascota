package com.upb.controlmascota.data

import java.security.MessageDigest
import java.security.SecureRandom

object Seguridad {
    private fun ByteArray.aHex() = joinToString("") { "%02x".format(it) }
    private fun String.deHex() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    private fun calcular(sal: ByteArray, contrasena: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(sal)
        return md.digest(contrasena.toByteArray(Charsets.UTF_8)).aHex()
    }

    /** Devuelve "sal:hash". Cada usuario tiene su propia sal aleatoria. */
    fun hashear(contrasena: String): String {
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return sal.aHex() + ":" + calcular(sal, contrasena)
    }

    fun verificar(contrasena: String, almacenado: String): Boolean {
        val partes = almacenado.split(":")
        if (partes.size != 2) return false
        return calcular(partes[0].deHex(), contrasena) == partes[1]
    }
}