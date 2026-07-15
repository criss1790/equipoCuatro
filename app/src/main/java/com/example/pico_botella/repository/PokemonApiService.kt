package com.example.pico_botella.repository

import com.example.pico_botella.model.PokemonResponse
import retrofit2.http.GET

interface PokemonApiService {
    // La dirección base de la API
    // Definimos la ruta restante para obtener el JSON de la Pokedex:
    @GET("Biuni/PokemonGO-Pokedex/master/pokedex.json")
    suspend fun obtenerPokedex(): PokemonResponse
}