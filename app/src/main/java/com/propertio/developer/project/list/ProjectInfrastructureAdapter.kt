package com.propertio.developer.project.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.propertio.developer.PropertioDeveloperApplication
import com.propertio.developer.R
import com.propertio.developer.api.DomainURL
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.databinding.ItemInfrastrukturBinding
import com.propertio.developer.project.viewmodel.FacilityViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias OnDeleteProjectInfrastructure = (ProjectDetail.ProjectDeveloper.ProjectInfrastructure) -> Unit
class ProjectInfrastructureAdapter(
    private val context: Context,
    var listInfrastructure : List<ProjectDetail.ProjectDeveloper.ProjectInfrastructure>,
    private val onItemDelete : OnDeleteProjectInfrastructure,
    private val scope : CoroutineScope,
    private val viewModelStoreOwner: ViewModelStoreOwner
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


                loadImage(infrastructure.infrastructureTypeId)
            }
        }

        private fun loadImage(id: String?) {
            scope.launch {
                val factory = FacilityViewModelFactory((context.applicationContext as PropertioDeveloperApplication).repository)
                val facilityAndInfrastructureTypeViewModel = ViewModelProvider(viewModelStoreOwner, factory)[FacilityAndInfrastructureTypeViewModel::class.java]


                val infrastructure = withContext(Dispatchers.IO) {
                    facilityAndInfrastructureTypeViewModel.getInfrastructureById(
                        id!!.toInt()
                    )
                }

                val icon = infrastructure?.icon

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
