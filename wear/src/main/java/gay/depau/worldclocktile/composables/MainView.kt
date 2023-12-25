package gay.depau.worldclocktile.composables

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.curvedComposable
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material.*
import gay.depau.worldclocktile.presentation.theme.WorldClockTileTheme

@Composable
fun MainView(
    listState: ScalingLazyListState? = null,
    topText: () -> String? = { null },
    topSpinner: () -> Boolean = { false },
    topTextStyle: TextStyle = TimeTextDefaults.timeTextStyle(color = MaterialTheme.colors.primary),
    content: @Composable () -> Unit
) {
    WorldClockTileTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            timeText = {
                TimeText(startLinearContent = if (topSpinner()) {
                    {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(0.dp)
                                .size(16.dp)
                        )
                    }
                } else if (topText() != null) {
                    {
                        Text(
                            text = topText()!!, style = topTextStyle
                        )
                    }
                } else null, startCurvedContent = if (topSpinner()) {
                    {
                        curvedComposable {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(0.dp)
                                    .size(16.dp)
                            )
                        }
                    }
                } else if (topText() != null) {
                    {
                        curvedText(
                            text = topText()!!, style = CurvedTextStyle(topTextStyle)
                        )
                    }
                } else null)
            },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
            positionIndicator = {
                if (listState != null) {
                    PositionIndicator(listState)
                }
            },
            content = content
        )
    }
}
