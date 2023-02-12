package gay.depau.worldclocktile.composables

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.wear.tiles.DeviceParametersBuilders
import androidx.wear.tiles.LayoutElementBuilders
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.tiles.ExperimentalHorologistTilesApi
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer


@OptIn(ExperimentalHorologistTilesApi::class)
class WorldClockTileRenderer(context: Context) :
    SingleTileLayoutRenderer<() -> LayoutElementBuilders.LayoutElement, Nothing?>(context) {
    override fun renderTile(
        state: () -> LayoutElementBuilders.LayoutElement,
        deviceParameters: DeviceParametersBuilders.DeviceParameters
    ): LayoutElementBuilders.LayoutElement {
        return state()
    }
}


@OptIn(ExperimentalHorologistTilesApi::class)
@Composable
fun TilePreview(
    layout: Context.() -> LayoutElementBuilders.LayoutElement,
) {
    val context = LocalContext.current
    TileLayoutPreview(
        state = { context.layout() },
        resourceState = null,
        renderer = WorldClockTileRenderer(LocalContext.current)
    )
}