package com.yulianti.kodytest.util

import java.security.MessageDigest

object MD5Util {
    fun md5(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach { byte ->
            val toAppend = String.format("%02x", byte)
            result.append(toAppend)
        }
        return result.toString()
    }
}
