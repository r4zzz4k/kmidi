package me.r4zzz4k.kmidi.core

import me.r4zzz4k.kmidi.utils.Disposable

expect class KMidiClient(name: String? = null) : Disposable

interface KMidiPort : Disposable {
    val displayName: String
}
interface KMidiSourcePort : KMidiPort
interface KMidiSinkPort : KMidiPort

expect class KMidiConnection : Disposable

expect val KMidiClient.sources: List<KMidiSourcePort>
expect val KMidiClient.sinks: List<KMidiSinkPort>

expect fun KMidiClient.createSource(name: String): KMidiSourcePort
expect fun KMidiClient.createSink(name: String, readChannel: Any): KMidiSinkPort

expect fun KMidiSourcePort.passThrough(sink: KMidiSinkPort, transpose: Int = 0): KMidiConnection
