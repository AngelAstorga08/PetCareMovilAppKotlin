package com.example.petcaremovilapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.example.petcaremovilapp.R

/**
 * Home / menú de navegación de PetCare.
 *
 * Contiene exactamente lo que antes vivía en activity_main.xml
 * (saludo, tarjetas de acceso a Citas/Mascotas/Perfil/Administrar y
 * próximas citas). MainActivity ahora solo aloja el NavHostFragment;
 * esta pantalla es el verdadero destino de "inicio" dentro del grafo
 * de navegación.
 *
 * NOTA: cardAppointments y tvLogout ya navegan (a panelCitasFragment y
 * loginFragment respectivamente). cardPets y cardProfile siguen sin
 * cableado porque esas pantallas no existen todavía (ver README).
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardAppointments = view.findViewById<CardView>(R.id.card_appointments)
        val cardPets = view.findViewById<CardView>(R.id.card_pets)
        val cardProfile = view.findViewById<CardView>(R.id.card_profile)
        val tvLogout = view.findViewById<TextView>(R.id.tv_logout)

        // Cableado real: cardAppointments y tvLogout ya tienen destino en nav_graph.xml.
        // cardPets y cardProfile siguen sin destino porque esas pantallas no existen
        // todavía (ver README - pendientes); no se navega a nada inventado.
        cardAppointments.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_panelCitas)
        }
        cardPets.setOnClickListener {
            // TODO: pantalla de mascotas, no existe todavía
        }
        cardProfile.setOnClickListener {
            // TODO: pantalla de perfil, no existe todavía
        }
        tvLogout.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_login)
        }
    }
}
