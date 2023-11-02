package com.propertio.developer.pesan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.databinding.ItemChatContainerBinding


typealias onClickChat = (Chat) -> Unit
class ChatAdapter(
    var listMahasiswa: List<Chat>,
    private val onClickChat: onClickChat
) : RecyclerView.Adapter<ChatAdapter.ItemChatViewHolder>()
{
    inner class ItemChatViewHolder(
        private val binding: ItemChatContainerBinding
    ) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(data: Chat) {
            with(binding) {
                textSubject.text = data.subject
                textName.text = data.name
                textTime.text = data.formattedTime

                iconUnreadNotification.visibility = if (data.read == 0) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }


                itemView.setOnClickListener {
                    onClickChat(data)
                }

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemChatViewHolder {
        val binding = ItemChatContainerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return ItemChatViewHolder(binding)
    }

    override fun getItemCount(): Int = listMahasiswa.size

    override fun onBindViewHolder(holder: ItemChatViewHolder, position: Int) {
        holder.bind(listMahasiswa[position])
    }
}