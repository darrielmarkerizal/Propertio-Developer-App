package com.propertio.developer.project.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.propertio.developer.R
import com.propertio.developer.api.DomainURL.DOMAIN
import com.propertio.developer.databinding.ItemUnggahPhotoBinding
import com.propertio.developer.model.LitePhotosModel

class UnggahFotoAdapter(
    private val onClickButtonCover : (LitePhotosModel) -> Unit,
    private val onClickDelete : (LitePhotosModel) -> Unit,
    private val onClickSaveCaption : (LitePhotosModel) -> Unit,
    private val showCoverButton : Boolean = true,
) : ListAdapter<LitePhotosModel, UnggahFotoAdapter.UnggahFotoViewHolder>(UnggahFotoDiffUtil()) {
    inner class UnggahFotoViewHolder(
        private val binding : ItemUnggahPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photosModel: LitePhotosModel) {
            with(binding) {

                if (photosModel.isCover == 1) {
                    buttonCoverPhotoCard.visibility = View.GONE
                    buttonDeletePhotoCard.visibility = View.GONE
                } else if (showCoverButton){
                    buttonCoverPhotoCard.visibility = View.VISIBLE
                    buttonDeletePhotoCard.visibility = View.VISIBLE
                } else {
                    buttonCoverPhotoCard.visibility = View.GONE
                    buttonDeletePhotoCard.visibility = View.VISIBLE
                }

                buttonCoverPhotoCard.setOnClickListener {
                    onClickButtonCover(photosModel)
                }

                buttonDeletePhotoCard.setOnClickListener {
                    onClickDelete(photosModel)
                }


                buttonSimpanCaption.setOnClickListener {
                    val newData = photosModel.copy(caption = editTextCaptionPhotoCard.text.toString())
                    editTextCaptionPhotoCard.clearFocus()
                    onClickSaveCaption(newData)
                }


                if (photosModel.filePath != null) {
                    loadCoverImage(photosModel.filePath!!)
                }

                if (photosModel.caption != null) {
                    editTextCaptionPhotoCard.setText(photosModel.caption)
                }

            }
        }

        private fun loadCoverImage(filePath: String) {
            val imageUrl: String = if (filePath.startsWith("http")) {
                "$filePath?timestamp=${System.currentTimeMillis()}"
            } else if (filePath.startsWith("storage")) {
                "$DOMAIN$filePath?timestamp=${System.currentTimeMillis()}"
            } else {
                filePath
            }

            binding.imageViewItemUnggahPhoto.load(imageUrl) {
                crossfade(true)
                size(266, 100)
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
                scale(Scale.FILL)
            }

            Log.d("UnggahFotoAdapter", "loadCoverImage: $imageUrl")


        }

    }

    class UnggahFotoDiffUtil : DiffUtil.ItemCallback<LitePhotosModel>() {
        override fun areItemsTheSame(oldItem: LitePhotosModel, newItem: LitePhotosModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LitePhotosModel, newItem: LitePhotosModel): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    fun updateList(newList : List<LitePhotosModel>) {
        submitList(emptyList())
        // move cover to first index
        val coverIndex = newList.indexOfFirst { it.isCover == 1 }
        if (coverIndex != -1) {
            val cover = newList[coverIndex]
            val newListWithoutCover = newList.toMutableList()
            newListWithoutCover.removeAt(coverIndex)
            newListWithoutCover.add(0, cover)
            submitList(newListWithoutCover)
        } else {
            submitList(newList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnggahFotoViewHolder {
        val binding = ItemUnggahPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UnggahFotoViewHolder(binding)
    }


    override fun onBindViewHolder(holder: UnggahFotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
