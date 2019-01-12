package de.markhaehnel.rbtv.rocketbeanstv.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import de.markhaehnel.rbtv.rocketbeanstv.vo.ChatMessage
import de.markhaehnel.rbtv.rocketbeanstv.vo.Resource
import io.socket.client.IO
import io.socket.client.Socket
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class ChatRepository() {
    val messages = mutableListOf<ChatMessage>()

    fun loadChatMessages(endpoint: String): LiveData<Resource<List<ChatMessage>>> {
        //TODO: move this to NetworkBoundResource
        val data = MutableLiveData<Resource<List<ChatMessage>>>()
        data.value = Resource.loading(null)

        val opts = IO.Options().apply { transports = arrayOf("websocket") }
        val socket = IO.socket(endpoint, opts)
        val gson = Gson()

        socket.on(Socket.EVENT_CONNECT) {
            socket.emit("subscribeChatMessages")
        }

        socket.on("chatMessage") { args ->
            val message = gson.fromJson(args[0].toString(), ChatMessage::class.java)

            messages.add(message)
            data.postValue(Resource.success(messages.toList()))
        }

        socket.on(Socket.EVENT_ERROR) {
        }
        socket.on(Socket.EVENT_CONNECT_ERROR) {
        }

        socket.connect()

        return data
    }
}