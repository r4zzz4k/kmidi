package me.r4zzz4k.kmidi.core

import me.r4zzz4k.kmidi.utils.Disposable

expect class KMidiClient : Disposable
expect class KMidiEndpoint : Disposable {
    val displayName: String
}
expect class KMidiConnection : Disposable

expect val KMidiClient.sources: List<KMidiEndpoint>
expect val KMidiClient.sinks: List<KMidiEndpoint>

expect fun KMidiClient.createSource(name: String): KMidiEndpoint
expect fun KMidiClient.createSink(name: String, readChannel: Any): KMidiEndpoint

expect fun KMidiEndpoint.passThrough(sink: KMidiEndpoint, transpose: Int = 0): KMidiConnection
