package com.jhonkk.network

import com.jhonkk.network.api.PokemonApi
import com.jhonkk.network.api.QUERY_LIMIT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class PokemonNetwork @Inject constructor(private val api: PokemonApi) {

    suspend fun getPokemons(page: Int) = withContext(Dispatchers.IO) {
        api.getPokemons(
            offset = page * QUERY_LIMIT
        )
    }

    suspend fun getPokemon(name: String) = withContext(Dispatchers.IO) {
        try {
            api.getPokemonByIdOrName(name)
        }catch (e: IOException) {
            null
        }
    }

}