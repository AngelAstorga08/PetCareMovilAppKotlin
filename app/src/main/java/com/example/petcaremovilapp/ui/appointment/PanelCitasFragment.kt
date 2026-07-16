package com.example.petcaremovilapp.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.petcaremovilapp.R

/**
 * Panel de citas (antes veterinario.xml). Sigue siendo un mockup:
 * el layout no tiene ids en sus vistas (estadísticas, filtros, lista
 * y paginación son todos datos de ejemplo escritos directamente en el
 * XML), así que aquí solo se registra el Fragment y se infla el
 * layout para que sea un destino real y navegable dentro del grafo
 * de navegación. Ver README - Pendientes para lo que falta.
 */
class PanelCitasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_panel_citas, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tv_back).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
