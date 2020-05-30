package me.r4zzz4k.kmidi.utils

fun Int.validate(reason: String) = validate { reason }

fun Int.validate(reason: () -> String) {
    if(this != noErrInt) {
        error("${reason()}: code $this")
    }
}

private val noErrInt = 0
