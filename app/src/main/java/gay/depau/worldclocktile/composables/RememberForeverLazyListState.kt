package gay.depau.worldclocktile.composables

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.wear.compose.foundation.lazy.ScalingLazyListState

// Adapted from https://stackoverflow.com/a/71123229

private val StorageMap = mutableMapOf<String, KeyParams>()

private data class KeyParams(
    val params: Any? = null,
    val index: Int,
    val scrollOffset: Int
)

/**
 * Save scroll state on all time.
 * @param key value for comparing screen
 * @param params arguments for find different between equals screen
 * @param initialCenterItemIndex see [LazyListState.firstVisibleItemIndex]
 * @param initialCenterItemScrollOffset see [LazyListState.firstVisibleItemScrollOffset]
 */
@Composable
fun rememberForeverScalingLazyListState(
    key: String,
    params: Any?,
    initialCenterItemIndex: Int = 1,
    initialCenterItemScrollOffset: Int = 0
): ScalingLazyListState {
    val scrollState = rememberSaveable(saver = ScalingLazyListState.Saver) {
        var savedValue = StorageMap[key]
        if (savedValue?.params != params) {
            savedValue = null
            StorageMap.remove(key)
        }
        val savedIndex = savedValue?.index ?: initialCenterItemIndex
        val savedOffset = savedValue?.scrollOffset ?: initialCenterItemScrollOffset
        ScalingLazyListState(
            savedIndex,
            savedOffset
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            val lastIndex = scrollState.centerItemIndex
            val lastOffset = scrollState.centerItemScrollOffset
            StorageMap[key] = KeyParams(params, lastIndex, lastOffset)
        }
    }
    return scrollState
}