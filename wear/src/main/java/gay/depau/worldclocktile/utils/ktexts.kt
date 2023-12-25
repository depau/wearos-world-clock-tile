package gay.depau.worldclocktile.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.core.graphics.ColorUtils
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.protolayout.ColorBuilders
import gay.depau.worldclocktile.shared.utils.ColorScheme
import gay.depau.worldclocktile.shared.utils.androidColor
import gay.depau.worldclocktile.shared.utils.composeColor
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor


fun ColorScheme.getColorProp(context: Context) = ColorBuilders.ColorProp.Builder(getColor(context).toArgb()).build()

val AndroidColor.foreground: AndroidColor
    @Composable
    get() {
        val contrastLight = ColorUtils.calculateContrast(
            MaterialTheme.colors.onSurface.androidColor.toArgb(),
            toArgb()
        )
        return if (contrastLight <= 1.6) {
            MaterialTheme.colors.onPrimary.androidColor
        } else {
            MaterialTheme.colors.onSurface.androidColor
        }
    }

val ComposeColor.foreground: ComposeColor
    @Composable
    get() = androidColor.foreground.composeColor