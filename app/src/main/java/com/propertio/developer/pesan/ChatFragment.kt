package com.propertio.developer.pesan

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.propertio.developer.PropertioDeveloperApplication
import com.propertio.developer.TokenManager
import com.propertio.developer.api.Retro
import com.propertio.developer.api.common.message.MessageApi
import com.propertio.developer.api.common.message.MessageDetailResponse
import com.propertio.developer.databinding.FragmentChatBinding
import com.propertio.developer.project.ProjectViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment : Fragment() {
    private val binding by lazy { FragmentChatBinding.inflate(layoutInflater) }
    private val messageApi by lazy { Retro(TokenManager(requireActivity()).token!!).getRetroClientInstance().create(MessageApi::class.java) }
    private lateinit var chatViewModel : ChatViewModel

    private var visibleThreshold : Int = 10
    private val cardHeight = 50
    private val cardsToLoadMore = 5
    private val loadHeighThreshold = cardHeight * cardsToLoadMore

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val chatAdapter by lazy {
        ChatAdapter(
            context = requireContext(),
            onClickChat = { it, itemBinding ->
                val intentToDetailChat = Intent(activity, DetailChatActivity::class.java)
                intentToDetailChat.putExtra(DetailChatActivity.EXTRA_NAME, it.name)
                intentToDetailChat.putExtra(DetailChatActivity.EXTRA_EMAIL, it.email)
                intentToDetailChat.putExtra(DetailChatActivity.EXTRA_PHONE, it.phone)
                intentToDetailChat.putExtra(DetailChatActivity.EXTRA_SUBJECT, it.subject)
                intentToDetailChat.putExtra(DetailChatActivity.EXTRA_MESSAGE, it.message)
                intentToDetailChat.putExtra(DetailChatActivity.EXTRA_TIME, it.createAt)
                intentToDetailChat.putExtra(DetailChatActivity.EXTRA_ID, it.id)
                launcher.launch(intentToDetailChat)

                lifecycleScope.launch(Dispatchers.IO) {
                    messageApi.getDetailMessage(it.id).enqueue(object : Callback<MessageDetailResponse> {
                        override fun onResponse(
                            call: Call<MessageDetailResponse>,
                            response: Response<MessageDetailResponse>
                        ) {
                            if (response.isSuccessful) {
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        chatViewModel.updateRead(it.id)
                                        chatViewModel.unreadChatCount.postValue(chatViewModel.countUnread())
                                    }
                                    withContext(Dispatchers.Main) {
                                        itemBinding.visibility = View.GONE
                                    }
                                }
                            } else {
                                Log.d("ChatFragment", "onResponse: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(
                            call: Call<MessageDetailResponse>,
                            t: Throwable
                        ) {
                            Log.e("ChatFragment", "onFailure: ${t.message}")
                        }

                    })
                }

            }
        )
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    chatViewModel.fetchDataFromApi(TokenManager(requireContext()).token!!)
                }
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ProjectViewModelFactory((requireActivity().application as PropertioDeveloperApplication).repository)
        chatViewModel = ViewModelProvider(requireActivity(), factory)[ChatViewModel::class.java]

        setupInfinityScroll()
        setupRecyclerView()
        setupRefreshLayout()
        setupSearchBar()

    }

    private fun setupSearchBar() {
        binding.searchBarChat.doAfterTextChanged {
            val query = binding.searchBarChat.text.toString()
            if (query.isNotEmpty()) {
                lifecycleScope.launch {
                    val totalChat = withContext(Dispatchers.IO) {
                        chatViewModel.countAll()
                    }
                    var loadChat = 5
                    while (loadChat < totalChat) {
                        val chats = withContext(Dispatchers.IO) {
                            chatViewModel.search(query, loadChat)
                        }
                        chatAdapter.submitList(chats)
                        loadChat += 5
                    }


                }
            } else {
                lifecycleScope.launch {
                    loadChat()
                }
            }
        }
    }

    private fun setupRefreshLayout() {
        binding.chatSwipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                chatViewModel.fetchDataFromApi(TokenManager(requireActivity()).token!!)
                binding.chatSwipeRefreshLayout.isRefreshing = false
            }
        }
    }


    private fun setupInfinityScroll() {
        binding.nestedScrollViewChat.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

                val isAtBottom = scrollY >= (v.getChildAt(0).measuredHeight - v.measuredHeight)
                val isNearBottom = scrollY >= (v.getChildAt(0).measuredHeight - v.measuredHeight - loadHeighThreshold)

                if (!v.canScrollVertically(1) && isAtBottom || isNearBottom) {
                    Log.d("ProjectFragment", "onViewCreated: isAtBottom: $isAtBottom , isNearBottom: $isNearBottom")
                    if (!isLoading.value!!) {
                        Log.d("ProjectFragment", "onViewCreated: isLoading: ${isLoading.value}")
                        _isLoading.value = true
                        visibleThreshold += 5

                        binding.progressBarChat.visibility = View.VISIBLE

                        lifecycleScope.launch {
                            loadChat()
                        }
                    }
                }

            }
        )
    }



    private suspend fun loadChat() {
        withContext(Dispatchers.Main) {
            val chats = withContext(Dispatchers.IO) {
                chatViewModel.getAllChatPaginated(visibleThreshold, 0)
            }
            _isLoading.value = false
            binding.progressBarChat.visibility = View.GONE
            chatAdapter.submitList(chats)
        }
    }

    private fun setupRecyclerView() {
        with(binding) {
            recyclerViewChat.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewChat.adapter = chatAdapter
        }

        lifecycleScope.launch {
            loadChat()
        }
    }


}