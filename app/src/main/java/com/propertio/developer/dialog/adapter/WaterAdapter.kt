package com.propertio.developer.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.database.MasterData
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

class WaterAdapter(
    private val waterTypes: List<MasterData>,
    private val onClickItemListener: (MasterData) -> Unit
) : RecyclerView.Adapter<WaterAdapter.WaterViewHolder>() {

    inner class WaterViewHolder(private val binding: ItemSimpleCardForRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(waterType: MasterData) {
            with(binding) {
                textViewItemCardOption.text = waterType.toUser
                cardViewItemCardOption.setOnClickListener { onClickItemListener(waterType) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding
            .inflate(LayoutInflater.from(parent.context),
                parent,
                false
            )

        return WaterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WaterViewHolder, position: Int) {
        holder.bind(waterTypes[position])
    }

    override fun getItemCount(): Int = waterTypes.size
}