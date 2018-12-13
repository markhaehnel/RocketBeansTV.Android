package de.markhaehnel.rbtv.rocketbeanstv.db


import androidx.room.Database
import androidx.room.RoomDatabase
import de.markhaehnel.rbtv.rocketbeanstv.vo.Schedule
import de.markhaehnel.rbtv.rocketbeanstv.vo.Stream

@Database(
    entities = [Stream::class, Schedule::class],
    version = 1,
    exportSchema = false
)
abstract class RbtvDb : RoomDatabase() {

    abstract fun streamDao(): StreamDao

    abstract fun scheduleDao(): ScheduleDao

}