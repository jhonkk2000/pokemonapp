package com.jhonkk.domain.usecase

import android.util.Log
import com.jhonkk.data.repository.PokemonRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateBookmarkUseCase @Inject constructor(private val pokemonRepository: PokemonRepository) {

    operator fun invoke(id: Int, bookmarked: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            pokemonRepository.updateBookmarked(id, bookmarked)
        }
    }

}