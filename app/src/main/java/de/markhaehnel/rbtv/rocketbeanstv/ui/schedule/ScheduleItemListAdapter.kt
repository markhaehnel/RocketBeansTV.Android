package de.markhaehnel.rbtv.rocketbeanstv.ui.schedule

import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import de.markhaehnel.rbtv.rocketbeanstv.AppExecutors
import de.markhaehnel.rbtv.rocketbeanstv.R
import de.markhaehnel.rbtv.rocketbeanstv.databinding.ScheduleItemBinding
import de.markhaehnel.rbtv.rocketbeanstv.ui.common.DataBoundListAdapter
import de.markhaehnel.rbtv.rocketbeanstv.vo.ScheduleItem

/**
 * A RecyclerView adapter for [ScheduleItem] class.
 */
class ScheduleItemListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val showClickCallback: ((ScheduleItem) -> Unit)?
) : DataBoundListAdapter<ScheduleItem, ScheduleItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<ScheduleItem>() {
        override fun areItemsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.topic == newItem.topic
                    && oldItem.timeStart == newItem.timeStart
                    && oldItem.timeEnd == newItem.timeEnd
        }
    }
) {

    override fun createBinding(parent: ViewGroup): ScheduleItemBinding {
        val binding = DataBindingUtil.inflate<ScheduleItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.schedule_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.show?.let {
                showClickCallback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: ScheduleItemBinding, item: ScheduleItem) {
        binding.show = item
    }
}