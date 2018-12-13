package de.markhaehnel.rbtv.rocketbeanstv.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.markhaehnel.rbtv.rocketbeanstv.vo.Schedule

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(schedule: Schedule)

    @Query("SELECT * FROM schedule LIMIT 1")
    fun find(): LiveData<Schedule>
}