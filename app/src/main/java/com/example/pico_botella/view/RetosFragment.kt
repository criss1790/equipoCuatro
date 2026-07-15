package com.example.pico_botella.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pico_botella.databinding.FragmentRetosBinding
import com.example.pico_botella.model.Reto
import com.example.pico_botella.view.adapter.RetoAdapter
import com.example.pico_botella.view.dialog.AgregarRetoDialog
import com.example.pico_botella.view.dialog.EditarRetoDialog
import com.example.pico_botella.viewmodel.RetoViewModel

// Pantalla de listado de retos (HU 6.0). Estructura del profesor:
// controladores() (listeners, RecyclerView) + observadorViewModel() (observe).
class RetosFragment : Fragment() {

    private lateinit var binding: FragmentRetosBinding
    private lateinit var viewModel: RetoViewModel
    private lateinit var adaptador: RetoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRetosBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RetoViewModel::class.java]
        controladores()
        observadorViewModel()
        viewModel.listarRetos()
    }

    // Listeners, RecyclerView y creación ÚNICA del adaptador.
    private fun controladores() {
        adaptador = RetoAdapter(
            listaRetos = emptyList(),
            alEditar = { reto ->
                EditarRetoDialog(requireContext(), reto) { retoActualizado ->
                    viewModel.actualizarReto(retoActualizado)
                }.mostrar()
            },
            alEliminar = { /* TODO HU 9.0: EliminarRetoDialog */ }
        )
        binding.listaRetos.layoutManager = LinearLayoutManager(requireContext())
        binding.listaRetos.adapter = adaptador

        binding.iconoVolver.setOnClickListener {
            requireActivity().finish()
        }

        binding.botonFlotanteAgregar.setOnClickListener {
            AgregarRetoDialog(requireContext()) { textoReto ->
                viewModel.insertarReto(Reto(texto = textoReto))
            }.mostrar()
        }
    }

    // Observadores: solo actualizan el adaptador, nunca lo re-crean.
    private fun observadorViewModel() {
        viewModel.listaRetos.observe(viewLifecycleOwner) { lista ->
            adaptador.actualizarLista(lista)
        }
        viewModel.progresState.observe(viewLifecycleOwner) { cargando ->
            binding.barraProgreso.visibility = if (cargando) View.VISIBLE else View.GONE
        }
    }
}
