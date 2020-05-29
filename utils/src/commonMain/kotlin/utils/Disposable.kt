package me.r4zzz4k.kmidi.utils

interface Disposable {
    fun dispose()
}

inline fun <T, D: Disposable> D.use(block: (D) -> T) =
    try {
        block(this)
    } finally {
        dispose()
    }

class DisposableScope @PublishedApi internal constructor() : Disposable {
    private val disposables: MutableSet<Disposable> = mutableSetOf()

    fun <T: Disposable> T.bind(): T = apply {
        disposables += this
    }

    override fun dispose() {
        disposables.forEach { it.dispose() }
    }
}

inline fun <T> disposableScope(action: DisposableScope.() -> T): T =
    DisposableScope().use { it.action() }
