package me.r4zzz4k.kmidi.core

import kotlinx.cinterop.*
import me.r4zzz4k.kmidi.utils.*
import platform.CoreFoundation.CFStringRefVar
import platform.CoreMIDI.*
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes

actual class KMidiClient(internal val ref: MIDIClientRef): Disposable {
    constructor(name: String?): this(fetchViaPtr { ptr: CPointer<MIDIClientRefVar> ->
        MIDIClientCreate(name?.toCFStringRef(), null, null, ptr)
    })

    override fun dispose() {
        MIDIClientDispose(ref)
    }
}

actual class KMidiEndpoint(internal val ref: MIDIEndpointRef, private val owned: Boolean = true): Disposable {
    actual val displayName: String by lazy {
        memScoped {
            fetchViaPtr { ptr: CPointer<CFStringRefVar> ->
                MIDIObjectGetStringProperty(ref, kMIDIPropertyDisplayName, ptr)
                    .validate("Unable to get display name of MIDI device")
            }!!.toKString()
        }
    }

    override fun dispose() {
        if(owned) {
            MIDIClientDispose(ref)
        }
    }
}

actual class KMidiConnection(internal val ref: MIDIThruConnectionRef, private val owned: Boolean = true): Disposable {
    override fun dispose() {
        if(owned) {
            MIDIThruConnectionDispose(ref)
        }
    }
}

actual val KMidiClient.sources: List<KMidiEndpoint>
    get() = (0UL..MIDIGetNumberOfSources())
        .mapNotNull { MIDIGetSource(it).takeIf { it != 0U } }
        .map { KMidiEndpoint(it, false) }

actual val KMidiClient.sinks: List<KMidiEndpoint>
    get() = (0UL..MIDIGetNumberOfSources())
        .mapNotNull { MIDIGetDestination(it).takeIf { it != 0U } }
        .map { KMidiEndpoint(it, false) }

actual fun KMidiClient.createSource(name: String): KMidiEndpoint = KMidiEndpoint(
    fetchViaPtr { ptr: CPointer<MIDIEndpointRefVar> ->
        MIDISourceCreate(ref, name.toCFStringRef(), ptr)
    }
)

actual fun KMidiClient.createSink(name: String, readChannel: Any): KMidiEndpoint = KMidiEndpoint(
    fetchViaPtr { ptr: CPointer<MIDIEndpointRefVar> ->
        val stableReadChannel = StableRef.create(readChannel)
        MIDIDestinationCreate(
            ref,
            name.toCFStringRef(),
            staticCFunction { pktlist: CPointer<MIDIPacketList>?, readProcRefCon, srcConnRefCon ->
                initRuntimeIfNeeded()
                // TODO invoked from the separate thread
                val aReadChannel = readProcRefCon!!.asStableRef<Any>()
                // TODO
            },
            stableReadChannel.asCPointer(),
            ptr
        )
    }
)

actual fun KMidiEndpoint.passThrough(sink: KMidiEndpoint, transpose: Int): KMidiConnection = memScoped {
    val src = this@passThrough
    val params = cValue<MIDIThruConnectionParams> {
        MIDIThruConnectionParamsInitialize(ptr)

        numSources = 1U
        with(sources[0]) {
            endpointRef = src.ref
            uniqueID = 1
        }

        numDestinations = 1U
        with(destinations[0]) {
            endpointRef = sink.ref
            uniqueID = 2
        }

        filterOutSysEx = 1U
        filterOutMTC = 1U
        filterOutBeatClock = 1U
        filterOutTuneRequest = 1U

        with(noteNumber) {
            transform = kMIDITransform_Add
            param = (transpose * 12).convert()
        }
    }

    val midiThru = params.toCFDataRef().use { cfData ->
        fetchViaPtr { ptr: CPointer<MIDIThruConnectionRefVar> ->
            MIDIThruConnectionCreate(
                "me.r4zzz4k.kmidi.Sample".toCFStringRef(),
                cfData,
                ptr
            ).validate("Unable to get display name of MIDI device")
        }
    }

    return KMidiConnection(midiThru)
}
