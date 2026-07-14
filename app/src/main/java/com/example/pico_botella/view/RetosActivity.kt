package com.example.pico_botella.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pico_botella.R

// Activity contenedora del NavHost (Navigation Component). Aloja el RetosFragment
// (listado de retos). Se lanza desde el ícono "agregar" de la toolbar de MainActivity,
// para no alterar la estructura de MainActivity.
class RetosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_retos)
    }
}
