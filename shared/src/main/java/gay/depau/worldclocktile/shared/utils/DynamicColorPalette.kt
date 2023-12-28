package gay.depau.worldclocktile.shared.utils

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import gay.depau.worldclocktile.shared.utils.monet.MonetColorScheme
import androidx.compose.ui.graphics.Color as ComposeColor

/**
 * This file tries to reimplement the Material You dynamic color generation algorithm.
 * The algorithm is explained in the Android docs:
 *
 * - https://source.android.com/docs/core/display/material#dynamic-color
 * - https://source.android.com/docs/core/display/dynamic-color
 *
 *
 * The tonal palette was adapted from Jetpack Compose DynamicTonalPalette.kt, though the mapping is
 * also documented here:
 *
 * - https://source.android.com/docs/core/display/material#material-library
 */


private val List<Int>.l99: ComposeColor get() = ComposeColor(this[0])
private val List<Int>.l95: ComposeColor get() = ComposeColor(this[1])
private val List<Int>.l90: ComposeColor get() = ComposeColor(this[2])
private val List<Int>.l80: ComposeColor get() = ComposeColor(this[3])
private val List<Int>.l70: ComposeColor get() = ComposeColor(this[4])
private val List<Int>.l60: ComposeColor get() = ComposeColor(this[5])
private val List<Int>.l50: ComposeColor get() = ComposeColor(this[6])
private val List<Int>.l40: ComposeColor get() = ComposeColor(this[7])
private val List<Int>.l30: ComposeColor get() = ComposeColor(this[8])
private val List<Int>.l20: ComposeColor get() = ComposeColor(this[9])
private val List<Int>.l10: ComposeColor get() = ComposeColor(this[10])
private val List<Int>.l0: ComposeColor get() = ComposeColor(this[11])


interface DynamicColorPalette {
    val primary: ComposeColor
    val onPrimary: ComposeColor
    val primaryContainer: ComposeColor
    val onPrimaryContainer: ComposeColor
    val inversePrimary: ComposeColor
    val secondary: ComposeColor
    val onSecondary: ComposeColor
    val secondaryContainer: ComposeColor
    val onSecondaryContainer: ComposeColor
    val tertiary: ComposeColor
    val onTertiary: ComposeColor
    val tertiaryContainer: ComposeColor
    val onTertiaryContainer: ComposeColor
    val background: ComposeColor
    val onBackground: ComposeColor
    val surface: ComposeColor
    val onSurface: ComposeColor
    val surfaceVariant: ComposeColor
    val onSurfaceVariant: ComposeColor
    val inverseSurface: ComposeColor
    val inverseOnSurface: ComposeColor
    val outline: ComposeColor
}

fun DynamicColorPalette.contentColorFor(backgroundColor: Color): Color {
    return when (backgroundColor) {
        primary -> onPrimary
        primaryContainer -> onPrimaryContainer
        secondary -> onSecondary
        secondaryContainer -> onSecondaryContainer
        tertiary -> onTertiary
        tertiaryContainer -> onTertiaryContainer
        background -> onBackground
        surface -> onSurface
        surfaceVariant -> onSurfaceVariant
        inverseSurface -> inverseOnSurface
        else -> Color.Unspecified
    }
}

