package me.r4zzz4k.kmidi.utils

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.darwin.NSObject

@Suppress("CAST_NEVER_SUCCEEDS")
fun String.asNSString(): NSString = this as NSString
@Suppress("CAST_NEVER_SUCCEEDS")
fun NSString.asString(): String = this as String

fun CFStringRef.toKString(): String = toNSObject<NSString>().asString()
fun CFStringRef.toKStringRetaining(): String = toKString().also { platform.CoreFoundation.CFRetain(this) }

fun String.toCFStringRef(): CFStringRef = asNSString().toCFTypeRef()
fun String.toCFMutableStringRef(maxLength: Long = 0L): CFMutableStringRef = toCFStringRef()
    .use { CFStringCreateMutableCopy(null, maxLength, it)!! }

fun <T: CFTypeRef?, R> T.use(block: (T) -> R): R = block(this).also { this?.let(::CFRelease) }

private inline fun <reified T: CFTypeRef> NSObject.toCFTypeRef(): T = CFBridgingRetain(this) as T
private inline fun <reified T: NSObject> CFTypeRef.toNSObject(): T = platform.Foundation.CFBridgingRelease(this) as T

fun <T : CVariable> CValue<T>.toNSData(): NSData = memScoped {
    NSData.dataWithBytes(ptr, size.convert())
}

fun <T : CVariable> CValue<T>.toCFDataRef(): CFDataRef = memScoped {
    CFDataCreate(null, ptr.reinterpret(), size.convert())!!
    //NSData.dataWithBytes(ptr, size.convert())
}
