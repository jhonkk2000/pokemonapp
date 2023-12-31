package com.jhonkk.common.model

data class Pokemon(
    val id: Int? = null,
    val name: String = "",
    val height: Int? = null,
    val weight: Int? = null,
    val url: String = "",
    val sprites: Sprite = Sprite(),
    val types: List<PokemonType> = listOf()
)
