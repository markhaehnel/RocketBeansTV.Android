package de.markhaehnel.rbtv.rocketbeanstv.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import de.markhaehnel.rbtv.rocketbeanstv.repository.StreamRepository
import de.markhaehnel.rbtv.rocketbeanstv.util.AbsentLiveData
import de.markhaehnel.rbtv.rocketbeanstv.vo.Resource
import de.markhaehnel.rbtv.rocketbeanstv.vo.Stream
import de.markhaehnel.rbtv.rocketbeanstv.vo.StreamDataRaw
import javax.inject.Inject

class PlayerViewModel
@Inject constructor(streamRepository: StreamRepository) : ViewModel() {

    val stream: LiveData<Resource<Stream>> = streamRepository.loadStream()

    var streamData: LiveData<Resource<StreamDataRaw>> = Transformations
        .switchMap(stream) { stream ->
            if (stream.data === null) {
                AbsentLiveData.create()
            } else {
                streamRepository.loadStreamDataRaw(stream.data.videoId)
            }
        }


}