package de.markhaehnel.rbtv.rocketbeanstv.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import de.markhaehnel.rbtv.rocketbeanstv.repository.StreamRepository
import de.markhaehnel.rbtv.rocketbeanstv.util.AbsentLiveData
import de.markhaehnel.rbtv.rocketbeanstv.vo.Resource
import de.markhaehnel.rbtv.rocketbeanstv.vo.TwitchAccesToken
import io.lindstrom.m3u8.model.MasterPlaylist
import javax.inject.Inject

class PlayerViewModel
@Inject constructor(
    streamRepository: StreamRepository
) : ViewModel() {

    private var rbtvServiceInfo = streamRepository.loadServiceInfo()

    private var twitchAccessToken: LiveData<Resource<TwitchAccesToken>> = Transformations
        .switchMap(rbtvServiceInfo) { serviceInfo ->
            if (serviceInfo.data === null) {
                AbsentLiveData.create()
            } else {
                streamRepository.loadAccessToken()
            }
        }

    var streamPlaylist: LiveData<Resource<MasterPlaylist>> = Transformations
        .switchMap(twitchAccessToken) { accessToken ->
            if (accessToken === null || accessToken.data === null) {
                AbsentLiveData.create()
            } else {
                val streamPlaybackAccessToken = accessToken.data.data.streamPlaybackAccessToken
                streamRepository.loadPlaylist(streamPlaybackAccessToken.value, streamPlaybackAccessToken.signature)
            }
        }

    var isBuffering = MutableLiveData<Boolean>().apply { value = true }

    fun retry() {
        //TODO: implement retry
    }
}