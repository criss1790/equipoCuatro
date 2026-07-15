package com.example.pico_botella.repository

import android.content.Context
import com.example.pico_botella.model.CalificacionModelo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AlmacenamientoCalificacionLocal(
    contexto: Context
) : RepositorioCalificacion {

    private val preferencias = contexto.applicationContext
        .getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE)

    override suspend fun guardarCalificacion(calificacion: CalificacionModelo) =
        withContext(Dispatchers.IO) {
            preferencias.edit()
                .putInt(CLAVE_ESTRELLAS, calificacion.estrellas)
                .putString(CLAVE_COMENTARIO, calificacion.comentario)
                .putLong(CLAVE_FECHA_ENVIO, calificacion.fechaEnvio)
                .putBoolean(CLAVE_YA_CALIFICO, true)
                .apply()
        }

    override suspend fun obtenerCalificacion(): CalificacionModelo? =
        withContext(Dispatchers.IO) {
            if (!preferencias.getBoolean(CLAVE_YA_CALIFICO, false)) {
                null
            } else {
                CalificacionModelo(
                    estrellas = preferencias.getInt(CLAVE_ESTRELLAS, 0),
                    comentario = preferencias.getString(CLAVE_COMENTARIO, null),
                    fechaEnvio = preferencias.getLong(CLAVE_FECHA_ENVIO, 0L),
                    yaCalifico = true
                )
            }
        }

    override suspend fun yaSeMostroAutomaticamente(): Boolean =
        withContext(Dispatchers.IO) {
            preferencias.getBoolean(CLAVE_MOSTRADO_AUTOMATICO, false)
        }

    override suspend fun marcarMostradoAutomaticamente() =
        withContext(Dispatchers.IO) {
            preferencias.edit().putBoolean(CLAVE_MOSTRADO_AUTOMATICO, true).apply()
        }

    companion object {
        private const val NOMBRE_PREFERENCIAS = "preferencias_calificacion"
        private const val CLAVE_ESTRELLAS = "clave_estrellas"
        private const val CLAVE_COMENTARIO = "clave_comentario"
        private const val CLAVE_FECHA_ENVIO = "clave_fecha_envio"
        private const val CLAVE_YA_CALIFICO = "clave_ya_califico"
        private const val CLAVE_MOSTRADO_AUTOMATICO = "clave_mostrado_automatico"
    }
}
