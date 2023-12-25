package gay.depau.worldclocktile.presentation.theme

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material.*
import androidx.wear.compose.material.ToggleChipDefaults.splitToggleChipColors
import gay.depau.worldclocktile.utils.ColorScheme
import gay.depau.worldclocktile.utils.composeColor
import gay.depau.worldclocktile.utils.foreground

@Composable
fun chipGradientColors(
    checked: Boolean, flip: Boolean = false, colorScheme: ColorScheme
): ChipColors {
    val context = LocalContext.current
    val themeColor = remember { colorScheme.getColor(context).composeColor }

    return if (checked && flip) {
        ChipDefaults.gradientBackgroundChipColors(
            startBackgroundColor = themeColor.copy(alpha = 0.5f),
            endBackgroundColor = themeColor,
            contentColor = themeColor.foreground,
        )
    } else if (checked) {
        ChipDefaults.gradientBackgroundChipColors(
            startBackgroundColor = themeColor,
            endBackgroundColor = themeColor.copy(alpha = 0.5f),
            contentColor = themeColor.foreground,
        )
    } else {
        ChipDefaults.chipColors(
            backgroundColor = themeColor.copy(alpha = 0.5f),
            disabledBackgroundColor = themeColor.copy(alpha = 0.5f).copy(alpha = 0.6f),
            contentColor = themeColor.foreground,
            disabledContentColor = themeColor.foreground.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun toggleChipColors(
    colorScheme: ColorScheme
): ToggleChipColors {
    val context = LocalContext.current
    val themeColor by remember { derivedStateOf { colorScheme.getColor(context).composeColor } }

    return ToggleChipDefaults.toggleChipColors(
        checkedStartBackgroundColor = themeColor.copy(alpha = 0.6f),
        checkedEndBackgroundColor = themeColor,
        checkedContentColor = themeColor.foreground,
        checkedSecondaryContentColor = themeColor.foreground,
        checkedToggleControlColor = themeColor.foreground,
        uncheckedStartBackgroundColor = themeColor.copy(alpha = 0.6f),
        uncheckedEndBackgroundColor = themeColor.copy(alpha = 0.6f),
        uncheckedContentColor = themeColor.foreground,
        uncheckedSecondaryContentColor = themeColor.foreground,
    )
}

@Composable
fun themedChipColors(
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    secondaryContentColor: Color? = null,
    iconColor: Color? = null,
    disabledBackgroundColor: Color? = null,
    disabledContentColor: Color? = null,
    disabledSecondaryContentColor: Color? = null,
    disabledIconColor: Color? = null,
    getColorScheme: () -> ColorScheme
): ChipColors {
    val context = LocalContext.current
    val colorScheme = getColorScheme()
    val themeColor = remember { colorScheme.getColor(context).composeColor }

    return ChipDefaults.chipColors(
        backgroundColor = backgroundColor ?: themeColor,
        disabledBackgroundColor = disabledBackgroundColor ?: (backgroundColor ?: themeColor).copy(
            alpha = 0.5f
        ),
        contentColor = contentColor ?: themeColor.foreground,
        disabledContentColor = disabledContentColor ?: (contentColor ?: themeColor.foreground).copy(
            alpha = 0.5f
        ),
        secondaryContentColor = secondaryContentColor ?: themeColor.foreground,
        disabledSecondaryContentColor = disabledSecondaryContentColor ?: (secondaryContentColor
            ?: themeColor.foreground).copy(alpha = 0.5f),
        iconColor = iconColor ?: themeColor.foreground,
        disabledIconColor = disabledIconColor ?: (iconColor ?: themeColor.foreground).copy(
            alpha = 0.5f
        ),
    )
}

@Composable
fun themedSplitChipColors(
    colorScheme: ColorScheme,
    backgroundColor: Color = colorScheme.getColor(LocalContext.current).composeColor,
    contentColor: Color = colorScheme.getColor(LocalContext.current).composeColor.foreground,
    secondaryContentColor: Color = contentColor,
    checkedToggleControlColor: Color = contentColor,
    uncheckedToggleControlColor: Color = contentColor,
    splitBackgroundOverlayColor: Color = Color.Black.copy(alpha = 0.05f),
) = splitToggleChipColors(
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    secondaryContentColor = secondaryContentColor,
    checkedToggleControlColor = checkedToggleControlColor,
    uncheckedToggleControlColor = uncheckedToggleControlColor,
    splitBackgroundOverlayColor = splitBackgroundOverlayColor,
)