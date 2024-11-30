package com.yulianti.kodytest

import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.model.PaginatedResult

// Sample character data
val sampleCharacter = Character(
    id = 1,
    name = "Test Character",
    coverUrl = "",
    description = ""
)

// Sample paginated result
val samplePaginatedResult = PaginatedResult(
    items = listOf(sampleCharacter),
    totalSize = 1,
    offset = 10,
    count = 0
)

// CustomResult success and error instances
val successCharacterListResult: CustomResult<PaginatedResult<Character>, DataError> = CustomResult.Success(
    samplePaginatedResult)
val errorResult: CustomResult<PaginatedResult<Character>, DataError> = CustomResult.Error(DataError.Network.SERVER_ERROR)

val successCharacterDetailResult: CustomResult<Character, DataError> = CustomResult.Success(
    sampleCharacter)
