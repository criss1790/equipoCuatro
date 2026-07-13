package com.example.pico_botella.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pico_botella.repository.AlmacenamientoCalificacionLocal
import com.example.pico_botella.repository.RepositorioCalificacion

// Crea instancias de CalificacionViewModel entregándole el repositorio de calificación,
// igual que FabricaRetosViewModel hace para RetosViewModel.
class FabricaCalificacionViewModel(
    contexto: Context,
    private val repositorioCalificacion: RepositorioCalificacion = AlmacenamientoCalificacionLocal(contexto)
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalificacionViewModel(repositorioCalificacion) as T
    }
}
