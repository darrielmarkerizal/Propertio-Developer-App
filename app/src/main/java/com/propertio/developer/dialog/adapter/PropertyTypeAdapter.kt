package com.propertio.developer.dialog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.api.models.GeneralType
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

typealias OnClickItemPropertyTypeListener = (GeneralType) -> Unit
class PropertyTypeAdapter(
    private val onClickItemListener: OnClickItemPropertyTypeListener
) : ListAdapter<GeneralType, PropertyTypeAdapter.PropertyTypeViewHolder>(PropertyTypeDiffUttil()) {

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

    class PropertyTypeDiffUttil : DiffUtil.ItemCallback<GeneralType>() {
        override fun areItemsTheSame(oldItem: GeneralType, newItem: GeneralType): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GeneralType, newItem: GeneralType): Boolean {
            return oldItem.toString() == newItem.toString()
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


    override fun onBindViewHolder(holder: PropertyTypeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
