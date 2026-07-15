package com.example.pico_botella.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pico_botella.repository.RepositorioRetosFalso
import com.example.pico_botella.repository.RepositorioRetos

// Crea instancias de RetosViewModel entregándole el repositorio que necesita,
// ya que el ViewModel no tiene un constructor sin parámetros.
class FabricaRetosViewModel(
    private val repositorioRetos: RepositorioRetos = RepositorioRetosFalso()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RetosViewModel(repositorioRetos) as T
    }
}
