package com.example.pico_botella.repository

import com.example.pico_botella.model.Reto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// Implementación de prueba en memoria (MutableList). Se usa en tests y como
// repositorio por defecto de FabricaRetosViewModel (reto aleatorio de MainActivity).
// No depende de Room: no rompe si la BD no está disponible.
class RepositorioRetosFalso : RepositorioRetos {

    private val retos = mutableListOf(
        Reto(id = 1, texto = "Cuenta un secreto vergonzoso", categoria = "Verdad"),
        Reto(id = 2, texto = "Imita a un animal durante 30 segundos", categoria = "Reto"),
        Reto(id = 3, texto = "Canta una canción a capela", categoria = "Reto")
    )

    override fun obtenerRetos(): Flow<List<Reto>> {
        return flowOf(retos.toList())
    }

    // Lista ordenada como el DAO real: el último agregado queda arriba (id DESC).
    override suspend fun listarRetos(): List<Reto> =
        retos.sortedByDescending { it.id }

    override suspend fun insertarReto(reto: Reto) {
        val nuevoId = (retos.maxOfOrNull { it.id } ?: 0) + 1
        retos.add(reto.copy(id = nuevoId))
    }

    override suspend fun actualizarReto(reto: Reto) {
        val indice = retos.indexOfFirst { it.id == reto.id }
        if (indice >= 0) retos[indice] = reto
    }

    override suspend fun eliminarReto(reto: Reto) {
        retos.removeAll { it.id == reto.id }
    }
}
