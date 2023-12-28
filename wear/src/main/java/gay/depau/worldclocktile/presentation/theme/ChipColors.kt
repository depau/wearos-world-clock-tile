package gay.depau.worldclocktile.presentation.theme

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.SplitToggleChipColors
import androidx.wear.compose.material.ToggleChipColors
import androidx.wear.compose.material.ToggleChipDefaults
import androidx.wear.compose.material.ToggleChipDefaults.splitToggleChipColors
import gay.depau.worldclocktile.shared.utils.ColorScheme
import gay.depau.worldclocktile.shared.utils.composeColor
import gay.depau.worldclocktile.shared.utils.contentColorFor
import gay.depau.worldclocktile.shared.utils.dynamicDarkColorPalette

@Composable
fun chipGradientColors(
    checked: Boolean, flip: Boolean = false, colorScheme: ColorScheme,
): ChipColors {
    val context = LocalContext.current
    val themeColor = remember { colorScheme.getColor(context).composeColor }
    val palette = dynamicDarkColorPalette(themeColor)

    return if (checked) {
        val bg1 = palette.primary.copy(alpha = 1f).compositeOver(palette.surface)
        val bg2 = palette.primary.copy(alpha = 0.4f).compositeOver(palette.surface)

        if (flip) {
            ChipDefaults.gradientBackgroundChipColors(
                startBackgroundColor = bg1,
                endBackgroundColor = bg2,
                contentColor = palette.contentColorFor(bg2),
            )
        } else {
            ChipDefaults.gradientBackgroundChipColors(
                startBackgroundColor = bg2,
                endBackgroundColor = bg1,
                contentColor = palette.contentColorFor(bg1),
            )
        }
    } else {
        val contentColor = palette.contentColorFor(palette.primary)

        return ChipDefaults.chipColors(
            backgroundColor = palette.primary,
            contentColor = contentColor,
            secondaryContentColor = contentColor,
            iconColor = contentColor,
            disabledBackgroundColor = palette.primary.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f),
            disabledSecondaryContentColor = contentColor.copy(alpha = 0.5f),
            disabledIconColor = contentColor.copy(alpha = 0.5f),
        )
    }
}

@Composable
fun toggleChipColors(
    colorScheme: ColorScheme,
): ToggleChipColors {
    val context = LocalContext.current
    val themeColor by remember { derivedStateOf { colorScheme.getColor(context).composeColor } }
    val palette = dynamicDarkColorPalette(themeColor)

    val checkedStartBackgroundColor = palette.surface.copy(alpha = 0f).compositeOver(palette.surface)
    val checkedEndBackgroundColor = palette.primary.copy(alpha = 0.5f).compositeOver(palette.surface)
    val checkedContentColor = palette.onSurface
    val checkedSecondaryContentColor = palette.onSurfaceVariant
    val checkedToggleControlColor = palette.secondary
    val uncheckedStartBackgroundColor = palette.surface
    val uncheckedContentColor = palette.contentColorFor(uncheckedStartBackgroundColor)

    return ToggleChipDefaults.toggleChipColors(
        checkedStartBackgroundColor = checkedStartBackgroundColor,
        checkedEndBackgroundColor = checkedEndBackgroundColor,
        checkedContentColor = checkedContentColor,
        checkedSecondaryContentColor = checkedSecondaryContentColor,
        checkedToggleControlColor = checkedToggleControlColor,
        uncheckedStartBackgroundColor = uncheckedStartBackgroundColor,
        uncheckedEndBackgroundColor = uncheckedStartBackgroundColor,
        uncheckedContentColor = uncheckedContentColor,
        uncheckedSecondaryContentColor = uncheckedContentColor,
        uncheckedToggleControlColor = uncheckedContentColor,
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
    getColorScheme: () -> ColorScheme,
): ChipColors {
    val context = LocalContext.current
    val colorScheme = getColorScheme()
    val themeColor = remember { colorScheme.getColor(context).composeColor }
    val palette = dynamicDarkColorPalette(themeColor)

    val chipBackgroundColor = backgroundColor ?: palette.primary
    val chipContentColor = contentColor ?: palette.onPrimary
    val chipSecondaryContentColor = secondaryContentColor ?: chipContentColor
    val chipIconColor = iconColor ?: chipContentColor
    val chipDisabledBackgroundColor = disabledBackgroundColor ?: chipBackgroundColor.copy(alpha = 0.5f)
    val chipDisabledContentColor = disabledContentColor ?: chipContentColor.copy(alpha = 0.5f)
    val chipDisabledSecondaryContentColor =
        disabledSecondaryContentColor ?: chipSecondaryContentColor.copy(alpha = 0.5f)
    val chipDisabledIconColor = disabledIconColor ?: chipIconColor.copy(alpha = 0.5f)

    return ChipDefaults.chipColors(
        backgroundColor = chipBackgroundColor,
        contentColor = chipContentColor,
        secondaryContentColor = chipSecondaryContentColor,
        iconColor = chipIconColor,
        disabledBackgroundColor = chipDisabledBackgroundColor,
        disabledContentColor = chipDisabledContentColor,
        disabledSecondaryContentColor = chipDisabledSecondaryContentColor,
        disabledIconColor = chipDisabledIconColor,
    )
}

@Composable
fun themedSplitChipColors(
    colorScheme: ColorScheme,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    secondaryContentColor: Color? = null,
    checkedToggleControlColor: Color? = null,
    uncheckedToggleControlColor: Color? = null,
    splitBackgroundOverlayColor: Color? = null,
): SplitToggleChipColors {
    val context = LocalContext.current
    val themeColor = remember { colorScheme.getColor(context).composeColor }
    val palette = dynamicDarkColorPalette(themeColor)

    val chipBackgroundColor = backgroundColor ?: palette.surface
    val chipContentColor = contentColor ?: palette.onSurface
    val chipSecondaryContentColor = secondaryContentColor ?: palette.onSurfaceVariant
    val chipCheckedToggleControlColor = checkedToggleControlColor ?: palette.secondary
    val chipUncheckedToggleControlColor = uncheckedToggleControlColor ?: chipContentColor
    val chipSplitBackgroundOverlayColor = splitBackgroundOverlayColor ?: Color.White.copy(alpha = 0.05f)

    return splitToggleChipColors(
        backgroundColor = chipBackgroundColor,
        contentColor = chipContentColor,
        secondaryContentColor = chipSecondaryContentColor,
        checkedToggleControlColor = chipCheckedToggleControlColor,
        uncheckedToggleControlColor = chipUncheckedToggleControlColor,
        splitBackgroundOverlayColor = chipSplitBackgroundOverlayColor,
    )
}