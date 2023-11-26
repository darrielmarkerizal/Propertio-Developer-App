package com.propertio.developer.pesan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.TokenManager
import com.propertio.developer.databinding.FragmentChatBinding
import com.propertio.developer.model.Chat
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private lateinit var binding : FragmentChatBinding
    val listChat by lazy { mutableListOf<Chat>() }
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

        lifecycleScope.launch {
            val viewModelFactory = TokenManager(requireContext()).token?.let {
                ChatViewModelFactory(it)
            }
            loadMessage(viewModelFactory)
        }


    }

    private fun loadMessage(viewModelFactory: ChatViewModelFactory?) {
        val viewModel = viewModelFactory?.let {
            ViewModelProvider(this@ChatFragment, it)
        }?.get(ChatViewModel::class.java)


        with(binding) {
            recyclerViewChat.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewChat.adapter = ChatAdapter(
                context = requireContext(),
                chatList = listChat,
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


            viewModel?.chatList?.observe(viewLifecycleOwner, Observer { newChatList ->
                listChat.clear()
                listChat.addAll(newChatList ?: emptyList())
                (recyclerViewChat.adapter as? ChatAdapter)?.notifyDataSetChanged()
            })

            viewModel?.getAllMessage()

            chatSwipeRefreshLayout.setOnRefreshListener {
                viewModel?.fetchNewData()
            }

            viewModel?.isRefreshing?.observe(viewLifecycleOwner, Observer { isRefreshing ->
                binding.chatSwipeRefreshLayout.isRefreshing = isRefreshing
            })

        }
    }


}