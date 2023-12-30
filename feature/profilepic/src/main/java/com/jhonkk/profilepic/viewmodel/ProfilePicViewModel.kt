package com.jhonkk.profilepic.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhonkk.profilepic.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfilePicViewModel @Inject constructor() : ViewModel() {

    private val _imageUiState: MutableStateFlow<ImageUiState> = MutableStateFlow(ImageUiState())
    val imageUiState: StateFlow<ImageUiState> = _imageUiState.asStateFlow()

    fun setName(name: String) {
        viewModelScope.launch {
            _imageUiState.update { it.copy(name = name) }
        }
    }

    fun setBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            _imageUiState.update { it.copy(bitmap = bitmap) }
        }
    }

    fun setUrlError(error: String?) {
        viewModelScope.launch {
            _imageUiState.update { it.copy(urlError = error) }
        }
    }

    fun setTextColor(color: String) {
        viewModelScope.launch {
            _imageUiState.update { it.copy(textColor = color) }
        }
    }

    fun setBackgroundColor(color: String) {
        viewModelScope.launch {
            _imageUiState.update { it.copy(backgroundColor = color) }
        }
    }

    fun setPlaceHolder(placeholder: Int) {
        viewModelScope.launch {
            _imageUiState.update { it.copy(placeholder = placeholder) }
        }
    }

}

data class ImageUiState(
    val bitmap: Bitmap? = null,
    val name: String? = null,
    val urlError: String? = null,
    val placeholder: Int = R.drawable.ic_placeholder,
    val textColor: String = java.lang.String.format("#%06X", 0xFFFFFF and Color.RED),
    val backgroundColor: String = java.lang.String.format("#%06X", 0xFFFFFF and Color.CYAN)
)