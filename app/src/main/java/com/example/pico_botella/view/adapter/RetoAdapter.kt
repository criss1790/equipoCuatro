package com.example.pico_botella.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pico_botella.databinding.ItemRetoBinding
import com.example.pico_botella.model.Reto

// Adaptador del listado de retos. No conoce el ViewModel ni Room:
// expone callbacks hacia el Fragment (onEditar → HU 8.0, onEliminar → HU 9.0).
class RetoAdapter(
    private var listaRetos: List<Reto>,
    private val alEditar: (Reto) -> Unit,
    private val alEliminar: (Reto) -> Unit
) : RecyclerView.Adapter<RetoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetoViewHolder {
        val binding = ItemRetoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RetoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RetoViewHolder, position: Int) {
        val reto = listaRetos[position]
        holder.binding.textoReto.text = reto.texto          // campo real: texto
        holder.binding.iconoEditar.setOnClickListener { alEditar(reto) }
        holder.binding.iconoEliminar.setOnClickListener { alEliminar(reto) }
    }

    override fun getItemCount(): Int = listaRetos.size

    // Reemplaza la lista y refresca. Sin DiffUtil (no lo enseñó el profesor).
    fun actualizarLista(nuevaLista: List<Reto>) {
        this.listaRetos = nuevaLista
        notifyDataSetChanged()
    }
}
