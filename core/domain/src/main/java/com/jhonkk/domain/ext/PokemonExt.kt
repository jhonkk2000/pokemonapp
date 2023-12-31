package com.jhonkk.domain.ext

import android.util.Log
import com.google.gson.Gson
import com.jhonkk.data.model.PokemonE
import com.jhonkk.domain.model.Pokemon

fun com.jhonkk.common.model.Pokemon.toFinalPokemon() = Pokemon(
    id = this.cutId,
    name = this.name,
    image = this.sprites.frontDefault,
    height = this.height,
    weight = this.weight,
    types = this.types.map { it.type.name }
)

fun PokemonE.toPokemon() = Pokemon(
    id = this.id,
    name = this.name,
    image = this.image,
    height = this.height,
    weight = this.weight,
    types = Gson().fromJson(this.types, List::class.java) as List<String>,
    isLoadedData = this.isLoadedData,
    bookmarked = this.bookmarked
)

fun Pokemon.toPokemonE() = PokemonE(
    id = this.id,
    name = this.name,
    image = this.image,
    height = this.height,
    weight = this.weight,
    types = Gson().toJson(this.types),
    isLoadedData = this.isLoadedData,
    bookmarked = this.bookmarked
)

val com.jhonkk.common.model.Pokemon.cutId: Int
    get() {
        if (url.isEmpty()) return this.id?: -1
        val urlSplit = this.url.split("/")
        val lastNumber = urlSplit.getOrNull(urlSplit.size - 2)?.trim()
        return lastNumber?.toIntOrNull()?: -1
    }