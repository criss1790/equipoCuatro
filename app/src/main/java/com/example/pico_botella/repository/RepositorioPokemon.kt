package com.example.pico_botella.repository

import com.example.pico_botella.model.PokemonResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RepositorioPokemon {

    // Inicializamos Retrofit de forma privada dentro de nuestro repositorio
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Usamos el servicio de la API (asegúrate de que esté en el mismo paquete o importado)
    private val apiService = retrofit.create(PokemonApiService::class.java)

    /**
     * Hace la consulta a la API y devuelve la respuesta completa
     */
    suspend fun obtenerPokedex(): PokemonResponse {
        return apiService.obtenerPokedex()
    }
}