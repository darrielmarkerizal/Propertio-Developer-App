package com.propertio.developer.dialog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.database.MasterData
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

class CertificateTypeAdapter(
    private val certificateTypes: List<MasterData>,
    private val onClickItemListener: (MasterData) -> Unit
) : RecyclerView.Adapter<CertificateTypeAdapter.CertificateTypeViewHolder>() {
    inner class CertificateTypeViewHolder(
        private val binding: ItemSimpleCardForRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(certificate: MasterData) {
            Log.d("CertificateTypeAdapter", "bind: $certificate")

            with(binding) {
                textViewItemCardOption.text = certificate.toUser

                cardViewItemCardOption.setOnClickListener {
                    onClickItemListener(certificate)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateTypeViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CertificateTypeViewHolder(binding)
    }

    override fun getItemCount(): Int = certificateTypes.size

    override fun onBindViewHolder(holder: CertificateTypeViewHolder, position: Int) {
        holder.bind(certificateTypes[position])
    }

}
