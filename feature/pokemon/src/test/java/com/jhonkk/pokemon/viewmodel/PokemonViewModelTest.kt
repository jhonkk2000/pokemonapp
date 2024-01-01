package com.jhonkk.pokemon.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jhonkk.domain.model.Pokemon
import com.jhonkk.domain.usecase.GetPokemonUseCase
import com.jhonkk.domain.usecase.GetPokemonsUseCase
import com.jhonkk.domain.usecase.UpdateBookmarkUseCase
import com.jhonkk.pokemon.adapter.StatusDataPokemon
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PokemonViewModelTest {

    @RelaxedMockK
    private lateinit var getPokemonUseCase: GetPokemonUseCase

    @RelaxedMockK
    private lateinit var getPokemonsUseCase: GetPokemonsUseCase

    @RelaxedMockK
    private lateinit var updateBookmarkUseCase: UpdateBookmarkUseCase

    private lateinit var pokemonViewModel: PokemonViewModel

    @get:Rule
    var rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        pokemonViewModel = PokemonViewModel(getPokemonsUseCase, getPokemonUseCase, updateBookmarkUseCase)
        Dispatchers.setMain(Dispatchers.IO)
    }

    @After
    fun onAfter() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when get list of pokemons from api, get full data of all pokemons`() = runTest {
        val pokemonList = listOf(Pokemon(), Pokemon(), Pokemon())
        coEvery { getPokemonsUseCase(any()) } returns flow { emit(pokemonList) }

        pokemonViewModel.getPokemons()

//        assert(pokemonViewModel.pokemonUiState.value.list.isEmpty())
        coVerify(exactly = pokemonList.size) { getPokemonUseCase(any()) }
    }

}