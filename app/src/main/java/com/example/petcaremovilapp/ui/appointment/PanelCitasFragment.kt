package com.example.petcaremovilapp.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petcaremovilapp.R
import com.example.petcaremovilapp.data.api.ApiClient
import com.example.petcaremovilapp.models.dto.AppointmentDto
import com.example.petcaremovilapp.ui.viewmodel.AppointmentListViewModel
import com.google.android.material.button.MaterialButton

class PanelCitasFragment : Fragment() {
    private val viewModel: AppointmentListViewModel by viewModels()
    private val adapter = AppointmentAdapter(::confirmCancel, ::chooseStatus)
    private val role get() = ApiClient.session.roleId

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View = inflater.inflate(R.layout.fragment_panel_citas, container, false)
    override fun onViewCreated(view: View, state: Bundle?) {
        adapter.roleId = role
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)
        val error = view.findViewById<TextView>(R.id.tv_error)
        val empty = view.findViewById<TextView>(R.id.tv_empty)
        val newAppointment = view.findViewById<MaterialButton>(R.id.btn_new_appointment)
        view.findViewById<TextView>(R.id.tv_title).text = when (role) { 1 -> "Citas de mi clinica"; 2 -> "Citas pendientes"; else -> "Mis citas" }
        empty.text = when (role) { 1 -> "La clinica no tiene citas."; 2 -> "No tienes citas pendientes por atender."; else -> "Aun no tienes citas. Agenda la primera." }
        newAppointment.isVisible = role == 3
        view.findViewById<RecyclerView>(R.id.rv_appointments).apply { layoutManager = LinearLayoutManager(requireContext()); adapter = this@PanelCitasFragment.adapter }
        view.findViewById<TextView>(R.id.tv_back).setOnClickListener { findNavController().navigateUp() }
        newAppointment.setOnClickListener { findNavController().navigate(R.id.action_panelCitas_to_agendarCita) }
        viewModel.appointments.observe(viewLifecycleOwner) { values ->
            adapter.submitList(values)
            empty.isVisible = values.isEmpty() && viewModel.loading.value != true
            view.findViewById<TextView>(R.id.tv_pending_count).text = "${values.count { it.status.equals("pendiente", true) }}\nPendientes"
            view.findViewById<TextView>(R.id.tv_confirmed_count).text = "${values.count { it.status.equals("confirmada", true) }}\nConfirmadas"
        }
        viewModel.loading.observe(viewLifecycleOwner) { loading -> progress.isVisible = loading; empty.isVisible = !loading && viewModel.appointments.value.orEmpty().isEmpty() }
        viewModel.error.observe(viewLifecycleOwner) { message -> error.text = message; error.isVisible = message != null }
        viewModel.message.observe(viewLifecycleOwner) { message -> message?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show(); viewModel.consumeMessage() } }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("appointment_created")?.observe(viewLifecycleOwner) { if (it) { findNavController().currentBackStackEntry?.savedStateHandle?.set("appointment_created", false); viewModel.refresh(role) } }
    }
    override fun onResume() { super.onResume(); viewModel.refresh(role) }

    private fun confirmCancel(item: AppointmentDto) {
        AlertDialog.Builder(requireContext()).setTitle("Cancelar cita").setMessage("Deseas cancelar la cita de ${item.pet_name}?")
            .setNegativeButton("No", null).setPositiveButton("Si, cancelar") { _, _ -> viewModel.cancel(item.id_appointment) }.show()
    }
    private fun chooseStatus(item: AppointmentDto) {
        val values = arrayOf("pendiente", "confirmada", "atendida", "cancelada")
        AlertDialog.Builder(requireContext()).setTitle("Cambiar estado").setSingleChoiceItems(values, values.indexOf(item.status.lowercase())) { dialog, index ->
            if (!values[index].equals(item.status, true)) viewModel.changeStatus(item.id_appointment, values[index])
            dialog.dismiss()
        }.setNegativeButton("Cerrar", null).show()
    }
}
