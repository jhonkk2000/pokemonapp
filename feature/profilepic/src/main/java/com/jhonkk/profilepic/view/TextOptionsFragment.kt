package com.jhonkk.profilepic.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.jhonkk.profilepic.databinding.FragmentTextOptionsBinding
import com.jhonkk.profilepic.viewmodel.ProfilePicViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TextOptionsFragment: Fragment() {

    private var _binding: FragmentTextOptionsBinding? = null
    private val binding: FragmentTextOptionsBinding
        get() = _binding!!

    private val viewModel: ProfilePicViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentTextOptionsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lifecycleScope.launch {
            viewModel.imageUiState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { state ->
                    binding.btnTextColor.setBackgroundColor(Color.parseColor(state.textColor))
                    binding.btnBackgroundColor.setBackgroundColor(Color.parseColor(state.backgroundColor))
                }
        }

        binding.btnTextColor.setOnClickListener {
            val defaultColor = viewModel.imageUiState.value.textColor
            openColorPicker(defaultColor) {
                viewModel.setTextColor(it)
            }
        }
        binding.btnBackgroundColor.setOnClickListener {
            val defaultColor = viewModel.imageUiState.value.textColor
            openColorPicker(defaultColor) {
                viewModel.setBackgroundColor(it)
            }
        }

        return binding.root
    }

    private fun openColorPicker(defaultColor: String, onSelectColor: (String) -> Unit) {
        ColorPickerDialog
            .Builder(requireContext())
            .setTitle("Elige un color")
            .setColorShape(ColorShape.CIRCLE)
            .setDefaultColor(defaultColor)
            .setColorListener { color, colorHex ->
                onSelectColor(colorHex)
            }
            .show()

    }

}