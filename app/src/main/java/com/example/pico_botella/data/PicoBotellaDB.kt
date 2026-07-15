package com.example.pico_botella.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pico_botella.model.Reto
import com.example.pico_botella.utils.Constantes

// Base de datos SQLite de la app (Room). Singleton con doble verificación
// (@Volatile + synchronized), patrón del profesor (clase8).
@Database(entities = [Reto::class], version = 1, exportSchema = false)
abstract class PicoBotellaDB : RoomDatabase() {

    abstract fun retoDao(): RetoDao

    companion object {
        @Volatile
        private var INSTANCIA: PicoBotellaDB? = null

        fun obtenerBaseDatos(contexto: Context): PicoBotellaDB {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    PicoBotellaDB::class.java,
                    Constantes.NOMBRE_BASE_DATOS
                )
                    // App de desarrollo: ante un cambio de esquema sin migración,
                    // Room recrea la base de datos en vez de crashear. No hay datos de producción.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}
