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

    var rbtvServiceInfo = streamRepository.loadServiceInfo()

    var streamManifest: LiveData<Resource<StreamManifest>> = Transformations
        .switchMap(rbtvServiceInfo) { serviceInfo ->
            if (serviceInfo.data === null) {
                AbsentLiveData.create()
            } else {
                streamRepository.loadStreamManifest(serviceInfo.data.service.streamInfo.youtubeToken)
            }
        }

    val schedule: LiveData<Resource<Schedule>> = streamRepository.loadSchedule()

    var isServiceInfoVisible: Boolean = true

    fun retry() {
    //TODO: implement retry
    }
}