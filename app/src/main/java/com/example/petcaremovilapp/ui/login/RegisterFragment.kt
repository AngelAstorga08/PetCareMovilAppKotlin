package com.example.petcaremovilapp.ui.login

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
import com.example.petcaremovilapp.models.dto.Result
import com.example.petcaremovilapp.ui.viewmodel.LoginViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View = inflater.inflate(R.layout.fragment_register, container, false)
    override fun onViewCreated(view: View, state: Bundle?) {
        val name = view.findViewById<TextInputEditText>(R.id.et_name)
        val email = view.findViewById<TextInputEditText>(R.id.et_email)
        val phone = view.findViewById<TextInputEditText>(R.id.et_phone)
        val password = view.findViewById<TextInputEditText>(R.id.et_password)
        val button = view.findViewById<MaterialButton>(R.id.btn_register)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)
        val error = view.findViewById<TextView>(R.id.tv_error)
        view.findViewById<TextView>(R.id.tv_back).setOnClickListener { findNavController().navigateUp() }
        button.setOnClickListener {
            val n = name.text.toString().trim(); val e = email.text.toString().trim(); val p = password.text.toString()
            if (n.isBlank() || e.isBlank() || p.length < 6) {
                error.text = "Completa nombre, correo y una contrasena de al menos 6 caracteres."; error.isVisible = true
            } else viewModel.register(n, e, p, phone.text.toString().trim().ifBlank { null })
        }
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            progress.isVisible = result is Result.Loading; button.isEnabled = result !is Result.Loading
            when (result) {
                is Result.Success -> { Toast.makeText(requireContext(), "Cuenta creada. Inicia sesion.", Toast.LENGTH_LONG).show(); findNavController().navigateUp() }
                is Result.Error -> { error.text = result.message; error.isVisible = true }
                else -> Unit
            }
        }
    }
}
