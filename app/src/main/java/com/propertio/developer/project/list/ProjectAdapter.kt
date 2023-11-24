package com.propertio.developer.project.list

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.propertio.developer.api.DomainURL
import com.propertio.developer.database.project.ProjectTable
import com.propertio.developer.databinding.TemplateCardProjectBinding
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
                textViewPropertyType.text = data.propertyType
                textViewCountUnit.text = data.countUnit.toString()
                textViewProjectCode.text = data.projectCode


                // Image
                val imageURL: String = DomainURL.DOMAIN + data.photo
                loadImage(imageURL)


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



        private fun loadImage(imageURL: String) {
            with(binding) {
                Log.d("ProjectAdapter", "imageURL: $imageURL")
                Glide.with(context)
                    .load(imageURL)
                    .thumbnail(0.1f)
                    .into(imageViewThumbnail)
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