package com.jhonkk.mylocations

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import com.google.android.gms.common.api.Response
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.ListenerRegistration
import com.jhonkk.common.model.MyLocation
import com.jhonkk.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MyLocationsViewModel @Inject constructor(
    private val repository: LocationRepository
): ViewModel() {

    private val _locationUiState: MutableStateFlow<LocationUiState> = MutableStateFlow(
        LocationUiState()
    )
    val locationUiState: StateFlow<LocationUiState> = _locationUiState.asStateFlow()

    private var listener: ListenerRegistration? = null
    fun startListenerUpdates() {
        viewModelScope.launch {
            listener?.remove()
            listener = repository.loadListener { myLocation ->
                _locationUiState.update {
                    var newList = it.list.toMutableList()
                    val filtered = newList.filter { item -> item.id == myLocation.id }
                    if (filtered.isNotEmpty()) {
                        newList = newList.map { item ->
                            if (item.id == myLocation.id) {
                                item.copy(datetime = myLocation.datetime)
                            } else{
                                item
                            }
                        }.toMutableList()
                    } else {
                        newList.add(myLocation)
                    }
                    it.copy(list = newList.sortedBy { loc -> loc.datetime })
                }
            }
        }
    }

    companion object {
        const val LOCATION_WORK_TAG = "LOCATION_WORK_TAG"
    }

}

data class LocationUiState(
    val list: List<MyLocation> = listOf()
)