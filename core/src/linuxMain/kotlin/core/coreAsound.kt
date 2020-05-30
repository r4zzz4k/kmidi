package me.r4zzz4k.kmidi.core

import asound.*
import kotlinx.cinterop.*
import me.r4zzz4k.kmidi.utils.*

actual class KMidiClient(internal val ref: CPointer<snd_seq_t>): Disposable {
    actual constructor(name: String?): this(fetchViaPtr { ptr: CPointer<CPointerVar<snd_seq_t>> ->
        val seqName = name ?: "default"
        snd_seq_open(ptr, seqName, SND_SEQ_OPEN_DUPLEX, 0)
            .validate("Unable to open sequencer \"$seqName\"")
    }!!)

    internal val clientInfo: CPointer<snd_seq_client_info_t>

    init {
        //val clientInfo = zeroValue<snd_seq_client_info_t>(snd_seq_client_info_sizeof().convert(), 0)
        clientInfo = fetchViaPtr { ptr: CPointer<CPointerVar<snd_seq_client_info_t>> ->
            snd_seq_client_info_malloc(ptr)
        }!!
    }

    override fun dispose() {
        snd_seq_client_info_free(clientInfo)
        snd_seq_close(ref)
    }
}

private fun KMidiClient.visitClientsAndPorts(caps: Int, action: (CPointer<snd_seq_port_info_t>) -> Unit) = memScoped {
    snd_seq_client_info_set_client(clientInfo, -1)
    while (snd_seq_query_next_client(ref, clientInfo) >= 0) {
        val client = snd_seq_client_info_get_client(clientInfo)

        val portInfo = fetchViaPtr { ptr: CPointer<CPointerVar<snd_seq_port_info_t>> ->
            snd_seq_port_info_malloc(ptr)
        }!!

        snd_seq_port_info_set_client(portInfo, client)
        snd_seq_port_info_set_port(portInfo, -1)
        while (snd_seq_query_next_port(ref, portInfo) >= 0) {
            val cap = caps.toUInt()
            if ((snd_seq_port_info_get_capability(portInfo) and cap) == cap) {
                action(portInfo)
            }
        }

        snd_seq_port_info_free(portInfo)
    }
}

private fun displayName(
    clientInfo: CPointer<snd_seq_client_info_t>,
    portInfo: CPointer<snd_seq_port_info_t>
): String = buildString {
    append(snd_seq_port_info_get_client(portInfo).toString().padStart(3))
    append(':')
    append(snd_seq_port_info_get_port(portInfo).toString().padEnd(3))
    append(" / ")
    append(snd_seq_client_info_get_name(clientInfo)!!.toKString())
    append(" / ")
    append(snd_seq_port_info_get_name(portInfo)!!.toKString())
}

actual class KMidiEndpoint(actual val displayName: String, private val owned: Boolean = true): Disposable {
    /*actual val displayName: String
        get() = "default name"*/

    override fun dispose() {
        if(owned) {
        }
    }
}

actual class KMidiConnection(private val owned: Boolean = true): Disposable {
    override fun dispose() {
        if(owned) {
        }
    }
}

actual val KMidiClient.sources: List<KMidiEndpoint>
    get() = buildList {
        visitClientsAndPorts(SND_SEQ_PORT_CAP_SUBS_READ or SND_SEQ_PORT_CAP_READ) { portInfo ->
            add(KMidiEndpoint(displayName(clientInfo, portInfo)))
        }
    }

actual val KMidiClient.sinks: List<KMidiEndpoint>
    get() = buildList {
        visitClientsAndPorts(SND_SEQ_PORT_CAP_SUBS_WRITE or SND_SEQ_PORT_CAP_WRITE) { portInfo ->
            add(KMidiEndpoint(displayName(clientInfo, portInfo)))
        }
    }

actual fun KMidiClient.createSource(name: String): KMidiEndpoint = TODO()

actual fun KMidiClient.createSink(name: String, readChannel: Any): KMidiEndpoint = TODO()

actual fun KMidiEndpoint.passThrough(sink: KMidiEndpoint, transpose: Int): KMidiConnection {
    println("TOOD: connect $displayName to ${sink.displayName}")
    return KMidiConnection()
}
