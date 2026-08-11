package com.example.petcaremovilapp.ui.profile

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
import com.example.petcaremovilapp.R
import com.example.petcaremovilapp.ui.viewmodel.ProfileViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()
    private var boundId: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View = inflater.inflate(R.layout.fragment_profile, container, false)
    override fun onViewCreated(view: View, state: Bundle?) {
        val name = view.findViewById<TextInputEditText>(R.id.et_name)
        val email = view.findViewById<TextInputEditText>(R.id.et_email)
        val phone = view.findViewById<TextInputEditText>(R.id.et_phone)
        val save = view.findViewById<MaterialButton>(R.id.btn_save)
        view.findViewById<TextView>(R.id.tv_back).setOnClickListener { findNavController().navigateUp() }
        save.setOnClickListener {
            val value = name.text.toString().trim()
            if (value.isBlank()) Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            else viewModel.save(value, email.text.toString(), phone.text.toString().trim().ifBlank { null })
        }
        viewModel.state.observe(viewLifecycleOwner) { ui ->
            ui.profile?.let { profile ->
                if (boundId != profile.id_user || ui.saved) {
                    boundId = profile.id_user; name.setText(profile.name); email.setText(profile.email); phone.setText(profile.phone.orEmpty())
                }
                view.findViewById<TextView>(R.id.tv_role).text = "Rol: ${when (profile.id_role) { 1 -> "Administrador"; 2 -> "Veterinario"; else -> "Cliente" }}"
            }
            view.findViewById<ProgressBar>(R.id.progress_bar).isVisible = ui.loading
            view.findViewById<TextView>(R.id.tv_error).apply { text = ui.error; isVisible = ui.error != null }
            save.isEnabled = !ui.loading
            if (ui.saved) Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
        }
        viewModel.load()
    }
}
