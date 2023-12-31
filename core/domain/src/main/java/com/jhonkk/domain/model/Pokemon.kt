package com.jhonkk.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pokemon(
    val id: Int? = null,
    val name: String = "",
    val image: String = "",
    val height: Int? = null,
    val weight: Int? = null,
    val types: List<String> = listOf(),
    val isLoadedData: Boolean = false,
    var bookmarked: Boolean = false
): Parcelable
