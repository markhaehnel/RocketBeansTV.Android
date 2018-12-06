package de.markhaehnel.rbtv.rocketbeanstv.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.markhaehnel.rbtv.rocketbeanstv.vo.Stream

@Dao
interface StreamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stream: Stream)

    @Query("SELECT * FROM stream LIMIT 1")
    fun find(): LiveData<Stream>
}