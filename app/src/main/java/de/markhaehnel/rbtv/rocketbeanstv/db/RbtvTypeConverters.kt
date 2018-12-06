package de.markhaehnel.rbtv.rocketbeanstv.db

import androidx.room.TypeConverter

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
}