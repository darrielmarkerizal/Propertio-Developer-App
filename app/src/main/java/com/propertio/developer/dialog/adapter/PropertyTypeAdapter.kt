package com.propertio.developer.dialog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.api.models.GeneralType
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

typealias OnClickItemPropertyTypeListener = (GeneralType) -> Unit
class PropertyTypeAdapter(
    private val propertyTypes: List<GeneralType>,
    private val onClickItemListener: OnClickItemPropertyTypeListener
) : RecyclerView.Adapter<PropertyTypeAdapter.PropertyTypeViewHolder>() {

    inner class PropertyTypeViewHolder(
        private val binding : ItemSimpleCardForRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(propertyType: GeneralType) {
            Log.d("PropertyTypeAdapter", "bind: ${propertyType.toString()}")

            with(binding) {
                textViewItemCardOption.text = propertyType.name

                cardViewItemCardOption.setOnClickListener {
                    onClickItemListener(propertyType)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyTypeViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PropertyTypeViewHolder(binding)
    }

    override fun getItemCount(): Int = propertyTypes.size

    override fun onBindViewHolder(holder: PropertyTypeViewHolder, position: Int) {
        holder.bind(propertyTypes[position])
    }

}
