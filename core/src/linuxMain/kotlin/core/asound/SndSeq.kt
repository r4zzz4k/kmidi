package me.r4zzz4k.kmidi.core.asound

import asound.*
import kotlinx.cinterop.*
import me.r4zzz4k.kmidi.utils.*

class SndSeq(internal val ref: CPointer<snd_seq_t>): Disposable {
    constructor(
        name: String?,
        streams: SndSeq.Open,
        nonBlock: Boolean
    ): this(fetchViaPtr { ptr: CPointer<CPointerVar<snd_seq_t>> ->
        val seqName = name ?: "default"
        snd_seq_open(ptr, seqName, streams.value, if(nonBlock) SND_SEQ_NONBLOCK else 0)
            .validate("Unable to open sequencer \"$seqName\"")
    }!!)

    override fun dispose() {
        snd_seq_close(ref)
    }

    fun queryNextClient(clientInfo: SndSeqClientInfo): Int {
        return snd_seq_query_next_client(ref, clientInfo.ref)
    }

    fun queryNextPort(portInfo: SndSeqPortInfo): Int {
        return snd_seq_query_next_port(ref, portInfo.ref)
    }

    fun forEachClient(action: (SndSeqClientInfo) -> Unit) = disposableScope {
        SndSeqClientInfo().use { clientInfo ->
            while (queryNextClient(clientInfo) >= 0) {
                action(clientInfo)
            }
        }
        Unit
    }

    fun forEachPort(action: (SndSeqClientInfo, SndSeqPortInfo) -> Unit) = disposableScope {
        forEachClient { clientInfo ->
            SndSeqPortInfo().use { portInfo ->
                portInfo.client = clientInfo.client
                while (queryNextPort(portInfo) >= 0) {
                    action(clientInfo, portInfo)
                }
            }
            Unit
        }
    }

    enum class Open(val value: Int) {
        OUTPUT(SND_SEQ_OPEN_OUTPUT),
        INPUT(SND_SEQ_OPEN_INPUT),
        DUPLEX(SND_SEQ_OPEN_DUPLEX)
    }
}

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
