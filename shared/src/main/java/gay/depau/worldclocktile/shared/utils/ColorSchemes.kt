package gay.depau.worldclocktile.shared.utils

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColor
import gay.depau.worldclocktile.shared.R
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor

val AndroidColor.composeColor: ComposeColor
    get() = ComposeColor(toArgb())

val ComposeColor.androidColor: AndroidColor
    get() = toArgb().toColor()

enum class ColorScheme(
    private val nameRes: Int,
    private val colorRes: Int,
    private val colorLightRes: Int,
) {
    CLOUD(R.string.color_cloud, R.color.cloud, R.color.cloud_light),
    ALMOND(R.string.color_almond, R.color.almond, R.color.almond_light),
    WATERMELON(R.string.color_watermelon, R.color.watermelon, R.color.watermelon_light),
    CORAL(R.string.color_coral, R.color.coral, R.color.coral_light),
    POMELO(R.string.color_pomelo, R.color.pomelo, R.color.pomelo_light),
    GUAVA(R.string.color_guava, R.color.guava, R.color.guava_light),
    PEACH(R.string.color_peach, R.color.peach, R.color.peach_light),
    CHAMPAGNE(R.string.color_champagne, R.color.champagne, R.color.champagne_light),
    CHAI(R.string.color_chai, R.color.chai, R.color.chai_light),
    SAND(R.string.color_sand, R.color.sand, R.color.sand_light),
    HONEY(R.string.color_honey, R.color.honey, R.color.honey_light),
    MELON(R.string.color_melon, R.color.melon, R.color.melon_light),
    WHEAT(R.string.color_wheat, R.color.wheat, R.color.wheat_light),
    DANDELION(R.string.color_dandelion, R.color.dandelion, R.color.dandelion_light),
    LIMONCELLO(R.string.color_limoncello, R.color.limoncello, R.color.limoncello_light),
    LEMONGRASS(R.string.color_lemongrass, R.color.lemongrass, R.color.lemongrass_light),
    LIME(R.string.color_lime, R.color.lime, R.color.lime_light),
    PEAR(R.string.color_pear, R.color.pear, R.color.pear_light),
    SPEARMINT(R.string.color_spearmint, R.color.spearmint, R.color.spearmint_light),
    FERN(R.string.color_fern, R.color.fern, R.color.fern_light),
    FOREST(R.string.color_forest, R.color.forest, R.color.forest_light),
    MINT(R.string.color_mint, R.color.mint, R.color.mint_light),
    JADE(R.string.color_jade, R.color.jade, R.color.jade_light),
    SAGE(R.string.color_sage, R.color.sage, R.color.sage_light),
    STREAM(R.string.color_stream, R.color.stream, R.color.stream_light),
    AQUA(R.string.color_aqua, R.color.aqua, R.color.aqua_light),
    SKY(R.string.color_sky, R.color.sky, R.color.sky_light),
    OCEAN(R.string.color_ocean, R.color.ocean, R.color.ocean_light),
    SAPPHIRE(R.string.color_sapphire, R.color.sapphire, R.color.sapphire_light),
    AMETHYST(R.string.color_amethyst, R.color.amethyst, R.color.amethyst_light),
    LILAC(R.string.color_lilac, R.color.lilac, R.color.lilac_light),
    LAVENDER(R.string.color_lavender, R.color.lavender, R.color.lavender_light),
    FLAMINGO(R.string.color_flamingo, R.color.flamingo, R.color.flamingo_light),
    BUBBLE_GUM(R.string.color_bubble_gum, R.color.bubble_gum, R.color.bubble_gum_light),
    GRAPHITE(R.string.color_graphite, R.color.graphite, R.color.graphite_light);

    fun getName(context: Context) = context.getString(nameRes)

    fun getColor(context: Context) = context.getColor(colorRes).toColor()

    fun getColorLight(context: Context) = context.getColor(colorLightRes).toColor()

    companion object {
        val Default = STREAM
    }
}