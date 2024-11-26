package com.yulianti.kodytest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.repository.CharacterRepository
import com.yulianti.kodytest.util.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private var repository: CharacterRepository
): ViewModel() {
    private val _characterFlow = MutableStateFlow<CharacterDetailUiState?>(null)
    val characterFlow: StateFlow<CharacterDetailUiState?> = _characterFlow.asStateFlow()
    fun getCharacterDetail(id: Int) {
        viewModelScope.launch {
            _characterFlow.emit(CharacterDetailUiState.Loading)
            val result = repository.getCharacterDetail(id)
            when (result) {
                is CustomResult.Success -> {
                    _characterFlow.emit(CharacterDetailUiState.Success(result.data))
                }
                is CustomResult.Error -> {
                    _characterFlow.emit(CharacterDetailUiState.Error(result.error.asUiText()))
                }
            }
        }
    }

    sealed interface CharacterDetailUiState {
        data object Loading : CharacterDetailUiState

        data class Success(
            val feed: Character,
        ) : CharacterDetailUiState

        data class Error(
            val error: Int
        ): CharacterDetailUiState
    }

}