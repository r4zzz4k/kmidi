package me.r4zzz4k.kmidi.asound

import kotlinx.cinterop.*
import me.r4zzz4k.kmidi.asound.capi.*
import me.r4zzz4k.kmidi.utils.*

class SndSeq(internal val ref: CPointer<snd_seq_t>): Disposable {
    constructor(
        name: String?,
        streams: Open,
        nonBlock: Boolean
    ): this(fetchViaPtr { ptr: CPointer<CPointerVar<snd_seq_t>> ->
        val sequencerName = name ?: "default"
        snd_seq_open(ptr, sequencerName, streams.value, if(nonBlock) SND_SEQ_NONBLOCK else 0)
            .validate("Unable to open sequencer \"$sequencerName\"")
    }!!)

    override fun dispose() {
        snd_seq_close(ref)
    }

    fun clientName(value: String) {
        snd_seq_set_client_name(ref, value)
    }

    fun createSimplePort(name: String, caps: Int, type: Int) {
        snd_seq_create_simple_port(ref, name, caps.toUInt(), type.toUInt())
            .validateNeg { "Unable to create port \"$name\"" }
    }

    fun deleteSimplePort(name: String, port: Int) {
        snd_seq_delete_simple_port(ref, port)
            .validate { "Unable to delete port \"$name\"" }
    }

    fun subscribePort(subscription: SndSeqPortSubscribe) {
        snd_seq_subscribe_port(ref, subscription.ref)
            .validate { "Unable to subscribe port" }
    }

    fun unsubscribePort(subscription: SndSeqPortSubscribe) {
        snd_seq_unsubscribe_port(ref, subscription.ref)
            .validate { "Unable to unsubscribe port" }
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

    enum class PortCap(val value: Int) {
        READ(SND_SEQ_PORT_CAP_READ),
        WRITE(SND_SEQ_PORT_CAP_WRITE),
        SYNC_READ(SND_SEQ_PORT_CAP_SYNC_READ),
        SYNC_WRITE(SND_SEQ_PORT_CAP_SYNC_WRITE),
        DUPLEX(SND_SEQ_PORT_CAP_DUPLEX),
        SUBS_READ(SND_SEQ_PORT_CAP_SUBS_READ),
        SUBS_WRITE(SND_SEQ_PORT_CAP_SUBS_WRITE),
        NO_EXPORT(SND_SEQ_PORT_CAP_NO_EXPORT)
    }

    enum class PortType(val value: Int) {
        SPECIFIC(SND_SEQ_PORT_TYPE_SPECIFIC),
        MIDI_GENERIC(SND_SEQ_PORT_TYPE_MIDI_GENERIC),
        MIDI_GM(SND_SEQ_PORT_TYPE_MIDI_GM),
        MIDI_GM2(SND_SEQ_PORT_TYPE_MIDI_GM2),
        MIDI_GS(SND_SEQ_PORT_TYPE_MIDI_GS),
        MIDI_XG(SND_SEQ_PORT_TYPE_MIDI_XG),
        MIDI_MT32(SND_SEQ_PORT_TYPE_MIDI_MT32),
        HARDWARE(SND_SEQ_PORT_TYPE_HARDWARE),
        SOFTWARE(SND_SEQ_PORT_TYPE_SOFTWARE),
        SYNTHESIZER(SND_SEQ_PORT_TYPE_SYNTHESIZER),
        PORT(SND_SEQ_PORT_TYPE_PORT),
        APPLICATION(SND_SEQ_PORT_TYPE_APPLICATION)
    }
}

fun SndSeq.subscribePort(subscription: SndSeqPortSubscribe.() -> Unit) =
    SndSeqPortSubscribe().use {
        subscribePort(it.apply(subscription))
    }
