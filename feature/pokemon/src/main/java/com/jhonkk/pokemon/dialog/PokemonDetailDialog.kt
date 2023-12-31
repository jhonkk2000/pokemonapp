package com.jhonkk.pokemon.dialog

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jhonkk.domain.model.Pokemon
import com.jhonkk.pokemon.R
import com.jhonkk.pokemon.adapter.PokemonTypeAdapter
import com.jhonkk.pokemon.databinding.DialogPokemonDetailBinding

class PokemonDetailDialog : DialogFragment() {

    private var _binding: DialogPokemonDetailBinding? = null
    private val binding: DialogPokemonDetailBinding
        get() = _binding!!

    private var pokemon: Pokemon? = null

    var pdi: PokemonDetailInterface? = null
    interface PokemonDetailInterface {
        fun onBookmark(id: Int, bookmarked: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogPokemonDetailBinding.inflate(layoutInflater)
        arguments?.let { bundle ->
            pokemon = bundle.getParcelable("pokemon") as? Pokemon
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pokemon?.let {
            binding.btnBookmark.setOnClickListener { _ ->
                it.id?.let { idPokemon ->
                    pdi?.onBookmark(idPokemon, !it.bookmarked)
                    it.bookmarked = !it.bookmarked
                }
                bindBookmark(it.bookmarked)
            }
            bindBookmark(it.bookmarked)
            binding.toolbar.title = it.name.uppercase()
            binding.tvId.text = "#${it.id}"
            binding.toolbar.setNavigationOnClickListener { dismiss() }
            binding.tvHeight.text = "Altura: ${it.height}"
            binding.tvWeight.text = "Peso: ${it.weight}"
            val adapter = PokemonTypeAdapter(it.types)
            binding.rvTypes.layoutManager = LinearLayoutManager(requireContext())
            binding.rvTypes.adapter = adapter
            val target = object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.pokemonDetailPic.setupWithBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    if (it.name.isEmpty()) {
                        binding.pokemonDetailPic.setupPlaceholder()
                    } else {
                        binding.pokemonDetailPic.setupWithText(it.name)
                    }
                }
            }
            Glide.with(requireContext())
                .asBitmap()
                .load(it.image)
                .into(target)
        }
        return binding.root
    }

    private fun bindBookmark(bookmarked: Boolean) {
        if (bookmarked) {
            binding.btnBookmark.setImageResource(R.drawable.ic_bookmark)
        } else {
            binding.btnBookmark.setImageResource(R.drawable.ic_unbookmark)
        }
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    companion object {
        fun newInstance(pokemon: Pokemon): PokemonDetailDialog {
            val dialog = PokemonDetailDialog()
            val bundle = Bundle()
            bundle.putParcelable("pokemon", pokemon)
            dialog.arguments = bundle
            return dialog
        }
    }

}