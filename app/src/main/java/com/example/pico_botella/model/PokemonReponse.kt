package com.example.pico_botella.model

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    // Mapea la lista de pokémon que viene de la API
    @SerializedName("pokemon") val pokemonList: List<Pokemon>
)

data class Pokemon(
    val id: Int,
    val name: String,
    // Mapea la URL de la imagen del pokémon
    @SerializedName("img") val imageUrl: String
)