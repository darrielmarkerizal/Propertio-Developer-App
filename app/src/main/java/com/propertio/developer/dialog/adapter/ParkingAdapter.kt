package com.propertio.developer.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.database.MasterData
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

class ParkingAdapter(
    private val parkingTypes: List<MasterData>,
    private val onClickItemListener: (MasterData) -> Unit
) : RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder>() {

    inner class ParkingViewHolder(private val binding: ItemSimpleCardForRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parkingType: MasterData) {
            with(binding) {
                textViewItemCardOption.text = parkingType.toUser
                cardViewItemCardOption.setOnClickListener { onClickItemListener(parkingType) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding
            .inflate(LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ParkingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParkingViewHolder, position: Int) {
        holder.bind(parkingTypes[position])
    }

    override fun getItemCount(): Int = parkingTypes.size
}