package com.example.petcaremovilapp.ui.client

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.petcaremovilapp.R
import com.example.petcaremovilapp.models.entities.Client
import com.example.petcaremovilapp.models.dto.Result
import com.example.petcaremovilapp.ui.viewmodel.ClientViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class ClientFragment : Fragment() {

    private val viewModel: ClientViewModel by viewModels()
    private var modoEdicion = false
    private var claveOriginal: String? = null   // clave que se cargó desde el grid o búsqueda
    private var fechaSeleccionada: Date? = null
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_client, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvFormTitle = view.findViewById<TextView>(R.id.tv_form_title)
        val tilClave    = view.findViewById<TextInputLayout>(R.id.til_clave)
        val etClave     = view.findViewById<TextInputEditText>(R.id.et_clave)
        val etNombre    = view.findViewById<TextInputEditText>(R.id.et_nombre)
        val etEdad      = view.findViewById<TextInputEditText>(R.id.et_edad)
       // val etFecha     = view.findViewById<TextInputEditText>(R.id.et_fecha_nacimiento)
        //val tilFecha    = view.findViewById<TextInputLayout>(R.id.til_fecha_nacimiento)
        val btnNuevo    = view.findViewById<MaterialButton>(R.id.btn_nuevo)
        val btnGuardar  = view.findViewById<MaterialButton>(R.id.btn_guardar)
        val btnEliminar = view.findViewById<MaterialButton>(R.id.btn_eliminar)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val rvClientes  = view.findViewById<RecyclerView>(R.id.rv_clientes)
        val tvEmpty     = view.findViewById<TextView>(R.id.tv_empty)

        // ── Helpers ────────────────────────────────────────────────────

        fun setModo(edicion: Boolean) {
            modoEdicion = edicion
            tvFormTitle.text      = if (edicion) "Editar cliente" else "Nuevo cliente"
            btnEliminar.isEnabled = edicion
        }

        fun limpiar() {
            etClave.text?.clear()
            etNombre.text?.clear()
            etEdad.text?.clear()
            //etFecha.text?.clear()
            fechaSeleccionada = null
            claveOriginal     = null
            tilClave.error      = null
            tilClave.helperText = null
            setModo(false)
            etClave.requestFocus()
        }

        fun cargar(client: Client) {
            etClave.setText(client.clave)
            etNombre.setText(client.nombre)
            etEdad.setText(if (client.edad > 0) client.edad.toString() else "")
            fechaSeleccionada = client.fechaNacimiento
            //etFecha.setText(client.fechaNacimiento?.let { sdf.format(it) } ?: "")
            claveOriginal = client.clave   // guardar referencia para comparar después
            setModo(true)
        }

        fun buildClient() = Client(
            clave           = etClave.text.toString().trim(),
            nombre          = etNombre.text.toString().trim(),
            edad            = etEdad.text?.toString()?.toIntOrNull() ?: 0,
            fechaNacimiento = fechaSeleccionada
        )

        fun validar(): Boolean {
            return if (etClave.text.isNullOrBlank()) {
                tilClave.error = "La clave es requerida"
                false
            } else {
                tilClave.error = null
                true
            }
        }

        // ── DatePicker ─────────────────────────────────────────────────

        fun mostrarDatePicker() {
            val cal = Calendar.getInstance().apply { fechaSeleccionada?.let { time = it } }
            DatePickerDialog(requireContext(), { _, y, m, d ->
                cal.set(y, m, d)
                fechaSeleccionada = cal.time
               // etFecha.setText(sdf.format(cal.time))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        //etFecha.setOnClickListener { mostrarDatePicker() }
        //tilFecha.setEndIconOnClickListener { mostrarDatePicker() }

        // ── Adapter ────────────────────────────────────────────────────

        val adapter = ClientAdapter { cargar(it) }
        rvClientes.adapter = adapter

        // ── Botones ────────────────────────────────────────────────────

        btnNuevo.setOnClickListener { limpiar() }

        btnGuardar.setOnClickListener {
            if (!validar()) return@setOnClickListener
            val client = buildClient()
            val claveActual = client.clave

            // Update solo si: se cargó un cliente desde el grid/búsqueda
            // Y la clave no fue modificada por el usuario.
            // Si la clave cambió, se trata como un registro nuevo (create).
            if (modoEdicion && claveActual == claveOriginal) {
                viewModel.updateClient(client)
            } else {
                viewModel.createClient(client)
            }
        }

        btnEliminar.setOnClickListener {
            val clave = etClave.text?.toString()?.trim() ?: return@setOnClickListener
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar cliente")
                .setMessage("¿Deseas eliminar al cliente con clave \"$clave\"? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ -> viewModel.deleteClient(clave) }
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        // ── Búsqueda por clave ─────────────────────────────────────────

        etClave.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val clave = etClave.text?.toString()?.trim() ?: return@setOnFocusChangeListener
                if (clave.isNotEmpty()) viewModel.fetchClientByClave(clave)
            }
        }

        // ── Observers ──────────────────────────────────────────────────

        viewModel.clientes.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    adapter.submitList(result.data)
                    tvEmpty.visibility = if (result.data.isEmpty()) View.VISIBLE else View.GONE
                }
                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.clienteDetalle.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> tilClave.helperText = "Buscando..."
                is Result.Success -> { tilClave.helperText = null; cargar(result.data) }
                is Result.Error   -> { tilClave.helperText = "Clave disponible"; setModo(false) }
            }
        }

        viewModel.createResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Cliente creado", Toast.LENGTH_SHORT).show()
                    limpiar();
                    viewModel.fetchAllClients()
                }
                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error al crear: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Cliente actualizado", Toast.LENGTH_SHORT).show()
                    viewModel.fetchAllClients()
                }
                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error al actualizar: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show()
                    limpiar(); viewModel.fetchAllClients()
                }
                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error al eliminar: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.fetchAllClients()
    }
}