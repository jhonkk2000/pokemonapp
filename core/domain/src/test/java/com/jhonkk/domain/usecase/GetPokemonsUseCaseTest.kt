package com.jhonkk.domain.usecase

import com.jhonkk.common.model.PokemonResource
import com.jhonkk.data.repository.PokemonRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class GetPokemonsUseCaseTest {

    @RelaxedMockK
    private lateinit var pokemonRepository: PokemonRepository

    lateinit var getPokemonsUseCase: GetPokemonsUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getPokemonsUseCase = GetPokemonsUseCase(pokemonRepository)
    }

    @Test
    fun `when response from api is successful then save on db`() = runBlocking {
        coEvery { pokemonRepository.getPokemonsFlow(any()) } returns  flow { emit(Response.success(PokemonResource(results = listOf()))) }

        getPokemonsUseCase(1)

        coVerify { pokemonRepository.insertPokemon(any()) }
    }

}