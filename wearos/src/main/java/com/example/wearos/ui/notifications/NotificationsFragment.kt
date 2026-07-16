package com.example.wearos.ui.notifications

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wearos.R

/**
 * Pantalla de notificaciones (screen_notifications.xml).
 * Destino final de la cadena de navegación de ejemplo; no navega a ningún
 * otro destino todavía (ver TODO en nav_graph_wear.xml).
 */
class NotificationsFragment : Fragment(R.layout.screen_notifications) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.linkActualizar).setOnClickListener {
            Toast.makeText(requireContext(), R.string.link_actualizar, Toast.LENGTH_SHORT).show()
        }
    }
}
