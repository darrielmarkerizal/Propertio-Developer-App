package com.propertio.developer.dialog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.api.common.address.Province
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

typealias onClickItemProvinceListener = (Province) -> Unit
class ProvinceAdapter(
    private val provinces: List<Province>,
    private val onClickItemListener: onClickItemProvinceListener
) : RecyclerView.Adapter<ProvinceAdapter.ProvinceViewHolder>()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProvinceViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProvinceViewHolder(binding)
    }

    override fun getItemCount(): Int = provinces.size

    override fun onBindViewHolder(holder: ProvinceViewHolder, position: Int) {
        holder.bind(provinces[position])
    }

}
