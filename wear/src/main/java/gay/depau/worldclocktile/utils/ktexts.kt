package gay.depau.worldclocktile.utils

import android.content.Context
import androidx.wear.protolayout.ColorBuilders
import gay.depau.worldclocktile.shared.utils.ColorScheme


fun ColorScheme.getColorProp(context: Context) = ColorBuilders.ColorProp.Builder(getColor(context).toArgb()).build()

