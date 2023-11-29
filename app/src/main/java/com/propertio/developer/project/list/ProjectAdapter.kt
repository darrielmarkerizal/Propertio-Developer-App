package com.propertio.developer.project.list

import android.content.Context
import android.content.Intent
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.bumptech.glide.Glide
import com.propertio.developer.R
import com.propertio.developer.api.DomainURL
import com.propertio.developer.database.project.ProjectTable
import com.propertio.developer.databinding.TemplateCardProjectBinding
//import com.propertio.developer.lib.GlideApp
//import com.propertio.developer.lib.SvgSoftwareLayerSetter
import com.propertio.developer.project.ProjectDetailActivity
import com.propertio.developer.project.ProjectDetailActivity.Companion.PROJECT_ID


class ProjectAdapter(
    private val context: Context,
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

                textViewPrice.text = data.price.toString()
                textViewPropertyType.text = data.propertyTypeName ?: "Property Type"
                textViewCountUnit.text = data.countUnit.toString()
                textViewProjectCode.text = data.projectCode


                // Image
                val imageURL: String = DomainURL.DOMAIN + data.photo
                loadImage(imageURL)

                val propertyTypeIconURL : String = DomainURL.DOMAIN + data.propertyTypeIcon
                loadImagePropertyTypeIcon(propertyTypeIconURL)



                // button
                buttonRincian.setOnClickListener {
                    Log.d("ProjectAdapter", "Repost button clicked")
                    val intentToDetailProject = Intent(context, ProjectDetailActivity::class.java)
                    intentToDetailProject.putExtra(PROJECT_ID, data.id)
                    context.startActivity(intentToDetailProject)
                }


                // Additional
                textViewDatetime.text = data.postedAt


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
            with(binding) {
                Log.d("ProjectAdapter", "imageURL: $imageURL")
                imageViewThumbnail.load(imageURL) {
                    crossfade(true)
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