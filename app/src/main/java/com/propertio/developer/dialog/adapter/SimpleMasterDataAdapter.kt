package com.propertio.developer.dialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.database.MasterData
import com.propertio.developer.databinding.ItemSimpleCardForRecyclerBinding

class SimpleMasterDataAdapter(
    private val onClickItemListener: (MasterData) -> Unit
) : ListAdapter<MasterData, SimpleMasterDataAdapter.SimpleMasterDataViewHolder>(
    MasterDiffUtilCallback()
) {
    inner class SimpleMasterDataViewHolder(
        private val binding: ItemSimpleCardForRecyclerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(masterData: MasterData) {
            with(binding) {
                textViewItemCardOption.text = masterData.toUser
                cardViewItemCardOption.setOnClickListener {
                    onClickItemListener(masterData)
                }
            }
        }

    }

    class MasterDiffUtilCallback : DiffUtil.ItemCallback<MasterData>() {
        override fun areItemsTheSame(oldItem: MasterData, newItem: MasterData): Boolean {
            return oldItem.toDb == newItem.toDb
        }

        override fun areContentsTheSame(oldItem: MasterData, newItem: MasterData): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleMasterDataViewHolder {
        val binding = ItemSimpleCardForRecyclerBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return SimpleMasterDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SimpleMasterDataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}