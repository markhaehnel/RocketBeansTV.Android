package de.markhaehnel.rbtv.rocketbeanstv.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import de.markhaehnel.rbtv.rocketbeanstv.repository.StreamRepository
import de.markhaehnel.rbtv.rocketbeanstv.util.Time
import de.markhaehnel.rbtv.rocketbeanstv.vo.Resource
import de.markhaehnel.rbtv.rocketbeanstv.vo.Schedule
import javax.inject.Inject

class ScheduleViewModel
@Inject constructor(streamRepository: StreamRepository) : ViewModel() {

    val schedule: LiveData<Resource<Schedule>> = streamRepository.loadSchedule(Time.getDayBefore(), Time.getDayAfter())

    fun retry() {
    //TODO: implement retry
    }
}