package com.example.petcaremovilapp.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petcaremovilapp.R
import com.example.petcaremovilapp.data.api.ApiClient
import com.example.petcaremovilapp.ui.viewmodel.AppointmentListViewModel
import com.google.android.material.button.MaterialButton

class PanelCitasFragment : Fragment() {
    private val viewModel: AppointmentListViewModel by viewModels()
    private val adapter = AppointmentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_panel_citas, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)
        val error = view.findViewById<TextView>(R.id.tv_error)
        val empty = view.findViewById<TextView>(R.id.tv_empty)
        val newAppointment = view.findViewById<MaterialButton>(R.id.btn_new_appointment)

        view.findViewById<RecyclerView>(R.id.rv_appointments).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PanelCitasFragment.adapter
        }
        view.findViewById<TextView>(R.id.tv_back).setOnClickListener {
            findNavController().navigateUp()
        }
        newAppointment.setOnClickListener {
            findNavController().navigate(R.id.action_panelCitas_to_agendarCita)
        }

        if (ApiClient.session.roleId != CLIENT_ROLE) {
            newAppointment.isVisible = false
            error.text = "Esta pantalla de citas está disponible para clientes."
            error.isVisible = true
            return
        }

        viewModel.appointments.observe(viewLifecycleOwner) { appointments ->
            adapter.submitList(appointments)
            empty.isVisible = appointments.isEmpty() && viewModel.loading.value != true
            view.findViewById<TextView>(R.id.tv_pending_count).text =
                "${appointments.count { it.status.equals("pendiente", true) }}\nPendientes"
            view.findViewById<TextView>(R.id.tv_confirmed_count).text =
                "${appointments.count { it.status.equals("confirmada", true) }}\nConfirmadas"
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            progress.isVisible = it
            empty.isVisible = !it && viewModel.appointments.value.orEmpty().isEmpty()
        }
        viewModel.error.observe(viewLifecycleOwner) {
            error.text = it
            error.isVisible = it != null
        }
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean>("appointment_created")
            ?.observe(viewLifecycleOwner) { created ->
                if (created) {
                    Toast.makeText(requireContext(), "Tu cita ya aparece en la lista", Toast.LENGTH_SHORT).show()
                    findNavController().currentBackStackEntry
                        ?.savedStateHandle?.set("appointment_created", false)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        if (ApiClient.session.roleId == CLIENT_ROLE) viewModel.refresh()
    }

    private companion object {
        const val CLIENT_ROLE = 3
    }
}
