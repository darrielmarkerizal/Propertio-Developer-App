package com.propertio.developer.dialog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.api.common.address.District
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding


typealias onClickItemDistrictsListener = (District) -> Unit
class DistrictAdapter(
    private val districts : List<District>,
    private val onClickItemListener: onClickItemDistrictsListener
) : RecyclerView.Adapter<DistrictAdapter.DistrictsViewHolder>(){

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictsViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return DistrictsViewHolder(binding)
    }

    override fun getItemCount(): Int = districts.size

    override fun onBindViewHolder(holder: DistrictsViewHolder, position: Int) {
        holder.bind(districts[position])
    }

}
