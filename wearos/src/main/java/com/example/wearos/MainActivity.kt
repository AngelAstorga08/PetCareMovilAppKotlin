package com.example.wearos

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.wear.widget.SwipeDismissFrameLayout

/**
 * MainActivity ahora solo aloja el NavHostFragment (ver activity_main.xml) y
 * maneja el gesto de "deslizar para volver" propio de Wear OS mediante
 * SwipeDismissFrameLayout. Toda la UI real vive en los Fragments dentro de
 * ui/ (QuickAppointmentFragment, AppointmentsTodayFragment,
 * NotificationsFragment), coordinados por nav_graph_wear.xml.
 *
 * Extiende FragmentActivity (no AppCompatActivity) porque el módulo no
 * declara la dependencia de appcompat y el manifest usa el tema nativo
 * @android:style/Theme.DeviceDefault. FragmentActivity es lo mínimo que
 * necesita FragmentContainerView / NavHostFragment para funcionar.
 */
class MainActivity : FragmentActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val swipeLayout = findViewById<SwipeDismissFrameLayout>(R.id.swipeDismissRoot)
        swipeLayout.addCallback(object : SwipeDismissFrameLayout.Callback() {
            override fun onDismissed(layout: SwipeDismissFrameLayout) {
                // Si hay pantallas anteriores en el back stack, el swipe
                // navega hacia atrás dentro de la app (patrón estándar de
                // Wear OS con Fragments + SwipeDismissFrameLayout). Si ya
                // estamos en el destino de inicio, dejamos que el sistema
                // cierre la Activity, igual que el swipe-to-dismiss nativo.
                layout.visibility = View.VISIBLE
                if (!navController.popBackStack()) {
                    finish()
                }
            }
        })
    }
}
