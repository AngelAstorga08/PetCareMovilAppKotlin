package com.example.petcaremovilapp.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.petcaremovilapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

/**
 * Formulario "Agendar cita" (antes vivía, mal ubicado, en item_cliente.xml
 * y no era inflado por ningún Fragment). Este Fragment solo hace el
 * cableado mínimo de vistas para que el proyecto compile.
 *
 * NOTA: no existe todavía ningún endpoint de citas en ApiService.kt,
 * así que btn_confirm no llama a ningún ViewModel/Repository real.
 * Eso queda pendiente (ver README - Pendientes).
 *
 * NOTA 2: este Fragment todavía no tiene ninguna <action> real en
 * nav_graph.xml que navegue hacia él (ninguna tarjeta de HomeFragment
 * apunta aquí todavía), así que hoy no es alcanzable desde la UI.
 * El botón "Volver" se dejó cableado con findNavController().navigateUp()
 * para cuando sí lo sea.
 */
class AgendarCitaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_agendar_cita, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actVeterinarian = view.findViewById<MaterialAutoCompleteTextView>(R.id.act_veterinarian)
        val etDate = view.findViewById<TextInputEditText>(R.id.et_date)
        val etPetName = view.findViewById<TextInputEditText>(R.id.et_pet_name)
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btn_confirm)

        // TODO: cargar veterinarios reales, DatePicker para et_date,
        // y llamar a un CitaViewModel/CitaRepository cuando exista el
        // endpoint correspondiente en ApiService.kt.
        btnConfirm.setOnClickListener { /* TODO: implementar creación de cita */ }

        view.findViewById<TextView>(R.id.tv_back).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
