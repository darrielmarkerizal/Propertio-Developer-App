package com.propertio.developer.pesan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.databinding.FragmentChatBinding

class ChatFragment : Fragment() {
    private lateinit var binding : FragmentChatBinding
    private val launcher = registerForActivityResult(ActivityResultContracts.
    StartActivityForResult()){}



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("account_data", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        Log.d("ChatFragment", "Token from prefs: $token")

        val viewModelFactory = token?.let {
            ChatViewModelFactory(it)
        }
        val viewModel = viewModelFactory?.let {
            ViewModelProvider(this@ChatFragment, it)
        }?.get(ChatViewModel::class.java)

        with(binding) {
            recyclerViewChat.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewChat.adapter = ChatAdapter(
                context = requireContext(),
                chatList = emptyList(),
                onClickChat = {
                    val intentToDetailChat = Intent(activity, DetailChatActivity::class.java)
                    intentToDetailChat.putExtra(DetailChatActivity.EXTRA_NAME, it.name)
                    intentToDetailChat.putExtra(DetailChatActivity.EXTRA_EMAIL, it.email)
                    intentToDetailChat.putExtra(DetailChatActivity.EXTRA_PHONE, it.phone)
                    intentToDetailChat.putExtra(DetailChatActivity.EXTRA_SUBJECT, it.subject)
                    intentToDetailChat.putExtra(DetailChatActivity.EXTRA_MESSAGE, it.message)
                    intentToDetailChat.putExtra(DetailChatActivity.EXTRA_TIME, it.time)
                    intentToDetailChat.putExtra(DetailChatActivity.EXTRA_ID, it.id)
                    launcher.launch(intentToDetailChat)
                }
            )


            viewModel?.chatList?.observe(viewLifecycleOwner, Observer { listChat ->
                (recyclerViewChat.adapter as ChatAdapter).apply {
                    chatList = listChat?.toList() ?: emptyList()
                    notifyDataSetChanged()
                }
            })

            viewModel?.getAllMessage()
        }
    }



}