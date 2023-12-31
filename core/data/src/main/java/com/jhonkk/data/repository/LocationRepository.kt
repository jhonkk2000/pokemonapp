package com.jhonkk.data.repository

import com.jhonkk.common.model.MyLocation
import com.jhonkk.data.dao.LocationDao
import com.jhonkk.data.model.LocationE
import com.jhonkk.network.LocationNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocationRepository @Inject constructor(private val locationDao: LocationDao, private val network: LocationNetwork) {

    fun getLocations() = locationDao.getAllLocations()

    fun insertLocation(locationE: LocationE) = locationDao.insertLocation(locationE)

    fun insertNetworkLocation(myLocation: MyLocation) = flow { emit(network.insertLocation(myLocation)) }.flowOn(Dispatchers.IO)

    fun loadListener(onNewItem: (MyLocation) -> Unit) = network.loadListener(onNewItem)

}