package com.yulianti.kodytest.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.model.PaginatedResult
import com.yulianti.kodytest.data.repository.CharacterRepository
import com.yulianti.kodytest.di.RequestLimit
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
) : ViewModel() {
    private val _characterFlow = MutableStateFlow<CharacterUiState?>(null)
    val characterFlow: StateFlow<CharacterUiState?> = _characterFlow.asStateFlow()

    fun getCharacter(keyword: String? = null, offset: Int = 0) {
        viewModelScope.launch {
            _characterFlow.update { currentState ->
                CharacterUiState(
                    items = currentState?.items ?: PaginatedResult(listOf(), 0, 0, 0),
                    isLoading = true
                )
            }

            when (val result = repository.getCharacter(keyword, requestLimit, offset)) {
                is CustomResult.Error -> {
                    _characterFlow.update { currentState ->
                        currentState?.copy(
                            isLoading = false,
                            error = result.error
                        )
                    }
                }

                is CustomResult.Success -> {
                    _characterFlow.update { _ ->
                        CharacterUiState(
                            isLoading = false,
                            items = result.data
                        )
                    }
                }
            }
        }
    }

    fun loadMore(keyword: String? = null) {
        viewModelScope.launch {
            // Update loading state while preserving current items
            _characterFlow.update { currentState ->
                currentState?.copy(
                    isLoading = true
                )
            }
            val currentData = _characterFlow.value?.items?.items?.size
            when (val result = repository.getCharacter(keyword, requestLimit, currentData ?: 0)) {
                is CustomResult.Error -> {
                    _characterFlow.update { currentState ->
                        currentState?.copy(
                            isLoading = false,
                            error = result.error
                        )
                    }
                }

                is CustomResult.Success -> {
                    val currentItems = _characterFlow.value?.items?.items ?: listOf()
                    val newItems = currentItems + result.data.items

                    _characterFlow.update { currentState ->
                        currentState?.copy(
                            isLoading = false,
                            items = PaginatedResult(
                                newItems,
                                result.data.totalSize,
                                result.data.offset,
                                result.data.count
                            )
                        )
                    }
                }
            }
        }
    }

    private fun isAllDataLoaded(): Boolean {
        return _characterFlow.value?.items?.totalSize == _characterFlow.value?.items?.items?.size
    }

    fun isEmpty(): Boolean {
        return _characterFlow.value?.items?.items?.isEmpty() == true
    }

    fun onScroll(lastVisibleItemPosition: Int, totalItemCount: Int, keyword: String?) {
        if (lastVisibleItemPosition + 1 == totalItemCount &&
            _characterFlow.value?.isLoading != true && !isAllDataLoaded()
        ) {
            loadMore(keyword)
        }
    }

    data class CharacterUiState(
        val items: PaginatedResult<Character> = PaginatedResult(listOf(), 0, 0, 0),
        val isLoading: Boolean = false,
        val error: DataError? = null
    )
}