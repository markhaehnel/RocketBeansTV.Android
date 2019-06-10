package de.markhaehnel.rbtv.rocketbeanstv.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import de.markhaehnel.rbtv.rocketbeanstv.repository.ChatRepository
import de.markhaehnel.rbtv.rocketbeanstv.repository.StreamRepository
import de.markhaehnel.rbtv.rocketbeanstv.util.AbsentLiveData
import de.markhaehnel.rbtv.rocketbeanstv.vo.ChatMessage
import de.markhaehnel.rbtv.rocketbeanstv.vo.RbtvServiceInfo
import de.markhaehnel.rbtv.rocketbeanstv.vo.Resource
import javax.inject.Inject

class ChatViewModel
@Inject constructor(
    streamRepository: StreamRepository,
    chatRepository: ChatRepository
) : ViewModel() {

    private val rbtvServiceInfo: LiveData<Resource<RbtvServiceInfo>> = streamRepository.loadServiceInfo()

    var chatMessages: LiveData<Resource<List<ChatMessage>>> = Transformations
        .switchMap(rbtvServiceInfo) { serviceInfo ->
            if (serviceInfo === null || serviceInfo.data === null) {
                AbsentLiveData.create()
            } else {
                chatRepository.loadChatMessages(serviceInfo.data.service.webSocket.url)
            }
        }

    fun retry() {
        //TODO: implement retry
    }
}