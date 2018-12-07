package de.markhaehnel.rbtv.rocketbeanstv.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.markhaehnel.rbtv.rocketbeanstv.vo.ScheduleItem
import de.markhaehnel.rbtv.rocketbeanstv.vo.Stream

@Dao
interface ScheduleItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(scheduleItem: ScheduleItem)

    @Query("SELECT * FROM scheduleitem LIMIT 1")
    fun find(): LiveData<ScheduleItem>
}