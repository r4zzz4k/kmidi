package me.r4zzz4k.kmidi.utils

import kotlin.io.readLine as stdLibReadLine
import platform.posix.sleep as posixSleep

actual fun readLine(): String? = stdLibReadLine()
actual fun sleep(seconds: UInt) { posixSleep(seconds) }
