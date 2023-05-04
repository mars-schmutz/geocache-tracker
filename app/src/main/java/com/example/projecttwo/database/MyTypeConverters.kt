package com.example.projecttwo.database

import android.location.Location
import androidx.room.TypeConverter
import java.util.*

class MyTypeConverters {
    @TypeConverter
    fun fromLocation(loc: Location?): String? {
        return loc.toString()
    }

    @TypeConverter
    fun toLocation(loc: String?): Location? {
        return Location(loc)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }
}