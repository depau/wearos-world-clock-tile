package gay.depau.worldclocktile.composables

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.tools.TileLayoutPreview
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer


@OptIn(ExperimentalHorologistApi::class)
class WorldClockTileRenderer(context: Context) :
    SingleTileLayoutRenderer<() -> LayoutElementBuilders.LayoutElement, Nothing?>(context) {
    override fun renderTile(
        state: () -> LayoutElementBuilders.LayoutElement,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
    ): LayoutElementBuilders.LayoutElement {
        return state()
    }
}


@OptIn(ExperimentalHorologistApi::class)
@Composable
fun TilePreview(
    layout: Context.() -> LayoutElementBuilders.LayoutElement,
) {
    val context = LocalContext.current
    TileLayoutPreview(
        state = { context.layout() }, resourceState = null, renderer = WorldClockTileRenderer(LocalContext.current)
    )
}