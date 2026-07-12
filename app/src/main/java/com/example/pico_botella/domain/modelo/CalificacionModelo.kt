package com.example.pico_botella.domain.modelo

// Representa la calificación que un jugador da a la app: estrellas, comentario opcional y fecha de envío.
data class CalificacionModelo(
    val estrellas: Int,
    val comentario: String?,
    val fechaEnvio: Long,
    val yaCalifico: Boolean
)
