package de.markhaehnel.rbtv.rocketbeanstv.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import de.markhaehnel.rbtv.rocketbeanstv.AppExecutors
import de.markhaehnel.rbtv.rocketbeanstv.R
import de.markhaehnel.rbtv.rocketbeanstv.binding.FragmentDataBindingComponent
import de.markhaehnel.rbtv.rocketbeanstv.databinding.FragmentChatBinding
import de.markhaehnel.rbtv.rocketbeanstv.di.Injectable
import de.markhaehnel.rbtv.rocketbeanstv.ui.common.RetryCallback
import de.markhaehnel.rbtv.rocketbeanstv.util.autoCleared
import kotlinx.android.synthetic.main.fragment_chat.*
import javax.inject.Inject

class ChatFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    private val MAX_CHAT_ITEMS = 15

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentChatBinding>()

    private lateinit var chatViewModel: ChatViewModel
    private var adapter by autoCleared<ChatMessageListAdapter>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentChatBinding>(
            inflater,
            R.layout.fragment_chat,
            container,
            false,
            dataBindingComponent
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                chatViewModel.retry()
            }
        }

        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner

        val rvAdapter = ChatMessageListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors
        )
        binding.chatList.adapter = rvAdapter
        this.adapter = rvAdapter

        initChat()
    }

    private fun initChat() {

        chatViewModel.chatMessages.observe(viewLifecycleOwner, Observer { chatMessages ->
            if (chatMessages?.data != null) {
                adapter.submitList(chatMessages.data.takeLast(MAX_CHAT_ITEMS))
                chat_list.smoothScrollToPosition(adapter.itemCount)
            }
        })
    }
}
