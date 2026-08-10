package com.example.petcaremovilapp.ui.appointment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petcaremovilapp.R

class HourSlotAdapter(private val onSelected: (String) -> Unit) :
    RecyclerView.Adapter<HourSlotAdapter.Holder>() {
    private var items: List<String> = emptyList()
    private var selected: String? = null

    fun submitList(values: List<String>) {
        items = values
        selected = null
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hour_slot, parent, false) as TextView
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val value = items[position]
        holder.text.text = value
        holder.text.isSelected = value == selected
        holder.text.setOnClickListener {
            val old = selected
            selected = value
            old?.let { previous -> notifyItemChanged(items.indexOf(previous)) }
            notifyItemChanged(position)
            onSelected(value)
        }
    }

    override fun getItemCount(): Int = items.size
    class Holder(val text: TextView) : RecyclerView.ViewHolder(text)
}
