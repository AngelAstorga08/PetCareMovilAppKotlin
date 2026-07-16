package com.example.wearos.ui.appointments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wearos.R

/**
 * Pantalla de citas del día (screen_appointments_today.xml).
 * Se llega aquí desde QuickAppointmentFragment (botón "Ver todas").
 */
class AppointmentsTodayFragment : Fragment(R.layout.screen_appointments_today) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: reemplazar este destino cuando exista el listado completo real
        // de citas; por ahora solo demuestra el patrón de navegación.
        view.findViewById<View>(R.id.linkVerTodas).setOnClickListener {
            findNavController().navigate(
                R.id.action_appointmentsTodayFragment_to_notificationsFragment
            )
        }
    }
}
