package me.r4zzz4k.kmidi.asound

import kotlinx.cinterop.*
import me.r4zzz4k.kmidi.asound.capi.*
import me.r4zzz4k.kmidi.utils.*

class SndSeqPortInfo(internal val ref: CPointer<snd_seq_port_info_t>): Disposable {
    constructor(): this(fetchViaPtr { ptr: CPointer<CPointerVar<snd_seq_port_info_t>> ->
        snd_seq_port_info_malloc(ptr)
    }!!)

    init {
        snd_seq_port_info_set_client(ref, -1)
        snd_seq_port_info_set_port(ref, -1)
    }

    override fun dispose() {
        snd_seq_port_info_free(ref)
    }

    fun copy(): SndSeqPortInfo = SndSeqPortInfo().also { snd_seq_port_info_copy(it.ref, this.ref) }

    var client: Int
        get() = snd_seq_port_info_get_client(ref)
        set(value) { snd_seq_port_info_set_client(ref, value) }

    var port: Int
        get() = snd_seq_port_info_get_port(ref)
        set(value) { snd_seq_port_info_set_port(ref, value) }

    val capability: UInt
        get() = snd_seq_port_info_get_capability(ref)

    val name: String
        get() = snd_seq_port_info_get_name(ref)!!.toKString()
}

val SndSeqPortInfo.portString: String
    get() = "${client.toString().padStart(3)}:${port.toString().padEnd(3)}"
