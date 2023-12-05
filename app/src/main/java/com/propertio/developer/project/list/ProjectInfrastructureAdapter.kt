package com.propertio.developer.project.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.propertio.developer.R
import com.propertio.developer.api.DomainURL
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.databinding.ItemInfrastrukturBinding

typealias OnDeleteProjectInfrastructure = (ProjectDetail.ProjectDeveloper.ProjectInfrastructure) -> Unit
class ProjectInfrastructureAdapter(
    private val context: Context,
    var listInfrastructure : List<ProjectDetail.ProjectDeveloper.ProjectInfrastructure>,
    private val onItemDelete : OnDeleteProjectInfrastructure
) : RecyclerView.Adapter<ProjectInfrastructureAdapter.ProjectInfrastructureViewHolder>() {

    inner class ProjectInfrastructureViewHolder(
        private val binding : ItemInfrastrukturBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(infrastructure: ProjectDetail.ProjectDeveloper.ProjectInfrastructure) {
            with(binding) {
                Log.d("ProjectInfrastructureAdapter", "bind: $infrastructure")

                textViewSelectedOptionInfrastruktur.text = infrastructure.name

                buttonDeleteInfrastruktur.setOnClickListener {
                    onItemDelete(infrastructure)
                }

                //TODO: Setelah Api Diupdate, uncomment code dibawah
//                loadImage(infrastructure.icon)
            }
        }

        private fun loadImage(icon: String?) {
            if (icon != null) {
                if (icon.startsWith("http")) {
                    Log.d("ProjectInfrastructureAdapter", "loadImage: $icon")
                    binding.imageViewIconInfrastruktur.load(icon) {
                        crossfade(true)
                        placeholder(R.drawable.outline_info_24)
                        error(R.drawable.outline_info_24)
                    }
                } else {
                    if (icon.endsWith(".svg")) {
                        Log.d("ProjectInfrastructureAdapter", "loadImage SVG: $icon")

                        val imageLoader = ImageLoader.Builder(context)
                            .components {
                                add(SvgDecoder.Factory())
                            }
                            .build()

                        binding.imageViewIconInfrastruktur.load(DomainURL.DOMAIN + icon, imageLoader) {
                            crossfade(true)
                            placeholder(R.drawable.outline_info_24)
                            error(R.drawable.outline_info_24)
                        }
                    } else {
                        Log.d("ProjectInfrastructureAdapter", "loadImage: ${DomainURL.DOMAIN + icon}")
                        binding.imageViewIconInfrastruktur.load(DomainURL.DOMAIN + icon) {
                            crossfade(true)
                            placeholder(R.drawable.outline_info_24)
                            error(R.drawable.outline_info_24)
                        }
                    }


                }
            } else {
                Log.w("ProjectInfrastructureAdapter", "loadImage: icon is null")
            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProjectInfrastructureViewHolder {
        val binding = ItemInfrastrukturBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ProjectInfrastructureViewHolder(binding)
    }

    override fun getItemCount(): Int = listInfrastructure.size

    override fun onBindViewHolder(holder: ProjectInfrastructureViewHolder, position: Int) {
        holder.bind(listInfrastructure[position])
    }

}
