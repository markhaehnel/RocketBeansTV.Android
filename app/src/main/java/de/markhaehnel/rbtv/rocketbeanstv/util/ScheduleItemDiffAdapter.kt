package de.markhaehnel.rbtv.rocketbeanstv.util

import androidx.recyclerview.widget.DiffUtil
import de.markhaehnel.rbtv.rocketbeanstv.vo.ScheduleItem

class ScheduleItemDiffCallback : DiffUtil.ItemCallback<ScheduleItem>() {
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