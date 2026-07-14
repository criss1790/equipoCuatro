package com.example.pico_botella.repository

import com.example.pico_botella.data.RetoDao
import com.example.pico_botella.model.Reto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

// Implementación real del repositorio sobre Room (SQLite).
// ÚNICO lugar del proyecto con Dispatchers.IO para retos.
class RepositorioRetosRoom(private val retoDao: RetoDao) : RepositorioRetos {

    override fun obtenerRetos(): Flow<List<Reto>> = flow {
        emit(listarRetos())
    }

    override suspend fun listarRetos(): List<Reto> =
        withContext(Dispatchers.IO) { retoDao.listarRetos() }

    override suspend fun insertarReto(reto: Reto) =
        withContext(Dispatchers.IO) { retoDao.insertarReto(reto) }

    override suspend fun actualizarReto(reto: Reto) =
        withContext(Dispatchers.IO) { retoDao.actualizarReto(reto) }

    override suspend fun eliminarReto(reto: Reto) =
        withContext(Dispatchers.IO) { retoDao.eliminarReto(reto) }
}
