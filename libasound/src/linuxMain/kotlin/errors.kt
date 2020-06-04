package me.r4zzz4k.kmidi.asound

import kotlinx.cinterop.toKString
import me.r4zzz4k.kmidi.asound.capi.snd_strerror

val Int.errorStr: String
    get() = "[${-this}] ${snd_strerror(this)!!.toKString()}"

inline fun Int.validate(reason: String) = validate { reason }

fun Int.validate(reason: () -> String) {
    if(this != 0) {
        error("${reason()}: $errorStr")
    }
}

inline fun Int.validateNeg(reason: String) = validateNeg { reason }

fun Int.validateNeg(reason: () -> String) {
    if(this < 0) {
        error("${reason()}: $errorStr")
    }
}
