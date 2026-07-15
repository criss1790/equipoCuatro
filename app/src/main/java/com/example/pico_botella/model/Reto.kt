package com.example.pico_botella.model

// Importaciones necesarias para que Android entienda Room
import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity le dice a Room que esto es una tabla de la base de datos
@Entity(tableName = "tabla_retos")
data class Reto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val texto: String,
    val categoria: String,

    // Aquí guardaremos la imagen del Pokémon de forma temporal
    var pokemonImageUrl: String? = null
)