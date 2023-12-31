package com.jhonkk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationE(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val datetime: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null
)
