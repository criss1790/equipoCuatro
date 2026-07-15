package com.example.pico_botella.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView // <- NUEVA IMPORTACIÓN
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide // <- NUEVA IMPORTACIÓN PARA CARGAR IMÁGENES
import com.example.pico_botella.R
import com.example.pico_botella.model.Reto

class RetoDialogFragment(
    private val reto: Reto,
    private val alCerrar: () -> Unit
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hace transparente el fondo de la ventana para que se aprecien las esquinas redondeadas
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_reto_personalizado, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // CONFIGURACIÓN DEL CRITERIO 6: Bloquear toques externos
        // Impide que el diálogo se cierre al tocar fuera de su área
        dialog?.setCanceledOnTouchOutside(false)
        // Impide que se cierre usando el botón "Atrás" físico del teléfono
        dialog?.setCancelable(false)

        val txtReto = view.findViewById<TextView>(R.id.txtTextoReto)
        val btnCerrar = view.findViewById<Button>(R.id.btnCerrar)

        // Buscamos el ImageView en el XML
        val imgPokemon = view.findViewById<ImageView>(R.id.imgPokemon)

        // Criterio 3: Seteamos el texto del reto aleatorio
        txtReto.text = reto.texto

        // Si el reto tiene una imagen de Pokémon asociada y el ImageView existe en el diseño, la cargamos
        if (imgPokemon != null && !reto.pokemonImageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(reto.pokemonImageUrl)
                .into(imgPokemon)
        }

        // CONFIGURACIÓN DEL CRITERIO 5: Acción del botón cerrar
        btnCerrar.setOnClickListener {
            dismiss()   // 1. Hace desaparecer este diálogo
            alCerrar()  // 2. Avisa a la MainActivity para que deje el juego listo para otra partida
        }
    }

    override fun onStart() {
        super.onStart()
        // Forzamos el ancho del diálogo para que ocupe casi toda la pantalla
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}