package com.jhonkk.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jhonkk.data.model.LocationE
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM LocationE ORDER BY datetime DESC")
    fun getAllLocations(): Flow<List<LocationE>>

    @Insert
    fun insertLocation(locationE: LocationE)

}