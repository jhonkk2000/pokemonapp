package com.jhonkk.profilepic

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jhonkk.common.view.ProfilePicView
import com.jhonkk.profilepic.databinding.FragmentProfilePicBinding
import com.jhonkk.profilepic.view.PlaceholderOptionsFragment
import com.jhonkk.profilepic.view.TextOptionsFragment
import com.jhonkk.profilepic.viewmodel.ImageUiState
import com.jhonkk.profilepic.viewmodel.ProfilePicViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfilePicFragment: Fragment() {

    private var _binding: FragmentProfilePicBinding? = null
    private val binding: FragmentProfilePicBinding
        get() = _binding!!

    private val fragmentTextOptions: TextOptionsFragment by lazy { TextOptionsFragment() }
    private val fragmentPlaceholderOptions: PlaceholderOptionsFragment by lazy { PlaceholderOptionsFragment() }
    private val viewModel: ProfilePicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentProfilePicBinding.inflate(layoutInflater)
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
                    bindImage(state)
                }
        }

        setupProfilePic()
        binding.etName.doOnTextChanged { text, start, before, count ->
            viewModel.setName(text.toString())
        }
        binding.etUrl.doOnTextChanged { text, start, before, count ->
            loadImageFromUrl(text.toString())
        }


        return binding.root
    }

    private fun setupProfilePic() {
        val callback = object : ProfilePicView.ProfilePicCallback {
            override fun activePlaceholder() {
                replaceFragment(fragmentPlaceholderOptions)
            }

            override fun activeBitmap() {
                cleanFragment()
            }

            override fun activeText() {
                replaceFragment(fragmentTextOptions)
            }

        }
        binding.profilePicView.addCallbackStates(callback)
    }

    private var target: CustomTarget<Bitmap>? = null
    private fun loadImageFromUrl(url: String) {
        target?.request?.clear()
        binding.profilePicView.setLoading(false)
        if (URLUtil.isValidUrl(url)) {
            viewModel.setUrlError(null)
            binding.profilePicView.setLoading(true)
            target = object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.profilePicView.setLoading(false)
                    viewModel.setBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    binding.profilePicView.setLoading(false)
                    viewModel.setBitmap(null)
                }
            }
            Glide.with(requireContext())
                .asBitmap()
                .load(url)
                .into(target!!)
        }else {
            viewModel.setUrlError("Url inválida")
            viewModel.setBitmap(null)
        }

    }

    private fun bindImage(state: ImageUiState) {
        binding.etUrl.error = state.urlError
        binding.profilePicView.setTextColor(state.textColor)
        binding.profilePicView.setBackgroundColor(state.backgroundColor)
        binding.profilePicView.setPlaceholder(state.placeholder)
        val isValidName = !state.name.isNullOrEmpty() && state.name?.firstOrNull()?.isLetter() == true
        when {
            state.bitmap == null && !isValidName -> binding.profilePicView.setupPlaceholder()
            state.bitmap != null ->  binding.profilePicView.setupWithBitmap(state.bitmap)
            isValidName -> binding.profilePicView.setupWithText(state.name!!)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(binding.containerOptions.id, fragment).commit()
    }

    private fun cleanFragment() {
        for (fragment in childFragmentManager.fragments) {
            childFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}