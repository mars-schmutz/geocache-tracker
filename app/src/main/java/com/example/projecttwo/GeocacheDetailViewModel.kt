package com.example.projecttwo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class GeocacheDetailViewModel(): ViewModel() {
    private val repository = GeocacheRepository.get()
    private val geocacheIdData = MutableLiveData<UUID>()
    var geocacheData: LiveData<Geocache?> = Transformations.switchMap(geocacheIdData) { geocacheId ->
        repository.getGeocache(geocacheId)
    }

    fun loadGeocache(geocacheId: UUID) {
        geocacheIdData.value = geocacheId
    }

    fun saveGeocache(geocache: Geocache) {
        repository.updateGeocache(geocache)
    }
}