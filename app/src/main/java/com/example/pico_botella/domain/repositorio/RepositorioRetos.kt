package com.example.pico_botella.domain.repositorio

import com.example.pico_botella.domain.modelo.Reto
import kotlinx.coroutines.flow.Flow

// Define las operaciones disponibles para obtener los retos del juego,
// sin importar de dónde vengan los datos (local, remoto o de prueba).
interface RepositorioRetos {
    fun obtenerRetos(): Flow<List<Reto>>
}
