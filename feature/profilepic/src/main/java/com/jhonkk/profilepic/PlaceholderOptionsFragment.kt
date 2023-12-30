package com.jhonkk.profilepic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jhonkk.profilepic.databinding.FragmentPlaceholderOptionsBinding
import com.jhonkk.profilepic.viewmodel.ProfilePicViewModel

class PlaceholderOptionsFragment: Fragment() {

    private var _binding: FragmentPlaceholderOptionsBinding? = null
    private val binding: FragmentPlaceholderOptionsBinding
        get() = _binding!!

    private val viewModel: ProfilePicViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentPlaceholderOptionsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_placeholder_1 -> viewModel.setPlaceHolder(R.drawable.ic_placeholder)
                R.id.rb_placeholder_2 -> viewModel.setPlaceHolder(R.drawable.ic_placeholder_2)
                R.id.rb_placeholder_3 -> viewModel.setPlaceHolder(R.drawable.ic_placeholder_3)
            }
        }
        binding.rbPlaceholder1.isChecked = true
        return binding.root
    }

}