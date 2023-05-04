package com.example.projecttwo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.projecttwo.Geocache
import java.util.*

@Dao
interface GeocacheDao {
    @Query("SELECT * FROM geocache")
    fun getGeocaches(): LiveData<List<Geocache>>

    @Query("SELECT * FROM geocache WHERE id=(:id)")
    fun getGeocache(id: UUID): LiveData<Geocache?>

    @Update
    fun updateGeocache(geocache: Geocache)

    @Insert
    fun addGeocache(geocache: Geocache)
}