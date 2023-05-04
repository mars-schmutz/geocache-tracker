package com.example.projecttwo

import androidx.lifecycle.ViewModel

private const val TAG = "GeocacheListViewModel"
class GeocacheListViewModel: ViewModel() {
    private val geocacheRepository = GeocacheRepository.get()
    val geocachesLiveData = geocacheRepository.getGeocaches()

    fun addGeocache(geocache: Geocache) {
        geocacheRepository.addGeocache(geocache)
    }
}