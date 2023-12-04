package com.propertio.developer.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.database.MasterData
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

class ElectricityAdapter(
    private val electricityType: List<MasterData>,
    private val onClickItemListener: (MasterData) -> Unit
) : RecyclerView.Adapter<ElectricityAdapter.ElectricityViewHolder>() {

    inner class ElectricityViewHolder(private val binding: ItemSimpleCardForRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(electricityType: MasterData) {
            with(binding) {
                textViewItemCardOption.text = electricityType.toUser
                cardViewItemCardOption.setOnClickListener { onClickItemListener(electricityType) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectricityViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding
            .inflate(LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ElectricityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ElectricityViewHolder, position: Int) {
        holder.bind(electricityType[position])
    }

    override fun getItemCount(): Int = electricityType.size
}