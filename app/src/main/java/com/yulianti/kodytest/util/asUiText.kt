package com.yulianti.kodytest.util

import com.yulianti.kodytest.R
import com.yulianti.kodytest.data.model.DataError


fun DataError.asUiText(): Int {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT -> 1

        DataError.Network.TOO_MANY_REQUESTS ->  2
        DataError.Network.NO_INTERNET ->  R.string.no_internet_connection

        DataError.Network.PAYLOAD_TOO_LARGE ->  4

        DataError.Network.SERVER_ERROR ->  R.string.unknown

        DataError.Network.SERIALIZATION ->  6

        DataError.Network.UNKNOWN, DataError.Local.UNKNOWN ->  R.string.unknown

        DataError.Local.DISK_FULL ->  8

    }
}