package com.jhonkk.pokemon.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jhonkk.domain.model.Pokemon
import com.jhonkk.pokemon.R
import com.jhonkk.pokemon.databinding.ItemLoadingBinding
import com.jhonkk.pokemon.databinding.ItemPokemonBinding

private const val LOADING_TYPE = 0
private const val POKEMON_TYPE = 1

class PokemonAdapter(val context: Context): ListAdapter<PokemonItemState?, ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == LOADING_TYPE) {
            LoadingHolder(ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            PokemonHolder(ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is PokemonHolder) {
            getItem(position)?.let { holder.bind(it) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) == null) {
            LOADING_TYPE
        } else {
            POKEMON_TYPE
        }
    }

    inner class LoadingHolder(binding: ItemLoadingBinding): ViewHolder(binding.root)

    inner class PokemonHolder(private val binding: ItemPokemonBinding): ViewHolder(binding.root) {
        fun bind(state: PokemonItemState) {
            binding.layoutContent.setOnClickListener { state.onClick() }
            binding.tvStatusData.text = when (state.statusData) {
                StatusDataPokemon.LOADING -> "Datos: Cargando"
                StatusDataPokemon.LOADED -> "Datos: Cargados"
            }
            state.pokemon.apply {
                binding.tvPokemonName.text = name
                binding.btnBookmark.setOnClickListener { state.onBookmark(!bookmarked) }
                if (bookmarked) {
                    binding.btnBookmark.setImageResource(R.drawable.ic_bookmark)
                } else {
                    binding.btnBookmark.setImageResource(R.drawable.ic_unbookmark)
                }
                val target = object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        binding.pokemonPic.setupWithBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        if (name.isEmpty()) {
                            binding.pokemonPic.setupPlaceholder()
                        } else {
                            binding.pokemonPic.setupWithText(name)
                        }
                    }
                }
                val requestOptions = RequestOptions()
                    .override(100,100)
                Glide.with(context)
                    .asBitmap()
                    .apply(requestOptions)
                    .load(image)
                    .into(target)
            }
        }
    }

}

data class PokemonItemState(
    val pokemon: Pokemon = Pokemon(),
    val onClick: () -> Unit = {},
    val onBookmark: (Boolean) -> Unit = {},
    val statusData: StatusDataPokemon = StatusDataPokemon.LOADING
)

enum class StatusDataPokemon {
    LOADING,
    LOADED
}

private class DiffUtilCallback: DiffUtil.ItemCallback<PokemonItemState?>() {
    override fun areItemsTheSame(oldItem: PokemonItemState, newItem: PokemonItemState): Boolean
        = oldItem.pokemon.id == newItem.pokemon.id

    override fun areContentsTheSame(oldItem: PokemonItemState, newItem: PokemonItemState): Boolean
        = oldItem.pokemon == newItem.pokemon

}