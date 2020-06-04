package me.r4zzz4k.kmidi.utils

import platform.Foundation.NSError
import platform.Foundation.NSOSStatusErrorDomain
import platform.darwin.OSStatus
import platform.darwin.noErr

fun OSStatus.validate(reason: String) = validate { reason }

fun OSStatus.validate(reason: () -> String) {
    if(this != noErrInt) {
        val e = NSError.errorWithDomain(NSOSStatusErrorDomain, toLong(), null)
        error("${reason()}: ${e.description}")
    }
}

private val noErrInt = noErr.toInt()
