package com.propertio.developer.project.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.propertio.developer.api.DomainURL
import com.propertio.developer.databinding.TemplateCardProjectBinding
import com.propertio.developer.model.Project


typealias onClickProject = (Project) -> Unit
class ProjectAdapter(
    private val context: Context,
    var projectList : List<Project>
) : RecyclerView.Adapter<ProjectAdapter.ItemProjectViewHolder>() {

    inner class ItemProjectViewHolder(
        private val binding: TemplateCardProjectBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: Project) {
            with(binding) {
                // Essential
                textViewTitle.text = data.title
                textViewViews.text = data.countViews.toString()
                textViewLeads.text = data.countLeads.toString()

                with(data){
                    val projectAddress = "${this.address?.addressAddress}, ${this.address?.addressDistrict}, ${this.address?.addressCity}, ${this.address?.addressProvince}"
                    textViewAddress.text = projectAddress
                }

                textViewPrice.text = data.price.toString()
                textViewPropertyType.text = data.propertyType
                textViewCountUnit.text = data.countUnit.toString()
                textViewProjectCode.text = data.projectCode


                // Image
                data.projectPhotos?.forEach {
                    if (it.isCover == "1") {
                        val imageURL: String = DomainURL.DOMAIN + it.filename
                        Log.d("ProjectAdapter", "imageURL: $imageURL")
                        Glide.with(context)
                            .load(imageURL)
                            .into(imageViewThumbnail)
                    }
                }




                // Additional
                textViewDatetime.text = data.updatedAt


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

    override fun getItemCount(): Int = projectList.size

    override fun onBindViewHolder(holder: ItemProjectViewHolder, position: Int) {
        holder.bind(projectList[position])
    }

    fun updateProjects(projects: List<Project>) {
        projectList = projects
        notifyDataSetChanged()
    }

}