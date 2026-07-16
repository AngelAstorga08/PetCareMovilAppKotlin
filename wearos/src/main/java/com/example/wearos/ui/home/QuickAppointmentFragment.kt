package com.example.wearos.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wearos.R

/**
 * Pantalla inicial: resumen de la próxima cita (screen_quick_appointment.xml).
 * Destino de arranque del nav graph (nav_graph_wear.xml).
 */
class QuickAppointmentFragment : Fragment(R.layout.screen_quick_appointment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnVerTodas).setOnClickListener {
            findNavController().navigate(
                R.id.action_quickAppointmentFragment_to_appointmentsTodayFragment
            )
        }
    }
}
