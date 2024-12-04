package com.yulianti.kodytest.util

import com.yulianti.kodytest.R
import com.yulianti.kodytest.data.model.DataError


fun DataError.asUiText(): Int {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT -> R.string.unknown

        DataError.Network.TOO_MANY_REQUESTS ->  R.string.unknown
        DataError.Network.NO_INTERNET ->  R.string.no_internet_connection

        DataError.Network.PAYLOAD_TOO_LARGE ->  R.string.unknown

        DataError.Network.SERVER_ERROR ->  R.string.unknown

        DataError.Network.SERIALIZATION ->  R.string.unknown

        DataError.Network.UNKNOWN, DataError.Local.UNKNOWN ->  R.string.unknown

        DataError.Local.DISK_FULL ->  R.string.disk_full

    }
}