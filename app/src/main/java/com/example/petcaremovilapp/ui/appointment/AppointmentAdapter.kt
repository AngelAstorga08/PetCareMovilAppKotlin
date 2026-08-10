package com.example.petcaremovilapp.ui.appointment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.petcaremovilapp.R
import com.example.petcaremovilapp.models.dto.AppointmentDto
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AppointmentAdapter : RecyclerView.Adapter<AppointmentAdapter.Holder>() {
    private var items: List<AppointmentDto> = emptyList()

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
            view.findViewById(R.id.tv_status)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.pet.text = item.pet_name
        holder.detail.text = "${item.service} · ${formatDate(item.date)} · $${item.cost.toInt()} MXN"
        holder.veterinarian.text = "Dr(a). ${item.veterinarian_name}"
        holder.status.text = item.status.replaceFirstChar { it.uppercase() }
        val color = when (item.status.lowercase()) {
            "confirmada" -> R.color.appointment_confirmed
            "atendida" -> R.color.appointment_attended
            "cancelada" -> R.color.appointment_cancelled
            else -> R.color.appointment_pending
        }
        holder.status.setTextColor(ContextCompat.getColor(holder.status.context, color))
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
        val status: TextView
    ) : RecyclerView.ViewHolder(itemView)
}
