package de.markhaehnel.rbtv.rocketbeanstv.repository

import androidx.lifecycle.LiveData
import de.markhaehnel.rbtv.rocketbeanstv.AppExecutors
import de.markhaehnel.rbtv.rocketbeanstv.api.RbtvService
import de.markhaehnel.rbtv.rocketbeanstv.db.RbtvDb
import de.markhaehnel.rbtv.rocketbeanstv.db.StreamDao
import de.markhaehnel.rbtv.rocketbeanstv.vo.Resource
import de.markhaehnel.rbtv.rocketbeanstv.vo.Stream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles User objects.
 */
@Singleton
class StreamRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: RbtvDb,
    private val streamDao: StreamDao,
    private val rbtvService: RbtvService
) {

    fun loadStream(): LiveData<Resource<Stream>> {
        return object : NetworkBoundResource<Stream, Stream>(appExecutors) {
            override fun saveCallResult(item: Stream) {
                streamDao.insert(item)
            }

            override fun shouldFetch(data: Stream?) = data == null

            override fun loadFromDb() = streamDao.find()

            override fun createCall() = rbtvService.getStream()
        }.asLiveData()
    }
}