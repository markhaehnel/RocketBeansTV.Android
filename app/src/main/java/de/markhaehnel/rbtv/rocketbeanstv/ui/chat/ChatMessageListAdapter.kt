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
            return oldItem.dateFrom == newItem.dateFrom
                    && oldItem.message == newItem.message
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.user == newItem.user
                    && oldItem.message == newItem.message
        }
    }
) {

    override fun createBinding(parent: ViewGroup): ChatItemBinding {
        val binding = DataBindingUtil.inflate<ChatItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.schedule_item,
            parent,
            false,
            dataBindingComponent
        )
        return binding
    }

    override fun bind(binding: ChatItemBinding, item: ChatMessage) {
        binding.message = item
    }
}