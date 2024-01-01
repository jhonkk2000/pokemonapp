package com.jhonkk.domain.usecase

import android.util.Log
import com.jhonkk.data.repository.PokemonRepository
import com.jhonkk.domain.ext.toFinalPokemon
import com.jhonkk.domain.ext.toPokemonE
import com.jhonkk.domain.model.Pokemon
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetPokemonUseCase @Inject constructor(private val pokemonRepository: PokemonRepository) {

    suspend operator fun invoke(name: String) {
        pokemonRepository.getPokemonFlow(name)
            .collect {  response ->
                if (response?.isSuccessful == true) {
                    val pokemon = response.body()?.toFinalPokemon()
                    pokemon?.let {
                        val savePokemon = it.copy(isLoadedData = true).toPokemonE()
                        pokemonRepository.updatePokemon(savePokemon)
                    }
                } else {
                    throw Throwable("Error al cargar pokemon")
                }
            }
    }

}