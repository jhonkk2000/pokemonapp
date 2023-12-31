package com.jhonkk.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jhonkk.data.model.PokemonE
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemonE ORDER BY id ASC LIMIT :limit")
    fun getPokemonByPage(limit: Int): Flow<List<PokemonE>>

    @Query("UPDATE pokemone SET bookmarked = :bookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Int, bookmarked: Boolean)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllPokemon(pokemonE: List<PokemonE>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePokemon(pokemonE: PokemonE): Int

    @Query("DELETE FROM pokemone")
    suspend fun deleteAll()

}