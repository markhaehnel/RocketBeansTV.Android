package de.markhaehnel.rbtv.rocketbeanstv.ui.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

internal class SharedViewModel : ViewModel() {
    val chatVisible = MutableLiveData<Boolean>(false)
    val scheduleTrigger = MutableLiveData<Boolean>(false)
    val serviceInfoTrigger = MutableLiveData<Boolean>(false)

    fun toggleChat() {
        chatVisible.postValue(chatVisible.value == false)
    }

    fun showSchedule() {
        scheduleTrigger.postValue(true)
    }
    fun resetSchedule() {
        scheduleTrigger.postValue(false)
    }

    fun showServiceInfo() {
        serviceInfoTrigger.postValue(true)
    }
    fun resetServiceInfo() {
        serviceInfoTrigger.postValue(false)
    }
}