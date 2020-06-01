package me.r4zzz4k.kmidi.core

import asound.*
import kotlinx.cinterop.*
import me.r4zzz4k.kmidi.core.asound.*
import me.r4zzz4k.kmidi.utils.*

actual class KMidiClient(internal val seq: SndSeq): Disposable {
    actual constructor(name: String?): this(SndSeq(name, SndSeq.Open.DUPLEX, false))
    override fun dispose() {
        seq.dispose()
    }
}

private fun KMidiClient.ports(caps: Int): List<AsoundMidiPort> = buildList {
    seq.forEachPort { clientInfo, portInfo ->
        val cap = caps.toUInt()
        if (portInfo.capability and cap == cap) {
            add(AsoundMidiPort(clientInfo.copy(), portInfo.copy()))
        }
    }
}

class AsoundMidiPort(
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

actual class KMidiConnection(private val owned: Boolean = true): Disposable {
    override fun dispose() {
        if(owned) {
        }
    }
}

actual val KMidiClient.sources: List<KMidiSourcePort>
    get() = ports(SND_SEQ_PORT_CAP_SUBS_READ or SND_SEQ_PORT_CAP_READ)

actual val KMidiClient.sinks: List<KMidiSinkPort>
    get() = ports(SND_SEQ_PORT_CAP_SUBS_WRITE or SND_SEQ_PORT_CAP_WRITE)

actual fun KMidiClient.createSource(name: String): KMidiSourcePort = TODO()

actual fun KMidiClient.createSink(name: String, readChannel: Any): KMidiSinkPort = TODO()

actual fun KMidiSourcePort.passThrough(sink: KMidiSinkPort, transpose: Int): KMidiConnection {
    println("TOOD: connect $displayName to ${sink.displayName}")
    return KMidiConnection()
}
