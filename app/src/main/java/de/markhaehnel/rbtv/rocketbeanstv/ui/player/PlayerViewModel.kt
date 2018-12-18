package de.markhaehnel.rbtv.rocketbeanstv.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import de.markhaehnel.rbtv.rocketbeanstv.repository.StreamRepository
import de.markhaehnel.rbtv.rocketbeanstv.util.AbsentLiveData
import de.markhaehnel.rbtv.rocketbeanstv.vo.*
import javax.inject.Inject

class PlayerViewModel
@Inject constructor(streamRepository: StreamRepository) : ViewModel() {

    val streamInfo: LiveData<Resource<Stream>> = streamRepository.loadStream()
    var streamManifest: LiveData<Resource<StreamManifest>> = Transformations
        .switchMap(streamInfo) { stream ->
            if (stream.data === null) {
                AbsentLiveData.create()
            } else {
                streamRepository.loadStreamManifest(stream.data.videoId)
            }
        }

    val currentShow: LiveData<Resource<ScheduleItem>> = streamRepository.loadCurrentShow()
    val upcomingShows: LiveData<Resource<Schedule>> = streamRepository.loadUpcomingShows()

    fun retry() {
        //TODO: implement retry
    }
}