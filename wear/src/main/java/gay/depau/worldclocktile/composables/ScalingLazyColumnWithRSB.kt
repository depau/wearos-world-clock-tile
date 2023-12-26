package gay.depau.worldclocktile.composables

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.ScalingParams
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll


@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ScalingLazyColumnWithRSB(
    modifier: Modifier = Modifier,
    state: ScalingLazyListState = rememberScalingLazyListState(),
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        Arrangement.spacedBy(
            space = 4.dp,
            alignment = if (!reverseLayout) Alignment.Top else Alignment.Bottom
        ),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams(),
    anchorType: ScalingLazyListAnchorType = ScalingLazyListAnchorType.ItemCenter,
    autoCentering: AutoCenteringParams? = AutoCenteringParams(),
    content: ScalingLazyListScope.() -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    ScalingLazyColumn(
        modifier = modifier.rotaryWithScroll(
            scrollableState = state,
            flingBehavior = flingBehavior,
            focusRequester = focusRequester
        ),
        state = state,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        scalingParams = scalingParams,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        autoCentering = autoCentering,
        anchorType = anchorType,
        content = content
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
