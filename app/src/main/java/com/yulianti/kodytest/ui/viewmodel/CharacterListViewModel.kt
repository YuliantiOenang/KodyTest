package com.yulianti.kodytest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.PaginatedResult
import com.yulianti.kodytest.data.repository.CharacterRepository
import com.yulianti.kodytest.di.RequestLimit
import com.yulianti.kodytest.util.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val repository: CharacterRepository,
    @RequestLimit private val requestLimit: Int
): ViewModel() {
    private val _characterFlow = MutableStateFlow<CharacterUiState?>(null)
    val characterFlow: StateFlow<CharacterUiState?> = _characterFlow.asStateFlow()
    private var servingItem = 0;
    private var currentOffset = 0
    private var totalRequest = 0
    fun getCharacter(keyword: String? = null, offset: Int = 0) {
        viewModelScope.launch {
            _characterFlow.value = CharacterUiState(isLoading = true)

            val result = repository.getCharacter(keyword, requestLimit, offset)
            _characterFlow.value = CharacterUiState(isLoading = false)
            when(result) {
                is CustomResult.Error -> {
                    val errorMessage = result.error.asUiText()
                    _characterFlow.value = CharacterUiState(error = errorMessage)
                }
                is CustomResult.Success -> {
                    currentOffset = requestLimit
                    totalRequest += 1
                    servingItem += result.data.items.size
                    _characterFlow.value = CharacterUiState(items = result.data)
                }
            }
        }
    }

    fun loadMore(keyword: String? = null) {
        viewModelScope.launch {
            _characterFlow.update { currentState ->
                currentState?.copy(
                    isLoading = true
                )
            }
            currentOffset = totalRequest * requestLimit
            println("yulianti load more from view model")
            val result = repository.getCharacter(keyword, requestLimit, currentOffset)
            when(result) {
                is CustomResult.Error -> {
                    val errorMessage = result.error.asUiText()
                    _characterFlow.update { currentState ->
                        currentState?.copy(
                            error = errorMessage
                        )
                    }
                }
                is CustomResult.Success -> {
                    totalRequest += 1
                    val previousData = _characterFlow.value?.items ?: PaginatedResult(listOf(), 0, 0,0)
                    val newData = previousData.items + result.data.items
                    _characterFlow.update { currentState ->
                        currentState?.copy(
                            items = PaginatedResult(newData, result.data.totalSize, result.data.offset, result.data.count)
                        )
                    }
                    _characterFlow.value = CharacterUiState(items = PaginatedResult(newData, result.data.totalSize, result.data.offset, result.data.count))
                }
            }
        }
    }

    fun isAllDataLoaded(): Boolean {
        return _characterFlow.value?.items?.totalSize == servingItem
    }

    data class CharacterUiState(
        val items: PaginatedResult<Character> = PaginatedResult(listOf(), 0,0,0),
        val isLoading: Boolean = false,
        val error: Int = 0
    )
}