package de.markhaehnel.rbtv.rocketbeanstv.ui.serviceinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import de.markhaehnel.rbtv.rocketbeanstv.repository.StreamRepository
import de.markhaehnel.rbtv.rocketbeanstv.vo.*
import javax.inject.Inject

class ServiceInfoViewModel
@Inject constructor(streamRepository: StreamRepository) : ViewModel() {

    val serviceInfo: LiveData<Resource<RbtvServiceInfo>> = streamRepository.loadServiceInfo()

    fun retry() {
        //TODO: implement retry
    }
}