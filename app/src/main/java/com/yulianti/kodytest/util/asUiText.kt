package com.yulianti.kodytest.util

import com.yulianti.kodytest.R
import com.yulianti.kodytest.data.model.DataError


fun DataError.asUiText(): Int {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT -> 1

        DataError.Network.TOO_MANY_REQUESTS ->  2
        DataError.Network.NO_INTERNET ->  3

        DataError.Network.PAYLOAD_TOO_LARGE ->  4

        DataError.Network.SERVER_ERROR ->  5

        DataError.Network.SERIALIZATION ->  6

        DataError.Network.UNKNOWN ->  7

        DataError.Local.DISK_FULL ->  8

    }
}

//fun Result.Error<*, DataError>.asErrorUiText(): UiText {
//    return error.asUiText()
//}