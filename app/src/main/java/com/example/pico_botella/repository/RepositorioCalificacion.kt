package com.example.pico_botella.repository

import com.example.pico_botella.model.CalificacionModelo

// Define cómo se guarda y consulta la calificación del jugador, sin importar si el
// almacenamiento es local (SharedPreferences), una base de datos o una API remota.
// Las funciones son suspend porque acceder a almacenamiento es una operación de
// entrada/salida que no debe bloquear el hilo principal.
interface RepositorioCalificacion {
    suspend fun guardarCalificacion(calificacion: CalificacionModelo)
    suspend fun obtenerCalificacion(): CalificacionModelo?
    suspend fun yaSeMostroAutomaticamente(): Boolean
    suspend fun marcarMostradoAutomaticamente()
}
