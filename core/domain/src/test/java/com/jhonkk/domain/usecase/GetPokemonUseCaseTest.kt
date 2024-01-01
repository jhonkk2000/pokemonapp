package com.jhonkk.domain.usecase

import com.jhonkk.common.model.Pokemon
import com.jhonkk.common.model.PokemonResource
import com.jhonkk.data.repository.PokemonRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class GetPokemonUseCaseTest {

    @RelaxedMockK
    private lateinit var pokemonRepository: PokemonRepository

    lateinit var getPokemonUseCase: GetPokemonUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getPokemonUseCase = GetPokemonUseCase(pokemonRepository)
    }

    @Test
    fun `when full data pokemon from api is successful then save on db`() = runBlocking {
        coEvery { pokemonRepository.getPokemonFlow(any()) } returns  flow { emit(
            Response.success(
                Pokemon()
            )) }

        getPokemonUseCase("")

        coVerify { pokemonRepository.updatePokemon(any()) }
    }

}