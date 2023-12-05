package com.propertio.developer.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.database.MasterData
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

class RoadAccessAdapter(
    private val roadAccessTypes: List<MasterData>,
    private val onClickItemListener: (MasterData) -> Unit
) : RecyclerView.Adapter<RoadAccessAdapter.RoadAccessViewHolder>() {

    inner class RoadAccessViewHolder(private val binding: ItemSimpleCardForRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(roadAccessType: MasterData) {
            with(binding) {
                textViewItemCardOption.text = roadAccessType.toUser
                cardViewItemCardOption.setOnClickListener { onClickItemListener(roadAccessType) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadAccessViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding
            .inflate(LayoutInflater.from(parent.context),
                parent,
                false
            )

        return RoadAccessViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoadAccessViewHolder, position: Int) {
        holder.bind(roadAccessTypes[position])
    }

    override fun getItemCount(): Int = roadAccessTypes.size
}