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
class CharacterListViewModel @Inject constructor(
    private val repository: CharacterRepository
): ViewModel() {
    private val _characterFlow = MutableStateFlow<CharacterUiState?>(null)
    val characterFlow: StateFlow<CharacterUiState?> = _characterFlow.asStateFlow()

    fun getCharacter() {
        viewModelScope.launch {
            _characterFlow.emit(CharacterUiState.Loading)
            val result = repository.getCharacter(null, 0,10)
            when(result) {
                is CustomResult.Error -> {
                    val errorMessage = result.error.asUiText()
                    _characterFlow.emit(CharacterUiState.Error(errorMessage))
                }
                is CustomResult.Success -> {
                    _characterFlow.emit(CharacterUiState.Success(result.data))
                }
            }
        }
    }

    sealed interface CharacterUiState {
        data object Loading : CharacterUiState

        data class Success(
            val feed: List<Character>,
        ) : CharacterUiState

        data class Error(
            val error: Int
        ): CharacterUiState
    }
}