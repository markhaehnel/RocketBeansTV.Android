package de.markhaehnel.rbtv.rocketbeanstv.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import de.markhaehnel.rbtv.rocketbeanstv.repository.StreamRepository
import de.markhaehnel.rbtv.rocketbeanstv.util.AbsentLiveData
import de.markhaehnel.rbtv.rocketbeanstv.vo.Resource
import de.markhaehnel.rbtv.rocketbeanstv.vo.StreamManifest
import io.lindstrom.m3u8.model.MasterPlaylist
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

    var streamPlaylist: LiveData<Resource<MasterPlaylist>> = Transformations
        .switchMap(streamManifest) { manifest ->
            if (manifest === null || manifest.data === null) {
                AbsentLiveData.create()
            } else {
                streamRepository.loadPlaylist(manifest.data.hlsUri.toString())
            }
        }

    var isServiceInfoVisible = MutableLiveData<Boolean>().apply { postValue(false) }
    var isScheduleVisible = MutableLiveData<Boolean>().apply { postValue(false) }

    fun retry() {
    //TODO: implement retry
    }
}