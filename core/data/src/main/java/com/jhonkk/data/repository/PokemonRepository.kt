package com.jhonkk.data.repository

import com.jhonkk.data.dao.PokemonDao
import com.jhonkk.data.model.PokemonE
import com.jhonkk.network.PokemonNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PokemonRepository @Inject constructor(private val network: PokemonNetwork, private val pokemonDao: PokemonDao) {

    fun getPokemonsFlow(page: Int) = flow { emit(network.getPokemons(page)) }.flowOn(Dispatchers.IO)

    fun getPokemonFlow(name: String) = flow { emit(network.getPokemon(name)) }.flowOn(Dispatchers.IO)

    fun getLocalPokemons(limit: Int) = pokemonDao.getPokemonByPage(limit)

    suspend fun insertPokemon(list: List<PokemonE>) = pokemonDao.insertAllPokemon(list)

    suspend fun updateBookmarked(id: Int, bookmarked: Boolean) = pokemonDao.updateBookmark(id, bookmarked)

    fun updatePokemon(pokemonE: PokemonE) = pokemonDao.updatePokemon(pokemonE)

    fun deleteAllLocal() = flow { emit(pokemonDao.deleteAll()) }

}