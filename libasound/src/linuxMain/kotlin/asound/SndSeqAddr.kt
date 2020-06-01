package me.r4zzz4k.kmidi.asound

import kotlinx.cinterop.*
import me.r4zzz4k.kmidi.asound.capi.*
import me.r4zzz4k.kmidi.utils.*

class SndSeqAddr(internal val ref: snd_seq_addr_t): Disposable {
    constructor(): this(nativeHeap.alloc<snd_seq_addr_t>())

    override fun dispose() {
        nativeHeap.free(ref)
    }

    var client: Int
        get() = ref.client.convert()
        set(value) { ref.client = value.convert() }

    var port: Int
        get() = ref.port.convert()
        set(value) { ref.port = value.convert() }
}

fun SndSeqAddr.setFrom(portInfo: SndSeqPortInfo) {
    ref.client = portInfo.client.convert()
    ref.port = portInfo.port.convert()
}
