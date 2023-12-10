package com.propertio.developer.project.list

import android.content.Context
import android.content.Intent
import android.graphics.drawable.PictureDrawable
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.bumptech.glide.Glide
import com.propertio.developer.NumericalUnitConverter
import com.propertio.developer.R
import com.propertio.developer.api.DomainURL
import com.propertio.developer.database.project.ProjectTable
import com.propertio.developer.databinding.TemplateCardProjectBinding
//import com.propertio.developer.lib.GlideApp
//import com.propertio.developer.lib.SvgSoftwareLayerSetter
import com.propertio.developer.project.ProjectDetailActivity
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_ID
import java.util.Date
import java.util.Locale


class ProjectAdapter(
    private val context: Context,
    private val onClickRincian: (ProjectTable) -> Unit,
    private val onClickMore: (ProjectTable, View) -> Unit,
    private val onClickRepost: (ProjectTable) -> Unit
) : ListAdapter<ProjectTable, ProjectAdapter.ItemProjectViewHolder>(ProjectDiffCallback()) {

    class ProjectDiffCallback : DiffUtil.ItemCallback<ProjectTable>() {
        override fun areItemsTheSame(oldItem: ProjectTable, newItem: ProjectTable): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProjectTable, newItem: ProjectTable): Boolean {
            return oldItem == newItem
        }
    }

    inner class ItemProjectViewHolder(
        private val binding: TemplateCardProjectBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: ProjectTable) {
            with(binding) {
                // Essential
                textViewProjectTitle.text = data.title
                textViewViews.text = data.countViews.toString()
                textViewLeads.text = data.countLeads.toString()

                with(data){
                    val projectAddress = "${this.addressAddress}, ${this.addressDistrict}, ${this.addressCity}, ${this.addressProvince}"
                    textViewAddress.text = projectAddress
                }

                textViewPrice.text = NumericalUnitConverter.unitFormatter(data.price ?: "0", true)
                textViewPropertyType.text = data.propertyTypeName ?: "Property Type"
                textViewCountUnit.text = data.countUnit.toString()
                textViewProjectCode.text = data.projectCode


                val imageURL: String = DomainURL.DOMAIN + data.photo
                loadImage(imageURL)

                val propertyTypeIconURL : String = DomainURL.DOMAIN + data.propertyTypeIcon
                loadImagePropertyTypeIcon(propertyTypeIconURL)



                // button
                buttonRincian.setOnClickListener {
                    onClickRincian(data)
                }

                buttonMoreHorizontal.setOnClickListener {
                    onClickMore(data, buttonMoreHorizontal)
                }

                buttonRepost.setOnClickListener {
                    onClickRepost(data)
                }


                // Additional
                textViewDatetime.text = formatDate(data.postedAt ?: "1970-1-1 00:00:00")





            }


        }

        private fun formatDate(inputDate: String): String {
            val months = context.resources.getStringArray(R.array.list_of_months)

            val date = inputDate.split(" ").get(0).split("-")
            val time = inputDate.split(" ").get(1).split(":")

            // get today date
            val today = java.util.Calendar.getInstance()


            if (today.get(java.util.Calendar.YEAR) > date[0].toInt()) {
                return "${date[0]} ${months[date[1].toInt() - 1]} ${date[2]}"
            }
            else if (
                today.get(java.util.Calendar.DAY_OF_MONTH) == date[2].toInt()
                && today.get(java.util.Calendar.MONTH) == date[1].toInt() - 1
            ) {
                Log.d("ChatAdapter", "dateFormat #2: $time")
                return "Hari ini"
            } else if (
                today.get(java.util.Calendar.DAY_OF_MONTH) == date[2].toInt() + 1
                && today.get(java.util.Calendar.MONTH) == date[1].toInt() - 1
            ){
                return "Kemarin"
            } else if (
                today.get(java.util.Calendar.DAY_OF_MONTH) == date[2].toInt() + 2
                && today.get(java.util.Calendar.MONTH) == date[1].toInt() - 1
            ){
                return "Dua hari yang lalu"
            } else if (
                today.get(java.util.Calendar.DAY_OF_MONTH) == date[2].toInt() + 3
                && today.get(java.util.Calendar.MONTH) == date[1].toInt() - 1
            ) {
                return "Tiga hari yang lalu"
            }else if (
                today.get(java.util.Calendar.DAY_OF_MONTH) <= date[2].toInt() + 7
                && today.get(java.util.Calendar.MONTH) == date[1].toInt() - 1
            ) {
                return "Minggu ini"
            } else {
                return "${date[2]} ${months[date[1].toInt() - 1]}"
            }


        }

        private fun loadImagePropertyTypeIcon(propertyTypeIconURL: String) {
            with(binding) {
                Log.d("ProjectAdapter", "propertyTypeIconURL: $propertyTypeIconURL")
                // Load with coil
                val imageLoader = ImageLoader.Builder(context)
                    .components {
                        add(SvgDecoder.Factory())
                    }
                    .build()

                icPropertyType.load(propertyTypeIconURL, imageLoader) {
                    crossfade(true)
                    placeholder(R.drawable.home)
                    error(R.drawable.home)
                }
            }
        }

        private fun loadImage(imageURL: String) {
            val dateFormat = SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            val uniqueURL = "$imageURL?$timestamp"
            with(binding) {
                Log.d("ProjectAdapter", "imageURL: $imageURL")
                imageViewThumbnail.load(uniqueURL) {
                    crossfade(true)
                    crossfade(500)
                    placeholder(R.drawable.placeholder)
                    error(R.drawable.placeholder)
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemProjectViewHolder {
        val binding = TemplateCardProjectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return ItemProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemProjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }




}