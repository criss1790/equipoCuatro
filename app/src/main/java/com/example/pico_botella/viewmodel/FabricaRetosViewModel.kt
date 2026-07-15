package com.example.pico_botella.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pico_botella.repository.RepositorioRetos
import com.example.pico_botella.repository.RepositorioPokemon // <- Asegúrate de tener este import

class FabricaRetosViewModel(
    private val repositorioRetos: RepositorioRetos,
    // Al ponerle "= RepositorioPokemon()", si no se lo pasas en la MainActivity, Kotlin lo crea solito
    private val repositorioPokemon: RepositorioPokemon = RepositorioPokemon()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RetosViewModel::class.java)) {
            return RetosViewModel(repositorioRetos, repositorioPokemon) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}