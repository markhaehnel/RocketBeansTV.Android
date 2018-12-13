package de.markhaehnel.rbtv.rocketbeanstv.db

import androidx.room.TypeConverter
import de.markhaehnel.rbtv.rocketbeanstv.vo.ScheduleItem

object RbtvTypeConverters {
    @TypeConverter
    @JvmStatic
    fun listOfStringToCommaSeperatedString(strings: List<String>): String {
        return strings.joinToString(",")
    }

    @TypeConverter
    @JvmStatic
    fun commaSeperatedStringToListOfString(string: String): List<String> {
        return string.split(",")
    }

    @TypeConverter
    @JvmStatic
    fun listOfScheduleItemToBeDetermined(items: List<ScheduleItem>): String {
        //TODO: Schedule Item list type converter
        return ""
    }

    @TypeConverter
    @JvmStatic
    fun ToBeDeterminedToListOfScheduleItem(string: String): List<ScheduleItem> {
        //TODO: Schedule Item list type converter
        return listOf(ScheduleItem(0, "","","","","",0,"",""))
    }
}