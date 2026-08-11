package com.example.petcaremovilapp.ui.appointment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.example.petcaremovilapp.R
import com.example.petcaremovilapp.models.dto.AppointmentDto
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AppointmentAdapter(
    private val onCancel: ((AppointmentDto) -> Unit)? = null,
    private val onStatus: ((AppointmentDto) -> Unit)? = null
) : RecyclerView.Adapter<AppointmentAdapter.Holder>() {
    private var items: List<AppointmentDto> = emptyList()
    var roleId: Int = 3

    fun submitList(values: List<AppointmentDto>) {
        items = values
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return Holder(
            view,
            view.findViewById(R.id.tv_pet),
            view.findViewById(R.id.tv_detail),
            view.findViewById(R.id.tv_veterinarian),
            view.findViewById(R.id.tv_status),
            view.findViewById(R.id.btn_cancel),
            view.findViewById(R.id.btn_status)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.pet.text = item.pet_name
        holder.detail.text = "${item.service} · ${formatDate(item.date)} · $${item.cost.toInt()} MXN"
        holder.veterinarian.text = if (roleId == 3) "Dr(a). ${item.veterinarian_name}" else "Cliente: ${item.user_name}"
        holder.status.text = item.status.replaceFirstChar { it.uppercase() }
        val color = when (item.status.lowercase()) {
            "confirmada" -> R.color.appointment_confirmed
            "atendida" -> R.color.appointment_attended
            "cancelada" -> R.color.appointment_cancelled
            else -> R.color.appointment_pending
        }
        holder.status.setTextColor(ContextCompat.getColor(holder.status.context, color))
        val terminal = item.status.lowercase() in setOf("atendida", "cancelada")
        holder.cancel.isVisible = roleId == 3 && !terminal && onCancel != null
        holder.changeStatus.isVisible = roleId == 1 && !terminal && onStatus != null
        holder.cancel.setOnClickListener { onCancel?.invoke(item) }
        holder.changeStatus.setOnClickListener { onStatus?.invoke(item) }
    }

    override fun getItemCount(): Int = items.size

    private fun formatDate(value: String): String = runCatching {
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(
            Instant.parse(value).atZone(ZoneId.of("America/Chihuahua"))
        )
    }.getOrDefault(value)

    class Holder(
        itemView: android.view.View,
        val pet: TextView,
        val detail: TextView,
        val veterinarian: TextView,
        val status: TextView,
        val cancel: MaterialButton,
        val changeStatus: MaterialButton
    ) : RecyclerView.ViewHolder(itemView)
}
