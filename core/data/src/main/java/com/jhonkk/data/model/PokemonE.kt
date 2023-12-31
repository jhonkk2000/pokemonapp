package com.jhonkk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PokemonE(
    @PrimaryKey val id: Int? = null,
    val name: String = "",
    val image: String = "",
    val height: Int? = null,
    val weight: Int? = null,
    val types: String = "",
    val bookmarked: Boolean = false,
    val isLoadedData: Boolean = false
)
