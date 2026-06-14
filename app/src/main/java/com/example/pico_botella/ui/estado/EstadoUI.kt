package com.example.pico_botella.ui.estado

// Representa los posibles estados de la pantalla mientras se obtienen datos balga la redundancia de la base de datos.
sealed class EstadoUI<out T> {
    object Cargando : EstadoUI<Nothing>()
    data class Exito<T>(val datos: T) : EstadoUI<T>()
    data class Error(val mensaje: String) : EstadoUI<Nothing>()
    object Vacio : EstadoUI<Nothing>()
}
