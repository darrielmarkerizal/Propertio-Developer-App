package com.propertio.developer.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.database.MasterData
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

class InteriorAdapter(
    private val interiorTypes: List<MasterData>,
    private val onClickItemListener: (MasterData) -> Unit
) : RecyclerView.Adapter<InteriorAdapter.InteriorViewHolder>() {

    inner class InteriorViewHolder(private val binding: ItemSimpleCardForRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(interiorType: MasterData) {
            with(binding) {
                textViewItemCardOption.text = interiorType.toUser
                cardViewItemCardOption.setOnClickListener { onClickItemListener(interiorType) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InteriorViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding
            .inflate(LayoutInflater.from(parent.context),
                parent,
                false
            )

        return InteriorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InteriorViewHolder, position: Int) {
        holder.bind(interiorTypes[position])
    }

    override fun getItemCount(): Int = interiorTypes.size
}