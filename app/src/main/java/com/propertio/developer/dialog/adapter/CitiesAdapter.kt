package com.propertio.developer.dialog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.api.common.address.City
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding


typealias onClickItemCitiesListener = (City) -> Unit
class CitiesAdapter(
    private val onClickItemListener: onClickItemCitiesListener
) : ListAdapter<City, CitiesAdapter.CitiesViewHolder>(DiffUtilCallback())
{
    inner class CitiesViewHolder(
        private val binding : ItemSimpleCardForRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(city: City) {
            Log.d("CitiesAdapter", "bind: ${city.toString()}")

            with(binding) {
                textViewItemCardOption.text = city.name

                cardViewItemCardOption.setOnClickListener {
                    onClickItemListener(city)
                }
            }
        }

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CitiesViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CitiesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
