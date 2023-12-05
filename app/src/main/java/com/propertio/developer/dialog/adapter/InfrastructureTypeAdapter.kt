package com.propertio.developer.dialog.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.propertio.developer.R
import com.propertio.developer.api.DomainURL.DOMAIN
import com.propertio.developer.api.models.GeneralType
import com.propertio.developer.databinding.ItemInfrastrukturBinding
import com.propertio.developer.databinding.ItemTipeInfrastrukturBinding

typealias onClickItemInfrastructureTypeListener = (GeneralType) -> Unit
class InfrastructureTypeAdapter(
    private val context : Context,
    private val propertyTypes: List<GeneralType>,
    private val onClickItemListener: onClickItemInfrastructureTypeListener
) : RecyclerView.Adapter<InfrastructureTypeAdapter.InfrastructureTypeViewHolder>() {

    inner class InfrastructureTypeViewHolder(
        private val binding : ItemTipeInfrastrukturBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(type: GeneralType) {
            Log.d("InfrastructureTypeAdapter", "bind: ${type.toString()}")

            with(binding) {
                textViewSelectedOptionInfrastruktur.text = type.name

                radiobuttonTipeInfrastruktur.setOnClickListener {
                    onClickItemListener(type)
                }

                loadImage(type.icon)
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

    override fun getItemCount(): Int = propertyTypes.size

    override fun onBindViewHolder(holder: InfrastructureTypeViewHolder, position: Int) {
        holder.bind(propertyTypes[position])
    }

}
