package com.jhonkk.common.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName

data class MyLocation(
    val id: String? = null,
    @ServerTimestamp
    val datetime: Timestamp? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)