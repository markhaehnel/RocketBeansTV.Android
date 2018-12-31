package de.markhaehnel.rbtv.rocketbeanstv.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import de.markhaehnel.rbtv.rocketbeanstv.repository.StreamRepository
import de.markhaehnel.rbtv.rocketbeanstv.util.AbsentLiveData
import de.markhaehnel.rbtv.rocketbeanstv.vo.*
import io.lindstrom.m3u8.model.MasterPlaylist
import javax.inject.Inject

class ScheduleViewModel
@Inject constructor(streamRepository: StreamRepository) : ViewModel() {

    val schedule: LiveData<Resource<Schedule>> = streamRepository.loadSchedule()

    fun retry() {
    //TODO: implement retry
    }
}