package com.propertio.developer.unit.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.propertio.developer.NumericalUnitConverter
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.databinding.TemplateCardUnitBinding

class UnitAdapter(
    private val context: Context,
    private val unitsList : LiveData<List<ProjectDetail.ProjectDeveloper.ProjectUnit>>
) : RecyclerView.Adapter<UnitAdapter.ItemUnitViewHolder>(){
    inner class ItemUnitViewHolder(
        private val binding : TemplateCardUnitBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(unit: ProjectDetail.ProjectDeveloper.ProjectUnit) {
            with(binding) {
                textViewProjectTitle.text = unit.title
                textViewPrice.text = NumericalUnitConverter.unitFormatter(unit.price ?: "0", true)
                textViewBedroomCount.text = unit.bedroom.toString()
                textViewBathroomCount.text = unit.bathroom.toString()
                textViewSurfaceAreaAndBuildingArea.text = "${unit.surfaceArea} m\\u00B2 / ${unit.buildingArea} m\\u00B2"
                textViewStock.text = unit.stock.toString()

                loadImage(unit.photoURL)


            }
        }

        @WorkerThread
        private fun loadImage(photoURL: String?) {
            Log.d("UnitAdapter", "loadImage: $photoURL")
            with(binding) {
                Glide.with(context)
                    .load(photoURL)
                    .into(imageViewThumbnail)
            }
        }


    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemUnitViewHolder {
        val binding = TemplateCardUnitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ItemUnitViewHolder(binding)
    }

    override fun getItemCount(): Int = unitsList.value?.size ?: 0

    override fun onBindViewHolder(holder: ItemUnitViewHolder, position: Int) {
        holder.bind(unitsList.value!![position])
    }


}