package com.example.projecttwo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.projecttwo.Geocache

@Database(entities = [Geocache::class], version=1)
@TypeConverters(MyTypeConverters::class)
abstract class GeocacheDatabase: RoomDatabase() {
    abstract fun geocacheDao(): GeocacheDao
}