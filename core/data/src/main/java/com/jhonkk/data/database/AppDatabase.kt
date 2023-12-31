package com.jhonkk.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jhonkk.data.dao.LocationDao
import com.jhonkk.data.dao.PokemonDao
import com.jhonkk.data.model.LocationE
import com.jhonkk.data.model.PokemonE

@Database(entities = [PokemonE::class, LocationE::class], version = 4)
abstract class AppDatabase: RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun locationDao(): LocationDao
}