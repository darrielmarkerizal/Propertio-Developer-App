package com.propertio.developer.dialog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.api.common.address.District
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding


typealias onClickItemDistrictsListener = (District) -> Unit
class DistrictAdapter(
    private val onClickItemListener: onClickItemDistrictsListener
) : ListAdapter<District, DistrictAdapter.DistrictsViewHolder>(DiffUtilCallback()){

    inner class DistrictsViewHolder(
        private val binding : ItemSimpleCardForRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(district: District) {
            Log.d("DistrictAdapter", "bind: ${district.toString()}")

            with(binding) {
                textViewItemCardOption.text = district.name

                cardViewItemCardOption.setOnClickListener {
                    onClickItemListener(district)
                }
            }
        }


    }

    class DiffUtilCallback : DiffUtil.ItemCallback<District>() {
        override fun areItemsTheSame(oldItem: District, newItem: District): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: District, newItem: District): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictsViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return DistrictsViewHolder(binding)
    }


    override fun onBindViewHolder(holder: DistrictsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
