package me.r4zzz4k.kmidi.core

import kotlinx.cinterop.*
import me.r4zzz4k.kmidi.asound.*
import me.r4zzz4k.kmidi.utils.*

actual class KMidiClient(internal val seq: SndSeq): Disposable {
    actual constructor(name: String?): this(name, "default")

    constructor(clientName: String?, sequencerName: String):
        this(SndSeq(sequencerName, SndSeq.Open.DUPLEX, false).also {
            if(clientName != null) {
                it.clientName(clientName)
            }
        })

    override fun dispose() {
        seq.dispose()
    }
}

private fun KMidiClient.ports(caps: Int): List<AsoundMidiPort> = buildList {
    seq.forEachPort { clientInfo, portInfo ->
        val cap = caps.toUInt()
        if (portInfo.capability and cap == cap) {
            add(AsoundMidiPort(this@ports, clientInfo.copy(), portInfo.copy()))
        }
    }
}

class AsoundMidiPort(
    val owner: KMidiClient,
    val client: SndSeqClientInfo,
    val port: SndSeqPortInfo,
    private val owned: Boolean = true
): KMidiSourcePort, KMidiSinkPort {
    override val displayName: String
        get() = "${port.portString} | ${client.name} | ${port.name}"

    override fun dispose() {
        if(owned) {
            client.dispose()
            port.dispose()
        }
    }
}

actual class KMidiConnection(
    internal val owner: KMidiClient,
    internal val ref: SndSeqPortSubscribe,
    private val owned: Boolean = true
): Disposable {
    init {
        owner.seq.subscribePort(ref)
    }

    override fun dispose() {
        owner.seq.unsubscribePort(ref)
        if(owned) {
            ref.dispose()
        }
    }
}

actual val KMidiClient.sources: List<KMidiSourcePort>
    get() = ports(SndSeq.PortCap.SUBS_READ.value or SndSeq.PortCap.READ.value)

actual val KMidiClient.sinks: List<KMidiSinkPort>
    get() = ports(SndSeq.PortCap.SUBS_WRITE.value or SndSeq.PortCap.WRITE.value)

actual fun KMidiClient.createSource(name: String): KMidiSourcePort = TODO()

actual fun KMidiClient.createSink(name: String, readChannel: Any): KMidiSinkPort {
    val port = seq.createSimplePort(
        name,
        SndSeq.PortCap.WRITE.value or SndSeq.PortCap.SUBS_WRITE.value,
        SndSeq.PortType.MIDI_GENERIC.value
    )
    TODO()
}

actual fun KMidiSourcePort.passThrough(sink: KMidiSinkPort, transpose: Int): KMidiConnection {
    val src = this as AsoundMidiPort
    sink as AsoundMidiPort
    val client = src.owner
    println("TODO: connect $displayName to ${sink.displayName}")

    val subsciption = SndSeqPortSubscribe().apply {
        sender { setFrom(src.port) }
        dest { setFrom(sink.port) }
    }

    return KMidiConnection(client, subsciption)
}
