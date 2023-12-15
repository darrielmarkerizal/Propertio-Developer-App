package com.propertio.developer.unit.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.size.Scale
import coil.load
import com.propertio.developer.R
import com.propertio.developer.api.DomainURL.DOMAIN
import com.propertio.developer.databinding.ItemUnggahPhotoBinding
import com.propertio.developer.model.LitePhotosModel

class UnggahFotoAdapter(
    var photosList : List<LitePhotosModel>,
    private val onClickButtonCover : (LitePhotosModel) -> Unit,
    private val onClickDelete : (LitePhotosModel) -> Unit,
    private val onClickSaveCaption : (LitePhotosModel) -> Unit,
    private val showCoverButton : Boolean,
) : RecyclerView.Adapter<UnggahFotoAdapter.UnggahFotoViewHolder>() {

    inner class UnggahFotoViewHolder(
        private val binding :ItemUnggahPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photosModel: LitePhotosModel) {
            with (binding) {
                // Menyembunyikan tombol jika foto adalah sampul
                if (photosModel.isCover == 1) {
                    buttonCoverPhotoCard.visibility = View.GONE
                    buttonDeletePhotoCard.visibility = View.GONE
                    isCoverPhotoCard.visibility = View.VISIBLE
                }

                // Mengatur listener untuk setiap tombol
                buttonCoverPhotoCard.setOnClickListener {
                    onClickButtonCover(photosModel)
                }

                buttonDeletePhotoCard.setOnClickListener {
                    onClickDelete(photosModel)
                }

                buttonSimpanCaption.setOnClickListener {
                    // Mengupdate data dengan caption baru
                    val newData = photosModel.copy(caption = editTextCaptionPhotoCard.text.toString())
                    editTextCaptionPhotoCard.clearFocus()
                    onClickSaveCaption(newData)
                }

                // Memuat gambar sampul jika tersedia
                if (photosModel.filePath != null) {
                    loadCoverImage(photosModel.filePath!!)
                }

                // Menetapkan teks caption jika tersedia
                if (photosModel.caption != null) {
                    editTextCaptionPhotoCard.setText(photosModel.caption)
                }

                buttonCoverPhotoCard.visibility = if (showCoverButton) View.VISIBLE else View.GONE
            }
        }

        private fun loadCoverImage(filePath: String) {
            // Membuat URL gambar dengan menambahkan timestamp
            val imageUrl: String = if (filePath.startsWith("http")) {
                "$filePath?timestamp=${System.currentTimeMillis()}"
            } else if (filePath.startsWith("storage")) {
                "$DOMAIN$filePath?timestamp=${System.currentTimeMillis()}"
            } else {
                filePath
            }

            // Memuat gambar menggunakan Coil library
            binding.imageViewItemUnggahPhoto.load(imageUrl) {
                crossfade(true)
                size(266, 100)
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
                scale(Scale.FILL)
            }

            // Log URL gambar untuk debug
            Log.d("UnggahFotoAdapter", "loadCoverImage: $imageUrl")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnggahFotoViewHolder {
        // Membuat instance dari binding
        val binding = ItemUnggahPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UnggahFotoViewHolder(binding)
    }

    override fun getItemCount(): Int = photosList.size

    override fun onBindViewHolder(holder: UnggahFotoViewHolder, position: Int) {
        // Memanggil fungsi bind pada ViewHolder
        holder.bind(photosList[position])
    }
}
