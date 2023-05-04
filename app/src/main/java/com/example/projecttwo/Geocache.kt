package com.example.projecttwo

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//import java.util.*

@Entity
data class Geocache(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var coords: String = "",
    var city: String = "",
    var hidingPlace: String = "",
    var rating: Int = 0
)