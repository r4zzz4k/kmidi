package me.r4zzz4k.kmidi.asound

import kotlinx.cinterop.*
import me.r4zzz4k.kmidi.asound.capi.*
import me.r4zzz4k.kmidi.utils.*

class SndSeqClientInfo(internal val ref: CPointer<snd_seq_client_info_t>): Disposable {
    constructor(): this(fetchViaPtr { ptr: CPointer<CPointerVar<snd_seq_client_info_t>> ->
        snd_seq_client_info_malloc(ptr)
    }!!)

    init {
        snd_seq_client_info_set_client(ref, -1)
    }

    override fun dispose() {
        snd_seq_client_info_free(ref)
    }

    fun copy(): SndSeqClientInfo = SndSeqClientInfo().also { snd_seq_client_info_copy(it.ref, this.ref) }

    val client: Int
        get() = snd_seq_client_info_get_client(ref)

    val name: String
        get() = snd_seq_client_info_get_name(ref)!!.toKString()
}
