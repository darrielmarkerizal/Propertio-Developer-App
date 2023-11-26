package com.propertio.developer.dialog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.api.profile.ProfileResponse
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding


typealias onClickItemCitiesListener = (ProfileResponse.City) -> Unit
class CitiesAdapter(
    private val cities : List<ProfileResponse.City>,
    private val onClickItemListener: onClickItemCitiesListener
) : RecyclerView.Adapter<CitiesAdapter.CitiesViewHolder>()
{
    inner class CitiesViewHolder(
        private val binding : ItemSimpleCardForRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(city: ProfileResponse.City) {
            Log.d("CitiesAdapter", "bind: ${city.toString()}")

            with(binding) {
                textViewItemCardOption.text = city.name

                cardViewItemCardOption.setOnClickListener {
                    onClickItemListener(city)
                }
            }
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

    override fun getItemCount(): Int = cities.size

    override fun onBindViewHolder(holder: CitiesViewHolder, position: Int) {
        holder.bind(cities[position])
    }
}
