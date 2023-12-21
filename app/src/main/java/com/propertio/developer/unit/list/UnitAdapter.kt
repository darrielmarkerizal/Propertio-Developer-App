package com.propertio.developer.unit.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.propertio.developer.NumericalUnitConverter
import com.propertio.developer.R
import com.propertio.developer.api.DomainURL.DOMAIN
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.databinding.TemplateCardUnitBinding

class UnitAdapter(
    private val onClickUnit: (ProjectDetail.ProjectDeveloper.ProjectUnit) -> Unit,
    private val onClickMore: (ProjectDetail.ProjectDeveloper.ProjectUnit, View) -> Unit,
    private val onDelete: (ProjectDetail.ProjectDeveloper.ProjectUnit) -> Unit,
    private val onClickUnitLaku : (ProjectDetail.ProjectDeveloper.ProjectUnit) -> Unit
) : ListAdapter<ProjectDetail.ProjectDeveloper.ProjectUnit  , UnitAdapter.ItemUnitViewHolder>(UserDiffUtil()){

    class UserDiffUtil : DiffUtil.ItemCallback<ProjectDetail.ProjectDeveloper.ProjectUnit>() {
        override fun areItemsTheSame(oldItem: ProjectDetail.ProjectDeveloper.ProjectUnit, newItem: ProjectDetail.ProjectDeveloper.ProjectUnit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ProjectDetail.ProjectDeveloper.ProjectUnit,
            newItem: ProjectDetail.ProjectDeveloper.ProjectUnit
        ): Boolean {
            return (
                    oldItem.id == newItem.id &&
                    oldItem.title == newItem.title &&
                    oldItem.price == newItem.price &&
                    oldItem.bedroom == newItem.bedroom &&
                    oldItem.bathroom == newItem.bathroom &&
                    oldItem.surfaceArea == newItem.surfaceArea &&
                    oldItem.buildingArea == newItem.buildingArea &&
                    oldItem.stock == newItem.stock &&
                    oldItem.photoURL == newItem.photoURL
                    )
        }

    }

    inner class ItemUnitViewHolder(
        private val binding : TemplateCardUnitBinding,
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(unit: ProjectDetail.ProjectDeveloper.ProjectUnit) {
            with(binding) {
                textViewProjectTitle.text = unit.title
                textViewPrice.text = NumericalUnitConverter.unitFormatter(unit.price ?: "0", true)
                textViewBedroomCount.text = unit.bedroom.toString()
                textViewBathroomCount.text = unit.bathroom.toString()
                textViewSurfaceAreaAndBuildingArea.text = "${NumericalUnitConverter.meterSquareFormatter(unit.surfaceArea ?: "0")} / ${NumericalUnitConverter.meterSquareFormatter(unit.buildingArea ?: "0")}"
                textViewStock.text = unit.stock.toString()

                buttonDelete.setOnClickListener(View.OnClickListener {
                    onDelete(unit)
                })

                loadImage(unit.photoURL)

                cardViewUnit.setOnClickListener {
                    Log.d("onClickUnitCard", "Unit is clicked: ${unit.id}")
                    onClickUnit(unit)
                }

                buttonMoreHorizontal.setOnClickListener {
                    Log.d("onClickMore", "More is clicked: ${unit.id}")
                    onClickMore(unit, it)
                }

                binding.buttonUnitLaku.setOnClickListener {
                    onClickUnitLaku(unit)
                }


            }
        }

        @WorkerThread
        private fun loadImage(photoURL: String?) {
            Log.d("UnitAdapter", "loadImage: $photoURL")
            if (photoURL == null) return
            val imageUrl = if (photoURL.startsWith("http")) photoURL else "$DOMAIN$photoURL"
            with(binding) {
                imageViewThumbnail.load(imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder)
                    error(R.drawable.placeholder)

                }
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

    override fun onBindViewHolder(holder: ItemUnitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}