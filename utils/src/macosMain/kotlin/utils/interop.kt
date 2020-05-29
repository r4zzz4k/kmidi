@file:Suppress("FINAL_UPPER_BOUND")
@file:OptIn(ExperimentalUnsignedTypes::class)

package me.r4zzz4k.kmidi.utils

import kotlinx.cinterop.*

inline fun <reified T: CVariable> fetchViaPtrVar(block: (CPointer<T>) -> Unit): T = memScoped {
    val result = alloc<T>()
    block(result.ptr)
    return result
}

inline fun <T : Boolean> fetchViaPtr(block: (CPointer<BooleanVarOf<T>>) -> Unit): T = fetchViaPtrVar(block).value
inline fun <T : Byte> fetchViaPtr(block: (CPointer<ByteVarOf<T>>) -> Unit): T = fetchViaPtrVar(block).value
inline fun <T : Short> fetchViaPtr(block: (CPointer<ShortVarOf<T>>) -> Unit): T = fetchViaPtrVar(block).value
inline fun <T : Int> fetchViaPtr(block: (CPointer<IntVarOf<T>>) -> Unit): T = fetchViaPtrVar(block).value
inline fun <T : Long> fetchViaPtr(block: (CPointer<LongVarOf<T>>) -> Unit): T = fetchViaPtrVar(block).value
inline fun <T : UByte> fetchViaPtr(block: (CPointer<UByteVarOf<T>>) -> Unit): T = fetchViaPtrVar(block).value
inline fun <T : UShort> fetchViaPtr(block: (CPointer<UShortVarOf<T>>) -> Unit): T = fetchViaPtrVar(block).value
inline fun <T : UInt> fetchViaPtr(block: (CPointer<UIntVarOf<T>>) -> Unit): T = fetchViaPtrVar(block).value
inline fun <T : ULong> fetchViaPtr(block: (CPointer<ULongVarOf<T>>) -> Unit): T = fetchViaPtrVar(block).value
inline fun <T : Float> fetchViaPtr(block: (CPointer<FloatVarOf<T>>) -> Unit): T = fetchViaPtrVar(block).value
inline fun <T : Double> fetchViaPtr(block: (CPointer<DoubleVarOf<T>>) -> Unit): T = fetchViaPtrVar(block).value
inline fun <T : CPointer<*>> fetchViaPtr(block: (CPointer<CPointerVarOf<T>>) -> Unit): T? = fetchViaPtrVar(block).value
