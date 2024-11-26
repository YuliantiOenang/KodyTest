package com.yulianti.kodytest.data.model

typealias RootError = Error

sealed interface CustomResult<out D, out E: RootError> {
    data class Success<out D, out E: RootError>(val data: D): CustomResult<D, E>
    data class Error<out D, out E: RootError>(val error: E): CustomResult<D, E>
}