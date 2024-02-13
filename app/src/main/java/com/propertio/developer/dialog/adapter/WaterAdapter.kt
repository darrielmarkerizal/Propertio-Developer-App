package com.propertio.developer.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.database.MasterData
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

class WaterAdapter(
    private val onClickItemListener: (MasterData) -> Unit
) : ListAdapter<MasterData, WaterAdapter.WaterViewHolder>(DiffUtilCallback()) {

    inner class WaterViewHolder(private val binding: ItemSimpleCardForRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(waterType: MasterData) {
            with(binding) {
                textViewItemCardOption.text = waterType.toUser
                cardViewItemCardOption.setOnClickListener { onClickItemListener(waterType) }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<MasterData>() {
        override fun areItemsTheSame(oldItem: MasterData, newItem: MasterData): Boolean {
            return oldItem.toDb == newItem.toDb
        }

        override fun areContentsTheSame(oldItem: MasterData, newItem: MasterData): Boolean {
            return oldItem.toString() == newItem.toString()
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
        holder.bind(getItem(position))
    }


}