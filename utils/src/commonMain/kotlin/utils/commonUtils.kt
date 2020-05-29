package me.r4zzz4k.kmidi.utils

fun Int.toHexString(): String = "0x" + toString(16).padStart(8, '0')
fun UInt.toHexString(): String = "0x" + toString(16).padStart(8, '0')
fun Long.toHexString(): String = "0x" + toString(16).padStart(16, '0')
fun ULong.toHexString(): String = "0x" + toString(16).padStart(16, '0')
