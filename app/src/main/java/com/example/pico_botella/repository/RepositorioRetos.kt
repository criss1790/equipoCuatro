package com.example.pico_botella.repository

import com.example.pico_botella.model.Reto
import kotlinx.coroutines.flow.Flow

// Define las operaciones disponibles sobre los retos del juego,
// sin importar de dónde vengan los datos (local Room, remoto o de prueba).
interface RepositorioRetos {

    // Conservado: lo usa RetosViewModel para el reto aleatorio de MainActivity.
    fun obtenerRetos(): Flow<List<Reto>>

    // CRUD (HU-0): usados por RetoViewModel para el listado y las HU 7/8/9.
    suspend fun listarRetos(): List<Reto>
    suspend fun insertarReto(reto: Reto)
    suspend fun actualizarReto(reto: Reto)
    suspend fun eliminarReto(reto: Reto)
}
