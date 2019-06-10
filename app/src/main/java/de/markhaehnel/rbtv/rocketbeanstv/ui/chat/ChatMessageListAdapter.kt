package de.markhaehnel.rbtv.rocketbeanstv.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import de.markhaehnel.rbtv.rocketbeanstv.AppExecutors
import de.markhaehnel.rbtv.rocketbeanstv.R
import de.markhaehnel.rbtv.rocketbeanstv.databinding.ChatItemBinding
import de.markhaehnel.rbtv.rocketbeanstv.ui.common.DataBoundListAdapter
import de.markhaehnel.rbtv.rocketbeanstv.vo.ChatMessage

/**
 * A RecyclerView adapter for [ChatMessage] class.
 */
class ChatMessageListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors
) : DataBoundListAdapter<ChatMessage, ChatItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.user == newItem.user && oldItem.message == newItem.message && oldItem.source == newItem.source
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.user == newItem.user && oldItem.message == newItem.message && oldItem.source == newItem.source
        }
    }
) {
    override fun createBinding(parent: ViewGroup): ChatItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.chat_item,
            parent,
            false,
            dataBindingComponent
        )
    }

    override fun bind(binding: ChatItemBinding, item: ChatMessage) {
        binding.message = item
        binding.root.apply {
            isClickable = false
            isFocusable = false
            isFocusableInTouchMode = false
            isEnabled = false
        }
    }

}