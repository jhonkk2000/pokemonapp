package com.jhonkk.pokemon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.jhonkk.common.extension.gone
import com.jhonkk.common.extension.visible
import com.jhonkk.domain.model.Pokemon
import com.jhonkk.pokemon.adapter.PokemonAdapter
import com.jhonkk.pokemon.databinding.FragmentPokemonBinding
import com.jhonkk.pokemon.dialog.PokemonDetailDialog
import com.jhonkk.pokemon.viewmodel.PokemonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonFragment: Fragment() {

    private var _binding: FragmentPokemonBinding? = null
    private val binding: FragmentPokemonBinding
        get() = _binding!!

    private lateinit var adapter: PokemonAdapter
    private val viewModel: PokemonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentPokemonBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initRecycler()

        lifecycleScope.launch {
            viewModel.pokemonUiState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { state ->
                    if (state.isLoadingMore) binding.linearProgress.visible() else binding.linearProgress.gone()
                    adapter.submitList(state.list)
                }
        }
        if (viewModel.currentPage == -1) viewModel.getPokemons(onClickPokemon)
        setupMoreData()

        return binding.root
    }

    private val onClickPokemon = { pokemon: Pokemon ->
        childFragmentManager.executePendingTransactions()
        if (isAdded) {
            val dialog = PokemonDetailDialog.newInstance(pokemon)
            dialog.pdi = object : PokemonDetailDialog.PokemonDetailInterface {
                override fun onBookmark(id: Int, bookmarked: Boolean) {
                    adapter.submitList(null)
                    viewModel.updateBookmarked(id, bookmarked)
                }
            }
            dialog.show(childFragmentManager, "POKEMON_DETAIL")
        }
    }

    private fun setupMoreData() {
        binding.rvPokemons.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                layoutManager?.let {
                    if (it.findLastVisibleItemPosition() == adapter.itemCount - 1
                        && !viewModel.pokemonUiState.value.isLoadingMore) {
                        //loadMore
                        viewModel.getPokemons(onClickPokemon)
                    }
                }
            }
        })
    }

    private fun initRecycler() {
        adapter = PokemonAdapter(requireContext())
        binding.rvPokemons.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPokemons.adapter = adapter
    }

}