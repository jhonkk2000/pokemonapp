package com.jhonkk.domain.usecase

import android.util.Log
import com.jhonkk.data.repository.PokemonRepository
import com.jhonkk.domain.ext.toFinalPokemon
import com.jhonkk.domain.ext.toPokemon
import com.jhonkk.domain.ext.toPokemonE
import com.jhonkk.domain.model.Pokemon
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

const val POKEMONS_LIMIT = 25

class GetPokemonsUseCase @Inject constructor(private val pokemonRepository: PokemonRepository) {

    val exceptionHandler = CoroutineExceptionHandler{_ , throwable->
        throwable.printStackTrace()
    }

    operator fun invoke(page: Int): Flow<List<Pokemon>> {
        val totalPokemons = (page + 1) * POKEMONS_LIMIT
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            pokemonRepository.getPokemonsFlow(page)
                .collect { response ->
                    if (response.isSuccessful) {
                        val list = response.body()?.results
                        list?.let {
                            pokemonRepository.insertPokemon(it.map { pokemon ->
                                pokemon.toFinalPokemon().toPokemonE()
                            })
                        }
                    } else {
                        throw Throwable("Error al cargar pokemons")
                    }
                }
        }
        val localFlow = pokemonRepository.getLocalPokemons(limit = totalPokemons)
            .map {
                it.map { pokemonE -> pokemonE.toPokemon() }
            }

        return localFlow
    }

}