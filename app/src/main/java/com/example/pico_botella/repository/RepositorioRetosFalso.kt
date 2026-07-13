package com.example.pico_botella.repository

import com.example.pico_botella.model.Reto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// Implementación de prueba que entrega una lista fija de retos,
// para poder construir y probar la pantalla antes de tener datos reales.
class RepositorioRetosFalso : RepositorioRetos {

    private val retosDePrueba = listOf(
        Reto(id = 1, texto = "Cuenta un secreto vergonzoso", categoria = "Verdad"),
        Reto(id = 2, texto = "Imita a un animal durante 30 segundos", categoria = "Reto"),
        Reto(id = 3, texto = "Canta una canción a capela", categoria = "Reto")
    )

    override fun obtenerRetos(): Flow<List<Reto>> {
        return flowOf(retosDePrueba)
    }
}
