package com.jhonkk.common.model

import com.google.gson.annotations.SerializedName

data class Sprite(
    @SerializedName("front_default")
    val frontDefault: String = ""
)
