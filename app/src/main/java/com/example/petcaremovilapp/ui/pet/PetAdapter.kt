package com.example.petcaremovilapp.ui.pet

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petcaremovilapp.R
import com.example.petcaremovilapp.models.dto.PetDto
import com.google.android.material.button.MaterialButton

class PetAdapter(private val edit: (PetDto) -> Unit, private val delete: (PetDto) -> Unit) : RecyclerView.Adapter<PetAdapter.Holder>() {
    private var items = emptyList<PetDto>()
    fun submitList(values: List<PetDto>) { items = values; notifyDataSetChanged() }
    override fun getItemCount() = items.size
    override fun onCreateViewHolder(parent: ViewGroup, type: Int): Holder = LayoutInflater.from(parent.context).inflate(R.layout.item_pet, parent, false).let { Holder(it as android.view.ViewGroup) }
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])
    inner class Holder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        fun bind(item: PetDto) {
            itemView.findViewById<TextView>(R.id.tv_pet_name).text = item.name
            itemView.findViewById<TextView>(R.id.tv_pet_detail).text = "${item.species ?: "Mascota"} - ${item.breed} - ${item.age} anos - ${item.weight} kg"
            itemView.findViewById<MaterialButton>(R.id.btn_edit).setOnClickListener { edit(item) }
            itemView.findViewById<MaterialButton>(R.id.btn_delete).setOnClickListener { delete(item) }
        }
    }
}
