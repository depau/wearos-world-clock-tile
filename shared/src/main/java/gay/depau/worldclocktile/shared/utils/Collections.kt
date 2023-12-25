package gay.depau.worldclocktile.shared.utils

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import org.json.JSONArray

operator fun JSONArray.iterator(): Iterator<Any> =
    (0 until this.length()).asSequence().map {
        @Suppress("UNCHECKED_CAST")
        this.get(it)
    }.iterator()

fun <R> JSONArray.map(transform: (Any) -> R): Sequence<R> =
    (0 until this.length()).asSequence().map {
        transform(this.get(it))
    }

inline fun <T> Collection<T>.ifNotEmpty(defaultValue: () -> Unit): Collection<T> {
    if (isNotEmpty())
        defaultValue()
    return this
}