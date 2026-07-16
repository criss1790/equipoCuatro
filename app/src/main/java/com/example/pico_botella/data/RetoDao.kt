package com.example.pico_botella.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pico_botella.model.Reto
import com.example.pico_botella.utils.Constantes

// Acceso a datos de la tabla "reto". Todas las operaciones son suspend
// (se ejecutan fuera del hilo principal desde el repositorio).
@Dao
interface RetoDao {

    // ORDER BY id DESC → el reto recién insertado queda arriba (HU 6.0).
    @Query("SELECT * FROM ${Constantes.TABLA_RETO} ORDER BY id DESC")
    suspend fun listarRetos(): List<Reto>

    @Insert
    suspend fun insertarReto(reto: Reto)

    @Update
    suspend fun actualizarReto(reto: Reto)

    @Delete
    suspend fun eliminarReto(reto: Reto)
}
