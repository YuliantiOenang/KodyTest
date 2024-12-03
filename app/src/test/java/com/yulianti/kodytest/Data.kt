package com.yulianti.kodytest

import com.yulianti.kodytest.data.model.Character
import com.yulianti.kodytest.data.model.CustomResult
import com.yulianti.kodytest.data.model.DataError
import com.yulianti.kodytest.data.model.PaginatedResult

// Sample character data
val sampleCharacter = Character(
    id = 1,
    name = "Test Character 1",
    coverUrl = "",
    description = ""
)

val sampleCharacter2 = Character(
    id = 2,
    name = "Test Character 2",
    coverUrl = "",
    description = ""
)

// Sample paginated result
val samplePaginatedResult = PaginatedResult(
    items = listOf(sampleCharacter),
    totalSize = 2,
    offset = 10,
    count = 0
)

val sampleLoadMoreResult = PaginatedResult(
    items = listOf(sampleCharacter2),
    totalSize = 2,
    offset = 10,
    count = 0
)

// CustomResult success and error instances
val successCharacterListResult: CustomResult<PaginatedResult<Character>, DataError> = CustomResult.Success(
    samplePaginatedResult
)

val successLoadMoreResult: CustomResult<PaginatedResult<Character>, DataError> = CustomResult.Success(
    sampleLoadMoreResult
)
val errorResult: CustomResult<PaginatedResult<Character>, DataError> = CustomResult.Error(DataError.Network.SERVER_ERROR)

val successCharacterDetailResult: CustomResult<Character, DataError> = CustomResult.Success(
    sampleCharacter
)
val errorCharacterDetail: CustomResult<Character, DataError> = CustomResult.Error(DataError.Network.SERVER_ERROR)
