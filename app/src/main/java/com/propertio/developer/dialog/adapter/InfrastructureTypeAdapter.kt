package com.propertio.developer.dialog.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.propertio.developer.R
import com.propertio.developer.api.DomainURL.DOMAIN
import com.propertio.developer.database.infrastructure.InfrastructureTable
import com.propertio.developer.databinding.ItemTipeInfrastrukturBinding

typealias onClickItemInfrastructureTypeListener = (InfrastructureTable) -> Unit
class InfrastructureTypeAdapter(
    private val context : Context,
    private val onClickItemListener: onClickItemInfrastructureTypeListener
) : ListAdapter<InfrastructureTable, InfrastructureTypeAdapter.InfrastructureTypeViewHolder>(InfrastructureTypeDiffUtil()) {

    inner class InfrastructureTypeViewHolder(
        private val binding : ItemTipeInfrastrukturBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(infrastructure: InfrastructureTable) {
            Log.d("InfrastructureTypeAdapter", "bind: $infrastructure")

            with(binding) {
                textViewSelectedOptionInfrastruktur.text = infrastructure.name

                radiobuttonTipeInfrastruktur.setOnClickListener {
                    onClickItemListener(infrastructure)
                }

                loadImage(infrastructure.icon)
            }
        }

        private fun loadImage(icon: String?) {
            if (icon != null) {
                if (icon.startsWith("http")) {
                    Log.d("InfrastructureTypeAdapter", "loadImage: $icon")
                    binding.imageViewIconInfrastruktur.load(icon) {
                        crossfade(true)
                        placeholder(R.drawable.outline_info_24)
                        error(R.drawable.outline_info_24)
                    }
                } else {
                    if (icon.endsWith(".svg")) {
                        Log.d("InfrastructureTypeAdapter", "loadImage SVG: $icon")

                        val imageLoader = ImageLoader.Builder(context)
                            .components {
                                add(SvgDecoder.Factory())
                            }
                            .build()

                        binding.imageViewIconInfrastruktur.load(DOMAIN + icon, imageLoader) {
                            crossfade(true)
                            placeholder(R.drawable.outline_info_24)
                            error(R.drawable.outline_info_24)
                        }
                    } else {
                        Log.d("InfrastructureTypeAdapter", "loadImage: ${DOMAIN + icon}")
                        binding.imageViewIconInfrastruktur.load(DOMAIN + icon) {
                            crossfade(true)
                            placeholder(R.drawable.outline_info_24)
                            error(R.drawable.outline_info_24)
                        }
                    }


                }
            } else {
                Log.w("InfrastructureTypeAdapter", "loadImage: icon is null")
            }
        }

    }

    class InfrastructureTypeDiffUtil : DiffUtil.ItemCallback<InfrastructureTable>() {
        override fun areItemsTheSame(oldItem: InfrastructureTable, newItem: InfrastructureTable): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InfrastructureTable, newItem: InfrastructureTable): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InfrastructureTypeViewHolder {
        val binding = ItemTipeInfrastrukturBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return InfrastructureTypeViewHolder(binding)
    }


    override fun onBindViewHolder(holder: InfrastructureTypeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
