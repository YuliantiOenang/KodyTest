package com.yulianti.kodytest.util

import com.yulianti.kodytest.R
import com.yulianti.kodytest.data.model.DataError


fun DataError.asUiText(): Int {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT -> 1

        DataError.Network.TOO_MANY_REQUESTS ->  1
        DataError.Network.NO_INTERNET ->  1

        DataError.Network.PAYLOAD_TOO_LARGE ->  1

        DataError.Network.SERVER_ERROR ->  1

        DataError.Network.SERIALIZATION ->  1

        DataError.Network.UNKNOWN ->  1

        DataError.Local.DISK_FULL ->  1

    }
}

//fun Result.Error<*, DataError>.asErrorUiText(): UiText {
//    return error.asUiText()
//}