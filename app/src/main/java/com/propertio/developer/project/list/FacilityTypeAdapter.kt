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
import com.propertio.developer.api.DomainURL.DOMAIN
import com.propertio.developer.api.models.GeneralType
import com.propertio.developer.database.facility.FacilityTable
import com.propertio.developer.databinding.ItemFasilitasBinding

typealias OnClickItemFacilityTypeListener = (FacilityTable) -> Unit
class FacilityTypeAdapter(
    private val context : Context,
    private val facilityTypeList: MutableList<FacilityTable>,
    private val selectedFacilities: MutableList<String>,
    private val onSelectItemFacilityType: OnClickItemFacilityTypeListener,
    private val onDeselectItemFacilityType : OnClickItemFacilityTypeListener
) : RecyclerView.Adapter<FacilityTypeAdapter.FacilityTypeViewHolder>(){
    inner class FacilityTypeViewHolder(
        private val binding : ItemFasilitasBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(facility: FacilityTable) {
            Log.d("FacilityTypeAdapter", "bind: $facility")

            with(binding) {

                checkboxFasilitas.isChecked = selectedFacilities.contains(facility.id.toString())

                textViewSelectedOptionFasilitas.text = facility.name
                loadImage(facility.icon)

                checkboxFasilitas.setOnClickListener {
                    Log.d("FacilityTypeAdapter", "bind: checkbox status : ${checkboxFasilitas.isChecked}")
                    if (checkboxFasilitas.isChecked) {
                        onSelectItemFacilityType(facility)
                    } else {
                        onDeselectItemFacilityType(facility)
                    }
                }


            }



        }

        private fun loadImage(icon: String?) {
            if (icon != null) {
                if (icon.startsWith("http")) {
                    Log.d("FacilityTypeAdapter", "loadImage: $icon")
                    binding.imageViewIconFasilitas.load(icon) {
                        crossfade(true)
                        placeholder(R.drawable.outline_info_24)
                        error(R.drawable.outline_info_24)
                    }
                } else {
                    if (icon.endsWith(".svg")) {
                        Log.d("FacilityTypeAdapter", "loadImage SVG: $icon")

                        val imageLoader = ImageLoader.Builder(context)
                            .components {
                                add(SvgDecoder.Factory())
                            }
                            .build()

                        binding.imageViewIconFasilitas.load(DOMAIN + icon, imageLoader) {
                            crossfade(true)
                            placeholder(R.drawable.outline_info_24)
                            error(R.drawable.outline_info_24)
                        }
                    } else {
                        Log.d("FacilityTypeAdapter", "loadImage: ${DOMAIN + icon}")
                        binding.imageViewIconFasilitas.load(DOMAIN + icon) {
                            crossfade(true)
                            placeholder(R.drawable.outline_info_24)
                            error(R.drawable.outline_info_24)
                        }
                    }


                }
            } else {
                Log.w("FacilityTypeAdapter", "loadImage: icon is null")
            }

        }

    }

    fun updateFacilityTypeList(newFacilityTypeList : List<FacilityTable>) {
        facilityTypeList.clear()
        facilityTypeList.addAll(newFacilityTypeList)
        notifyDataSetChanged()
    }

    fun updateSelectedFacilities(newSelectedFacilities : List<String>) {
        selectedFacilities.clear()
        selectedFacilities.addAll(newSelectedFacilities)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityTypeViewHolder {
        val binding = ItemFasilitasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FacilityTypeViewHolder(binding)
    }

    override fun getItemCount(): Int = facilityTypeList.size

    override fun onBindViewHolder(holder: FacilityTypeViewHolder, position: Int) {
        holder.bind(facilityTypeList[position])
    }


}