package gay.depau.worldclocktile.shared.utils

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Context
import android.content.ContextWrapper
import java.io.File

private val sharedCacheDir = File.createTempFile("preview", "tmp").apply { deleteOnExit() }

class PreviewContextWrapper(context: Context) : ContextWrapper(context) {
    override fun getCacheDir(): File {
        return sharedCacheDir
    }
}