class DynamicLightColorPalette(val colorScheme: MonetColorScheme) : DynamicColorPalette {
    override val primary: ComposeColor get() = colorScheme.accent1.l40
    override val onPrimary: ComposeColor get() = ComposeColor(colorScheme.accent3_100)
    override val primaryContainer: ComposeColor get() = colorScheme.accent1.l90
    override val onPrimaryContainer: ComposeColor get() = colorScheme.accent1.l10
    override val inversePrimary: ComposeColor get() = colorScheme.accent1.l80
    override val secondary: ComposeColor get() = colorScheme.accent2.l40
    override val onSecondary: ComposeColor get() = ComposeColor(colorScheme.accent3_100)
    override val secondaryContainer: ComposeColor get() = colorScheme.accent2.l90
    override val onSecondaryContainer: ComposeColor get() = colorScheme.accent2.l10
    override val tertiary: ComposeColor get() = colorScheme.accent3.l40
    override val onTertiary: ComposeColor get() = ComposeColor(colorScheme.accent3_100)
    override val tertiaryContainer: ComposeColor get() = colorScheme.accent3.l90
    override val onTertiaryContainer: ComposeColor get() = colorScheme.accent3.l10
    override val background: ComposeColor get() = colorScheme.neutral1.l99
    override val onBackground: ComposeColor get() = colorScheme.neutral1.l10
    override val surface: ComposeColor get() = colorScheme.neutral1.l99
    override val onSurface: ComposeColor get() = colorScheme.neutral1.l10
    override val surfaceVariant: ComposeColor get() = colorScheme.neutral2.l90
    override val onSurfaceVariant: ComposeColor get() = colorScheme.neutral2.l30
    override val inverseSurface: ComposeColor get() = colorScheme.neutral1.l20
    override val inverseOnSurface: ComposeColor get() = colorScheme.neutral1.l95
    override val outline: ComposeColor get() = colorScheme.neutral2.l50
}

class DynamicDarkColorPalette(
    val colorScheme: MonetColorScheme,
) : DynamicColorPalette {
    override val primary: ComposeColor get() = colorScheme.accent1.l80
    override val onPrimary: ComposeColor get() = colorScheme.accent1.l20
    override val primaryContainer: ComposeColor get() = colorScheme.accent1.l30
    override val onPrimaryContainer: ComposeColor get() = colorScheme.accent1.l90
    override val inversePrimary: ComposeColor get() = colorScheme.accent1.l40
    override val secondary: ComposeColor get() = colorScheme.accent2.l80
    override val onSecondary: ComposeColor get() = colorScheme.accent2.l20
    override val secondaryContainer: ComposeColor get() = colorScheme.accent2.l30
    override val onSecondaryContainer: ComposeColor get() = colorScheme.accent2.l90
    override val tertiary: ComposeColor get() = colorScheme.accent3.l80
    override val onTertiary: ComposeColor get() = colorScheme.accent3.l20
    override val tertiaryContainer: ComposeColor get() = colorScheme.accent3.l30
    override val onTertiaryContainer: ComposeColor get() = colorScheme.accent3.l90
    override val background: ComposeColor get() = colorScheme.neutral1.l10
    override val onBackground: ComposeColor get() = colorScheme.neutral1.l90
    override val surface: ComposeColor get() = colorScheme.neutral1.l10
    override val onSurface: ComposeColor get() = colorScheme.neutral1.l90
    override val surfaceVariant: ComposeColor get() = colorScheme.neutral2.l30
    override val onSurfaceVariant: ComposeColor get() = colorScheme.neutral2.l80
    override val inverseSurface: ComposeColor get() = colorScheme.neutral1.l90
    override val inverseOnSurface: ComposeColor get() = colorScheme.neutral1.l20
    override val outline: ComposeColor get() = colorScheme.neutral2.l60
}

private val darkColorPalettes = mutableMapOf<Int, DynamicDarkColorPalette>()
private val lightColorPalettes = mutableMapOf<Int, DynamicLightColorPalette>()


// Memoize the color palettes since they are expensive to compute on Wear OS

/**
 * Generate a Material You dynamic color palette for a given seed color to be used in a dark theme.
 *
 * @param seedColor the seed color
 * @return the generated color palette
 */
fun dynamicDarkColorPalette(seedColor: ComposeColor): DynamicColorPalette {
    val seedArgb = seedColor.toArgb()
    return darkColorPalettes.computeIfAbsent(seedArgb) {
        DynamicDarkColorPalette(MonetColorScheme(seedArgb, true))
    }
}

/**
 * Generate a Material You dynamic color palette for a given seed color to be used in a light theme.
 *
 * @param seedColor the seed color
 * @return the generated color palette
 */
fun dynamicLightColorPalette(seedColor: ComposeColor): DynamicColorPalette {
    val seedArgb = seedColor.toArgb()
    return lightColorPalettes.computeIfAbsent(seedArgb) {
        DynamicLightColorPalette(MonetColorScheme(seedArgb, false))
    }
}