package gay.depau.worldclocktile.shared.utils

import android.content.Context
import android.content.ContextWrapper
import java.io.File

private val sharedCacheDir = File.createTempFile("preview", "tmp").apply { deleteOnExit() }

class PreviewContextWrapper(context: Context) : ContextWrapper(context) {
    override fun getCacheDir(): File {
        return sharedCacheDir
    }
}