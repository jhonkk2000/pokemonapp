package com.jhonkk.network.api

import com.jhonkk.common.model.Pokemon
import com.jhonkk.common.model.PokemonResource
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val QUERY_LIMIT = 25

interface PokemonApi {

    @GET("pokemon")
    suspend fun getPokemons(
        @Query("limit") limit: Int = QUERY_LIMIT,
        @Query("offset") offset: Int
    ): Response<PokemonResource>

    @GET("pokemon/{value}")
    suspend fun getPokemonByIdOrName(
        @Path("value") value: Any
    ): Response<Pokemon>

}