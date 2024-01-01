package com.jhonkk.pokemon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhonkk.domain.model.Pokemon
import com.jhonkk.domain.usecase.GetPokemonUseCase
import com.jhonkk.domain.usecase.GetPokemonsUseCase
import com.jhonkk.domain.usecase.UpdateBookmarkUseCase
import com.jhonkk.pokemon.adapter.PokemonItemState
import com.jhonkk.pokemon.adapter.StatusDataPokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val getPokemonsUseCase: GetPokemonsUseCase,
    private val getPokemonUseCase: GetPokemonUseCase,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase
) : ViewModel() {

    private val _pokemonState: MutableSharedFlow<PokemonState> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val pokemonState: SharedFlow<PokemonState> = _pokemonState.asSharedFlow()

    private val _pokemonUiState: MutableStateFlow<PokemonUiState> =
        MutableStateFlow(PokemonUiState())
    val pokemonUiState: StateFlow<PokemonUiState> = _pokemonUiState.asStateFlow()

    var currentPage = -1

    private var jobPokemons: Job? = null
    fun getPokemons() {
        jobPokemons?.cancel()
        jobPokemons = viewModelScope.launch(Dispatchers.IO) {
            currentPage++
            _pokemonUiState.update { it.copy(isLoadingMore = true) }
            //Delay for improve experience without connection
            delay(1200)
            getPokemonsUseCase.invoke(currentPage).catch {
                _pokemonUiState.update { it.copy(isLoadingMore = false) }
            }.collect { list ->
                _pokemonUiState.update { state ->
                    val newList = list.map { item ->
                        PokemonItemState(
                            pokemon = item,
                            statusData = if (item.isLoadedData) StatusDataPokemon.LOADED else StatusDataPokemon.LOADING,
                            onClick = { _pokemonState.tryEmit(PokemonState.OpenDialog(item)) },
                            onBookmark = { bookmarked ->
                                item.id?.let { updateBookmarked(it, bookmarked) }
                            },
                        )
                    }
                    state.copy(list = newList, isLoadingMore = newList.isEmpty())
                }
                loadFullDataPokemon(list)
            }
        }
    }

    private fun loadFullDataPokemon(list: List<Pokemon>) {
        list.forEach { pokemon ->
            if (!pokemon.isLoadedData) {
                getPokemon(pokemon)
            }
        }
    }

    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }

    fun getPokemon(pokemon: Pokemon) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            getPokemonUseCase.invoke(pokemon.name)
        }
    }

    fun updateBookmarked(id: Int, bookmarked: Boolean) {
        viewModelScope.launch {
            updateBookmarkUseCase.invoke(id, bookmarked)
        }
    }

    fun clearState() {
        viewModelScope.launch {
            _pokemonState.tryEmit(PokemonState.Init)
        }
    }

}

data class PokemonUiState(
    val list: List<PokemonItemState> = listOf(),
    val isLoadingMore: Boolean = false
)

sealed class PokemonState {
    object Init: PokemonState()
    data class OpenDialog(val pokemon: Pokemon): PokemonState()
}