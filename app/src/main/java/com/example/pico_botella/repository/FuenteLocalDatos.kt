package com.example.pico_botella.repository

import com.example.pico_botella.data.RetoDao

// Contrato de la fuente de datos local (Room). Expone el DAO de retos;
// deja de ser un placeholder vacío.
interface FuenteLocalDatos {
    fun retoDao(): RetoDao
}
