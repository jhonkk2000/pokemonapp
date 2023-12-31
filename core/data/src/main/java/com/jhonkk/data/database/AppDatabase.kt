package com.jhonkk.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jhonkk.data.dao.PokemonDao
import com.jhonkk.data.model.PokemonE

@Database(entities = [PokemonE::class], version = 3)
abstract class AppDatabase: RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}