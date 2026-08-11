package com.example.petcaremovilapp.ui.pet

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
import com.example.petcaremovilapp.models.dto.PetDto
import com.example.petcaremovilapp.models.dto.PetRequest
import com.example.petcaremovilapp.ui.viewmodel.PetViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class PetsFragment : Fragment() {
    private val viewModel: PetViewModel by viewModels()
    private lateinit var adapter: PetAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View = inflater.inflate(R.layout.fragment_pets, container, false)
    override fun onViewCreated(view: View, state: Bundle?) {
        adapter = PetAdapter(::showEditor, ::confirmDelete)
        view.findViewById<RecyclerView>(R.id.rv_pets).apply { layoutManager = LinearLayoutManager(requireContext()); adapter = this@PetsFragment.adapter }
        view.findViewById<TextView>(R.id.tv_back).setOnClickListener { findNavController().navigateUp() }
        view.findViewById<MaterialButton>(R.id.btn_new_pet).setOnClickListener { showEditor(null) }
        viewModel.state.observe(viewLifecycleOwner) { ui ->
            adapter.submitList(ui.pets)
            view.findViewById<ProgressBar>(R.id.progress_bar).isVisible = ui.loading
            view.findViewById<TextView>(R.id.tv_empty).isVisible = !ui.loading && ui.error == null && ui.pets.isEmpty()
            view.findViewById<TextView>(R.id.tv_error).apply { text = ui.error; isVisible = ui.error != null }
            ui.message?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show(); viewModel.consumeMessage() }
        }
    }
    override fun onResume() { super.onResume(); viewModel.refresh() }

    private fun showEditor(pet: PetDto?) {
        val form = layoutInflater.inflate(R.layout.dialog_pet, null)
        val name = form.findViewById<TextInputEditText>(R.id.et_name)
        val species = form.findViewById<TextInputEditText>(R.id.et_species)
        val breed = form.findViewById<TextInputEditText>(R.id.et_breed)
        val weight = form.findViewById<TextInputEditText>(R.id.et_weight)
        val age = form.findViewById<TextInputEditText>(R.id.et_age)
        pet?.let { name.setText(it.name); species.setText(it.species); breed.setText(it.breed); weight.setText(it.weight.toString()); age.setText(it.age.toString()) }
        val dialog = AlertDialog.Builder(requireContext()).setTitle(if (pet == null) "Nueva mascota" else "Editar mascota").setView(form)
            .setNegativeButton("Cancelar", null).setPositiveButton("Guardar", null).create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val request = PetRequest(name.text.toString().trim(), breed.text.toString().trim(), species.text.toString().trim().ifBlank { null }, weight.text.toString().toDoubleOrNull() ?: 0.0, age.text.toString().toIntOrNull() ?: -1)
                if (request.name.isBlank() || request.breed.isBlank() || request.weight <= 0 || request.age < 0) Toast.makeText(requireContext(), "Completa los datos correctamente", Toast.LENGTH_SHORT).show()
                else { viewModel.save(pet?.id_pet, request); dialog.dismiss() }
            }
        }
        dialog.show()
    }
    private fun confirmDelete(pet: PetDto) {
        AlertDialog.Builder(requireContext()).setTitle("Eliminar mascota").setMessage("Eliminar a ${pet.name}?")
            .setNegativeButton("Cancelar", null).setPositiveButton("Eliminar") { _, _ -> viewModel.delete(pet.id_pet) }.show()
    }
}
