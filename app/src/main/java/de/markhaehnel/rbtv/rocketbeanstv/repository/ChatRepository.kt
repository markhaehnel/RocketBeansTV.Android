package de.markhaehnel.rbtv.rocketbeanstv.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import de.markhaehnel.rbtv.rocketbeanstv.vo.ChatMessage
import de.markhaehnel.rbtv.rocketbeanstv.vo.Resource
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Singleton

@Singleton
class ChatRepository {
    private val messages = mutableListOf<ChatMessage>()
    private val opts = IO.Options().apply { transports = arrayOf("websocket") }
    private var socket : Socket? = null
    private val gson = Gson()

    fun loadChatMessages(endpoint: String): LiveData<Resource<List<ChatMessage>>> {
        val data = MutableLiveData<Resource<List<ChatMessage>>>()

        if (socket == null) { socket = IO.socket(endpoint, opts) }

        data.value = Resource.loading(null)

        socket?.off(Socket.EVENT_CONNECT)
        socket?.on(Socket.EVENT_CONNECT) {
            socket?.emit("subscribeChatMessages")
        }

        socket?.off("chatMessage")
        socket?.on("chatMessage") { args ->
            val message = gson.fromJson(args[0].toString(), ChatMessage::class.java)
            messages.add(message)
            data.postValue(Resource.success(messages.toList()))
        }

        socket?.off(Socket.EVENT_ERROR)
        socket?.on(Socket.EVENT_ERROR) {
        }
        socket?.off(Socket.EVENT_CONNECT_ERROR)
        socket?.on(Socket.EVENT_CONNECT_ERROR) {
        }

        socket?.connect()

        return data
    }
}