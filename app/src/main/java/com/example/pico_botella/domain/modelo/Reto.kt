package com.example.pico_botella.domain.modelo

// Representa un reto o pregunta que puede salir al jugar.
data class Reto(
    val id: Int,
    val texto: String,
    val categoria: String
)
