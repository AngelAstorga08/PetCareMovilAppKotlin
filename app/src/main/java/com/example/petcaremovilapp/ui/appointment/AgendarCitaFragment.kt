package com.example.petcaremovilapp.ui.appointment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petcaremovilapp.R
import com.example.petcaremovilapp.models.dto.PetDto
import com.example.petcaremovilapp.models.dto.ServiceOption
import com.example.petcaremovilapp.models.dto.VeterinarianDto
import com.example.petcaremovilapp.ui.viewmodel.AppointmentViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AgendarCitaFragment : Fragment() {
    private val viewModel: AppointmentViewModel by viewModels()

    private var selectedVeterinarian: VeterinarianDto? = null
    private var selectedPet: PetDto? = null
    private var selectedService: ServiceOption? = null
    private var selectedDate: String? = null
    private var selectedHour: String? = null

    private lateinit var veterinarianInput: MaterialAutoCompleteTextView
    private lateinit var petInput: MaterialAutoCompleteTextView
    private lateinit var serviceInput: MaterialAutoCompleteTextView
    private lateinit var dateInput: TextInputEditText
    private lateinit var clinicText: TextView
    private lateinit var costText: TextView
    private lateinit var errorText: TextView
    private lateinit var resultText: TextView
    private lateinit var progress: ProgressBar
    private lateinit var confirmButton: MaterialButton
    private lateinit var hourAdapter: HourSlotAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_agendar_cita, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        configureStaticInputs()

        view.findViewById<TextView>(R.id.tv_back).setOnClickListener {
            findNavController().navigateUp()
        }
        dateInput.setOnClickListener { showDatePicker() }
        confirmButton.setOnClickListener { submit() }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            clinicText.text = state.clinic?.let { "${it.name}\n${it.location}" }
                ?: "Cargando clínica..."
            bindVeterinarians(state.veterinarians)
            bindPets(state.pets)
            hourAdapter.submitList(state.slots)
            progress.isVisible = state.loading || state.submitting
            confirmButton.isEnabled = !state.loading && !state.submitting
            errorText.isVisible = state.error != null
            errorText.text = state.error

            state.created?.let {
                resultText.text = "Cita agendada para ${it.pet_name}."
                resultText.isVisible = true
                Toast.makeText(requireContext(), "Cita agendada exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().previousBackStackEntry
                    ?.savedStateHandle?.set("appointment_created", true)
                viewModel.consumeCreated()
                findNavController().navigateUp()
            }
        }

        viewModel.loadForm()
    }

    private fun bindViews(view: View) {
        veterinarianInput = view.findViewById(R.id.act_veterinarian)
        petInput = view.findViewById(R.id.act_pet)
        serviceInput = view.findViewById(R.id.act_service)
        dateInput = view.findViewById(R.id.et_date)
        clinicText = view.findViewById(R.id.tv_clinic)
        costText = view.findViewById(R.id.tv_service_cost)
        errorText = view.findViewById(R.id.tv_error)
        resultText = view.findViewById(R.id.tv_result)
        progress = view.findViewById(R.id.progress_bar)
        confirmButton = view.findViewById(R.id.btn_confirm)
        hourAdapter = HourSlotAdapter { selectedHour = it }
        view.findViewById<RecyclerView>(R.id.rv_hours).apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = hourAdapter
        }
    }

    private fun configureStaticInputs() {
        val services = AppointmentViewModel.SERVICES
        serviceInput.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, services)
        )
        serviceInput.setOnItemClickListener { _, _, position, _ ->
            selectedService = services[position]
            costText.text = "Costo: $${services[position].price} MXN"
        }
    }

    private fun bindVeterinarians(values: List<VeterinarianDto>) {
        val currentNames = (veterinarianInput.adapter as? ArrayAdapter<*>)?.count ?: -1
        if (currentNames == values.size) return
        veterinarianInput.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                values.map { "Dr(a). ${it.name}" }
            )
        )
        veterinarianInput.setOnItemClickListener { _, _, position, _ ->
            selectedVeterinarian = values[position]
            selectedDate = null
            selectedHour = null
            dateInput.text = null
            viewModel.loadDates(values[position].id_user)
        }
    }

    private fun bindPets(values: List<PetDto>) {
        val currentCount = (petInput.adapter as? ArrayAdapter<*>)?.count ?: -1
        if (currentCount == values.size) return
        petInput.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                values.map { pet -> "${pet.name} · ${pet.species ?: pet.breed}" }
            )
        )
        petInput.setOnItemClickListener { _, _, position, _ -> selectedPet = values[position] }
    }

    private fun showDatePicker() {
        val veterinarian = selectedVeterinarian ?: run {
            showError("Selecciona primero un veterinario.")
            return
        }
        val available = viewModel.state.value?.availableDates.orEmpty()
        if (available.isEmpty()) {
            showError("Este veterinario aún no tiene fechas disponibles.")
            return
        }
        val today = LocalDate.now()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val value = LocalDate.of(year, month + 1, day)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE)
                if (value !in available) {
                    showError("La fecha seleccionada no está disponible.")
                    return@DatePickerDialog
                }
                selectedDate = value
                selectedHour = null
                dateInput.setText(
                    LocalDate.parse(value).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                )
                viewModel.loadSlots(veterinarian.id_user, value)
            },
            today.year,
            today.monthValue - 1,
            today.dayOfMonth
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1_000
            datePicker.maxDate = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1_000
        }.show()
    }

    private fun submit() {
        val veterinarian = selectedVeterinarian
        val pet = selectedPet
        val service = selectedService
        val date = selectedDate
        val hour = selectedHour
        if (veterinarian == null || pet == null || service == null || date == null || hour == null) {
            showError("Selecciona veterinario, fecha, hora, mascota y servicio.")
            return
        }
        viewModel.create(veterinarian.id_user, pet.id_pet, date, hour, service.name)
    }

    private fun showError(message: String) {
        errorText.text = message
        errorText.isVisible = true
    }
}
