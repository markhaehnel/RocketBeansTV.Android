package de.markhaehnel.rbtv.rocketbeanstv.ui.startup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.markhaehnel.rbtv.rocketbeanstv.repository.StreamRepository
import de.markhaehnel.rbtv.rocketbeanstv.vo.Resource
import de.markhaehnel.rbtv.rocketbeanstv.vo.Stream
import javax.inject.Inject

class StartupViewModel
@Inject constructor(streamRepository: StreamRepository) : ViewModel() {

    private val _videoId = MutableLiveData<String>()

    val videoId: LiveData<String>
        get() = _videoId

    val streamData: LiveData<Resource<Stream>> = streamRepository.loadStream()

    fun setStream(videoId: String?) {
        if (_videoId.value != videoId) {
            _videoId.value = videoId
        }
    }

    fun retry() {
        _videoId.value?.let {
            _videoId.value = it
        }
    }
}