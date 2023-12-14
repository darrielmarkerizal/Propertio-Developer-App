package com.propertio.developer.pesan

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.R
import com.propertio.developer.database.chat.ChatTable
import com.propertio.developer.databinding.ItemChatContainerBinding


typealias OnClickChat = (ChatTable, View) -> Unit
class ChatAdapter(
    private val context: Context,
    private val onClickChat: OnClickChat
) : ListAdapter<ChatTable, ChatAdapter.ItemChatViewHolder>(ChatDifCallback())
{

    class ChatDifCallback : DiffUtil.ItemCallback<ChatTable>() {
        override fun areItemsTheSame(oldItem: ChatTable, newItem: ChatTable): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatTable, newItem: ChatTable): Boolean {
            return oldItem == newItem
        }

    }

    inner class ItemChatViewHolder(
        private val binding: ItemChatContainerBinding
    ) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(data: ChatTable) {
            with(binding) {
                val date = dateFormat(data.createAt ?: "1970-01-01T05:18:39.000000Z")

                textSubject.text = data.subject
                textName.text = data.name
                textTime.text = date


                iconUnreadNotification.visibility = if (data.read == "0") {
                    View.VISIBLE
                } else {
                    View.GONE
                }


                itemView.setOnClickListener {
                    onClickChat(data, iconUnreadNotification)
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

    override fun onBindViewHolder(holder: ItemChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun dateFormat(date: String): String {
        val _date = date.split("T")
        val _time = _date[1].split(".")[0].split(":") // hh:mm:ss

        val dateString = _date[0].split("-") // yyyy-mm-dd
        val time = "${_time[0]}:${_time[1]}" // hh:mm


        // get today date
        val today = java.util.Calendar.getInstance()

        // get the delta between today and the date, if it's more than 1 day, then return the date. make sure it's on the same month and year
        Log.d("ChatAdapter Statement 1", "statement #1: ${today.get(java.util.Calendar.YEAR)} > ${dateString[0].toInt()}")
        Log.d("ChatAdapter Statement 2", "statement #2: ${today.get(java.util.Calendar.DAY_OF_MONTH)} == ${dateString[2].toInt()}  &&")
        Log.d("ChatAdapter Statement 3", "statement #2: ${today.get(java.util.Calendar.MONTH)} == ${dateString[1].toInt() - 1}")

        if (
            today.get(java.util.Calendar.YEAR) > dateString[0].toInt()
            )
        {
            Log.d("ChatAdapter", "dateFormat #1: ${dateString[0]}.${dateString[1]}.${dateString[2]}")

            return "${dateString[0]}.${dateString[1]}.${dateString[2]}"
        }
        else if (
            today.get(java.util.Calendar.DAY_OF_MONTH) == dateString[2].toInt()
            && today.get(java.util.Calendar.MONTH) == dateString[1].toInt() - 1
            )
        {
            Log.d("ChatAdapter", "dateFormat #2: $time")

            return time
        }

        val months = context.resources.getStringArray(R.array.list_of_months)

        Log.d("ChatAdapter", "dateFormat #3: ${dateString[2]} ${months[dateString[1].toInt() - 1]}")

        return "${dateString[2]} ${months[dateString[1].toInt() - 1]}"
    }


}

