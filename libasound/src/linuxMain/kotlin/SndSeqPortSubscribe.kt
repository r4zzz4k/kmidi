package me.r4zzz4k.kmidi.asound

import kotlinx.cinterop.*
import me.r4zzz4k.kmidi.asound.capi.*
import me.r4zzz4k.kmidi.utils.*

class SndSeqPortSubscribe(internal val ref: CPointer<snd_seq_port_subscribe_t>): Disposable {
    constructor(): this(fetchViaPtr { ptr: CPointer<CPointerVar<snd_seq_port_subscribe_t>> ->
        snd_seq_port_subscribe_malloc(ptr)
    }!!)

    fun sender(sender: SndSeqAddr.() -> Unit) {
        SndSeqAddr().use {
            it.apply(sender)
            snd_seq_port_subscribe_set_sender(ref, it.ref.ptr)
        }
    }

    fun dest(dest: SndSeqAddr.() -> Unit) {
        SndSeqAddr().use {
            it.apply(dest)
            snd_seq_port_subscribe_set_dest(ref, it.ref.ptr)
        }
    }

    var exclusive: Boolean
        get() = snd_seq_port_subscribe_get_exclusive(ref) != 0
        set(value) = snd_seq_port_subscribe_set_exclusive(ref, if(value) 1 else 0)

    override fun dispose() {
        snd_seq_port_subscribe_free(ref)
    }

    fun copy(): SndSeqPortSubscribe = SndSeqPortSubscribe().also { snd_seq_port_subscribe_copy(it.ref, this.ref) }
}
