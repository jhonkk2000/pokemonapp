package com.jhonkk.pokemon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jhonkk.pokemon.databinding.ItemTypeBinding

class PokemonTypeAdapter(private val types: List<String>): RecyclerView.Adapter<PokemonTypeAdapter.PokemonTypeHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PokemonTypeAdapter.PokemonTypeHolder {
        return PokemonTypeHolder(ItemTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PokemonTypeAdapter.PokemonTypeHolder, position: Int) {
        holder.bind(types[position])
    }

    override fun getItemCount(): Int = types.size

    inner class PokemonTypeHolder(private val binding: ItemTypeBinding): ViewHolder(binding.root) {
        fun bind(type: String) {
            binding.tvType.text = type
        }
    }
}