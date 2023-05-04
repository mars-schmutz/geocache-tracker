package com.example.projecttwo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.projecttwo.database.GeocacheDao
import com.example.projecttwo.database.GeocacheDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "geocache-database"
class GeocacheRepository private constructor(context: Context) {
    private val database: GeocacheDatabase = Room.databaseBuilder(
        context.applicationContext,
        GeocacheDatabase::class.java,
        DATABASE_NAME
    ).build()
    private val geocacheDao = database.geocacheDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getGeocaches(): LiveData<List<Geocache>> {
        return geocacheDao.getGeocaches()
    }

    fun getGeocache(id: UUID): LiveData<Geocache?> {
        return geocacheDao.getGeocache(id)
    }

    fun updateGeocache(geocache: Geocache) {
        executor.execute {
            geocacheDao.updateGeocache(geocache)
        }
    }

    fun addGeocache(geocache: Geocache) {
        executor.execute {
            geocacheDao.addGeocache(geocache)
        }
    }

    companion object {
        private var INSTANCE: GeocacheRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = GeocacheRepository(context)
            }
        }

        fun get(): GeocacheRepository {
            return INSTANCE ?: throw IllegalStateException("Repository must be initialized")
        }
    }
}