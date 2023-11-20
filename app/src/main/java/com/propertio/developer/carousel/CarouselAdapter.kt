package com.propertio.developer.carousel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.propertio.developer.databinding.ItemSlideBinding

class CarouselAdapter(
    private val images: List<ImageData>
): RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>(){
    inner class CarouselViewHolder(
        private val binding : ItemSlideBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ImageData) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(data.imgaeUrl)
                    .into(imageViewSlider)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return CarouselViewHolder(
            ItemSlideBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(images[position])
    }


}