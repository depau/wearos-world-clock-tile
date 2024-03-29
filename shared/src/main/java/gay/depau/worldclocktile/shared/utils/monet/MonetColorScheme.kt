package gay.depau.worldclocktile.shared.utils.monet

/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import gay.depau.worldclocktile.shared.utils.monet.cam.Cam
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

const val TAG = "ColorScheme"
const val ACCENT1_CHROMA = 48.0f
const val ACCENT2_CHROMA = 16.0f
const val ACCENT3_CHROMA = 32.0f
const val ACCENT3_HUE_SHIFT = 60.0f
const val NEUTRAL1_CHROMA = 4.0f
const val NEUTRAL2_CHROMA = 8.0f
const val GOOGLE_BLUE = 0xFF1b6ef3.toInt()
const val MIN_CHROMA = 15
const val MIN_LSTAR = 10

class MonetColorScheme(@ColorInt seed: Int, val darkTheme: Boolean) {
    val accent1: List<Int>
    val accent2: List<Int>
    val accent3: List<Int>
    val neutral1: List<Int>
    val neutral2: List<Int>

    val accent1_100: Int
    val accent2_100: Int
    val accent3_100: Int
    val neutral1_100: Int
    val neutral2_100: Int

    val allAccentColors: List<Int>
        get() {
            val allColors = mutableListOf<Int>()
            allColors.addAll(accent1)
            allColors.addAll(accent2)
            allColors.addAll(accent3)
            return allColors
        }
    val allNeutralColors: List<Int>
        get() {
            val allColors = mutableListOf<Int>()
            allColors.addAll(neutral1)
            allColors.addAll(neutral2)
            return allColors
        }
    val backgroundColor
        get() = ColorUtils.setAlphaComponent(if (darkTheme) neutral1[8] else neutral1[0], 0xFF)
    val accentColor
        get() = ColorUtils.setAlphaComponent(if (darkTheme) accent1[2] else accent1[6], 0xFF)

    init {
        val seedArgb = if (seed == Color.TRANSPARENT) GOOGLE_BLUE else seed
        val camSeed = Cam.fromInt(seedArgb)
        val hue = camSeed.hue
        val chroma = camSeed.chroma.coerceAtLeast(ACCENT1_CHROMA)
        accent1 = Shades.of(hue, chroma).toList()
        accent2 = Shades.of(hue, ACCENT2_CHROMA).toList()
        accent3 = Shades.of(hue + ACCENT3_HUE_SHIFT, ACCENT3_CHROMA).toList()
        neutral1 = Shades.of(hue, NEUTRAL1_CHROMA).toList()
        neutral2 = Shades.of(hue, NEUTRAL2_CHROMA).toList()

        // Added by depau: for some reason the original code seems to not compute these here?
        accent1_100 = Cam.getInt(hue, chroma, 100f)
        accent2_100 = Cam.getInt(hue, ACCENT2_CHROMA, 100f)
        accent3_100 = Cam.getInt(hue + ACCENT3_HUE_SHIFT, ACCENT3_CHROMA, 100f)
        neutral1_100 = Cam.getInt(hue, NEUTRAL1_CHROMA, 100f)
        neutral2_100 = Cam.getInt(hue, NEUTRAL2_CHROMA, 100f)
    }

    override fun toString(): String {
        return "ColorScheme {\n" +
                "  neutral1: ${humanReadable(neutral1)}\n" +
                "  neutral2: ${humanReadable(neutral2)}\n" +
                "  accent1: ${humanReadable(accent1)}\n" +
                "  accent2: ${humanReadable(accent2)}\n" +
                "  accent3: ${humanReadable(accent3)}\n" +
                "}"
    }

    companion object {
        private fun wrapDegrees(degrees: Int): Int {
            return when {
                degrees < 0 -> {
                    (degrees % 360) + 360
                }

                degrees >= 360 -> {
                    degrees % 360
                }

                else -> {
                    degrees
                }
            }
        }

        private fun hueDiff(a: Float, b: Float): Float {
            return 180f - ((a - b).absoluteValue - 180f).absoluteValue
        }

        private fun humanReadable(colors: List<Int>): String {
            return colors.joinToString { "#" + Integer.toHexString(it) }
        }

        private fun score(cam: Cam, proportion: Double): Double {
            val proportionScore = 0.7 * 100.0 * proportion
            val chromaScore = if (cam.chroma < ACCENT1_CHROMA) 0.1 * (cam.chroma - ACCENT1_CHROMA)
            else 0.3 * (cam.chroma - ACCENT1_CHROMA)
            return chromaScore + proportionScore
        }

        private fun huePopulations(
            camByColor: Map<Int, Cam>,
            populationByColor: Map<Int, Double>,
        ): List<Double> {
            val huePopulation = List(size = 360, init = { 0.0 }).toMutableList()
            for (entry in populationByColor.entries) {
                val population = populationByColor[entry.key]!!
                val cam = camByColor[entry.key]!!
                val hue = cam.hue.roundToInt() % 360
                huePopulation[hue] = huePopulation[hue] + population
            }
            return huePopulation
        }
    }
}