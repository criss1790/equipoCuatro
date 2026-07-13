package com.example.pico_botella.model

// Representa un reto o pregunta que puede salir al jugar.
data class Reto(
    val id: Int,
    val texto: String,
    val categoria: String
)
