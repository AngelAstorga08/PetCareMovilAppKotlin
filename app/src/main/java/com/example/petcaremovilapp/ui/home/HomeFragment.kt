package com.example.petcaremovilapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petcaremovilapp.R
import com.example.petcaremovilapp.data.api.ApiClient
import com.example.petcaremovilapp.ui.appointment.AppointmentAdapter
import com.example.petcaremovilapp.ui.viewmodel.HomeViewModel

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private val adapter = AppointmentAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View = inflater.inflate(R.layout.fragment_home, container, false)
    override fun onViewCreated(view: View, state: Bundle?) {
        val role = ApiClient.session.roleId
        adapter.roleId = role
        view.findViewById<TextView>(R.id.tv_user_name).text = ApiClient.session.userName
        view.findViewById<TextView>(R.id.tv_user_role).text = when (role) {
            1 -> "Administrador"
            2 -> "Veterinario"
            else -> "Cliente"
        }
        val petsCard = view.findViewById<CardView>(R.id.card_pets)
        petsCard.isVisible = role == 3
        view.findViewById<TextView>(R.id.tv_appointments_label).text = when (role) { 1 -> "Citas de la clinica"; 2 -> "Citas por atender"; else -> "Mis citas" }
        val group = view.findViewById<View>(R.id.group_upcoming)
        group.isVisible = role != 1
        view.findViewById<TextView>(R.id.tv_upcoming_title).text = if (role == 2) "Proximas citas por atender" else "Proximas citas"
        view.findViewById<RecyclerView>(R.id.rv_home_appointments).apply { layoutManager = LinearLayoutManager(requireContext()); adapter = this@HomeFragment.adapter }
        view.findViewById<CardView>(R.id.card_appointments).setOnClickListener { findNavController().navigate(R.id.action_home_to_panelCitas) }
        petsCard.setOnClickListener { findNavController().navigate(R.id.action_home_to_pets) }
        view.findViewById<CardView>(R.id.card_profile).setOnClickListener { findNavController().navigate(R.id.action_home_to_profile) }
        view.findViewById<TextView>(R.id.tv_logout).setOnClickListener { ApiClient.session.clear(); findNavController().navigate(R.id.action_home_to_login) }
        viewModel.state.observe(viewLifecycleOwner) { ui ->
            adapter.submitList(ui.appointments)
            view.findViewById<ProgressBar>(R.id.progress_bar).isVisible = ui.loading
            view.findViewById<TextView>(R.id.tv_home_empty).isVisible = !ui.loading && ui.error == null && ui.appointments.isEmpty()
            view.findViewById<TextView>(R.id.tv_home_error).apply { text = ui.error; isVisible = ui.error != null }
            view.findViewById<TextView>(R.id.tv_appointments_count).text = if (role == 1) "Ver y gestionar" else "${ui.appointments.size} proximas"
            view.findViewById<TextView>(R.id.tv_pets_count).text = "${ui.petCount} registradas"
        }
    }
    override fun onResume() { super.onResume(); view?.findViewById<TextView>(R.id.tv_user_name)?.text = ApiClient.session.userName; viewModel.refresh(ApiClient.session.roleId) }
}
