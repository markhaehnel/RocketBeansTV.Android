package de.markhaehnel.rbtv.rocketbeanstv.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import de.markhaehnel.rbtv.rocketbeanstv.AppExecutors
import de.markhaehnel.rbtv.rocketbeanstv.api.ApiResponse
import de.markhaehnel.rbtv.rocketbeanstv.api.ApiErrorResponse
import de.markhaehnel.rbtv.rocketbeanstv.api.ApiSuccessResponse
import de.markhaehnel.rbtv.rocketbeanstv.vo.Resource

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */
abstract class NetworkBoundResource<ResultType>
@MainThread constructor(private val appExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        fetchFromNetwork()
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork() {
        val apiResponse = createCall()

        //Listen for changes until new data arrives
        result.addSource(apiResponse) { response ->
            //Stop listening to changes
            result.removeSource(apiResponse)

            when (response) {
                is ApiSuccessResponse -> {
                    setValue(Resource.success(response.body))
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    setValue(Resource.error(response.errorMessage))
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<ResultType>) = response.body

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<ResultType>>
}