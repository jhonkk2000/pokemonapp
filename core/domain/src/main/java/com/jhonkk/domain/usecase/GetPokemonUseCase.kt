package com.jhonkk.domain.usecase

import android.util.Log
import com.jhonkk.data.repository.PokemonRepository
import com.jhonkk.domain.ext.toFinalPokemon
import com.jhonkk.domain.ext.toPokemonE
import com.jhonkk.domain.model.Pokemon
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetPokemonUseCase @Inject constructor(private val pokemonRepository: PokemonRepository) {

    val exceptionHandler = CoroutineExceptionHandler{_ , throwable->
        throwable.printStackTrace()
    }

    operator fun invoke(name: String) {
         CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
             pokemonRepository.getPokemonFlow(name)
                 .collect {  response ->
                     if (response?.isSuccessful == true) {
                         val pokemon = response.body()?.toFinalPokemon()
                         pokemon?.let {
                             val savePokemon = it.copy(isLoadedData = true).toPokemonE()
                             try {
                                 pokemonRepository.updatePokemon(savePokemon)
                             }catch (e: Exception) {
                                 Log.d("collectMapRoomData", "fail: ${e.message}")
                             }


                         }
                     } else {
                         throw Throwable("Error al cargar pokemon")
                     }
                 }
         }
    }

}