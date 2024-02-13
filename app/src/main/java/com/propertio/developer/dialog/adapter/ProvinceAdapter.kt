package com.propertio.developer.dialog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.api.common.address.Province
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

typealias onClickItemProvinceListener = (Province) -> Unit
class ProvinceAdapter(
    private val onClickItemListener: onClickItemProvinceListener
) : ListAdapter<Province, ProvinceAdapter.ProvinceViewHolder>(ProvinceDiffCallback())
{
    inner class ProvinceViewHolder (
        private val binding : ItemSimpleCardForRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(province: Province) {
            Log.d("ProvinceAdapter", "bind: ${province.toString()}")

            with(binding) {
                textViewItemCardOption.text = province.name

                cardViewItemCardOption.setOnClickListener {
                    onClickItemListener(province)
                }
            }
        }

    }

    class ProvinceDiffCallback : DiffUtil.ItemCallback<Province>() {
        override fun areItemsTheSame(oldItem: Province, newItem: Province): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Province, newItem: Province): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProvinceViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProvinceViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ProvinceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
