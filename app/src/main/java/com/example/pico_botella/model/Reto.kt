package com.example.pico_botella.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pico_botella.utils.Constantes
import java.io.Serializable

// Representa un reto o pregunta que puede salir al jugar.
// Entidad Room (RA-1): se persiste en la tabla "reto".
// Serializable para poder viajar en un Bundle entre Fragments (Bundle + Serializable, nunca Safe Args).
@Entity(tableName = Constantes.TABLA_RETO)
data class Reto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "texto") var texto: String,
    @ColumnInfo(name = "categoria") var categoria: String = "Reto"
) : Serializable
