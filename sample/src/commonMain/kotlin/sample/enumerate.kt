package me.r4zzz4k.kmidi.sample

import me.r4zzz4k.kmidi.core.*
import me.r4zzz4k.kmidi.utils.*

fun main(args: Array<String>) {
    disposableScope {
        val midi = KMidiClient().bind()
        //val midi = KMidiClient("KMidi Sample").bind()

        val src = pickFromList(midi.sources, "Enter source index") { it.displayName } ?: return
        val sink = pickFromList(midi.sinks, "Enter sink index") { it.displayName } ?: return

        println("Enter transposition (in octaves):")
        print("> [0] ")
        val transposition = readLine()?.toIntOrNull() ?: 0

        src.passThrough(sink, transposition).bind()
        sleep(5U)

        Unit
    }
}

private fun <T> pickFromList(
    list: List<T>,
    prompt: String,
    itemString: (T) -> String
): T? {
    val size = list.size
    println("$prompt:")
    list.forEachIndexed { idx, item ->
        println("\t#${idx + 1}: ${itemString(item)}")
    }
    print("> [1..$size] ")
    val index = readLine()?.toInt() ?: return null
    if(index !in 1..size) return null
    return list[index - 1].also { println("Picked ${itemString(it)}") }
}

private fun String.toYNBoolean(default: Boolean): Boolean? = when {
    length == 0 -> default
    length > 1 -> null
    else -> when(get(0).toLowerCase()) {
        'y' -> true
        'n' -> false
        else -> null
    }
}
