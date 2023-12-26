package gay.depau.worldclocktile.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import gay.depau.worldclocktile.presentation.theme.themedChipColors
import gay.depau.worldclocktile.shared.utils.ColorScheme
import kotlin.math.roundToInt


@Composable
fun ChipWithDeleteButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    deleteEnabled: Boolean = true,
    chipEnabled: Boolean = true,
    label: @Composable RowScope.() -> Unit,
    secondaryLabel: @Composable (RowScope.() -> Unit)? = null,
    colorScheme: ColorScheme,
) {
    val density = LocalDensity.current
    var isRevealed by remember { mutableStateOf(false) }
    val cardOffset = with(density) { (-48).dp.toPx() }
    val revealTransition by animateFloatAsState(
        targetValue = if (isRevealed && deleteEnabled) cardOffset else 0f
    )

    var offsetX by remember { mutableStateOf(0f) }
    val dragTransition by animateFloatAsState(targetValue = offsetX)

    var rawChipWidth by remember { mutableStateOf(0) }
    var rawChipHeight by remember { mutableStateOf(0) }
    val chipHeight by remember(rawChipHeight) {
        mutableStateOf(with(density) { rawChipHeight.toDp() })
    }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { println("click") }
                .height(chipHeight)
                .clip(RoundedCornerShape(corner = CornerSize(50)))
                .paint(
                    painter = ColorPainter(MaterialTheme.colorScheme.error),
                    contentScale = ContentScale.Crop
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onDelete,
                enabled = deleteEnabled,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete, contentDescription = null,
                    tint = Color.White
                )
            }
        }

        val offset = if (deleteEnabled) (dragTransition + revealTransition)
            .coerceIn(cardOffset, 0f) else 0f

        Chip(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged {
                    rawChipHeight = it.height
                    rawChipWidth = it.width
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            offsetX = 0f
                        }, onDragEnd = {
                            isRevealed = (offsetX < cardOffset / 2) && deleteEnabled
                            offsetX = 0f
                        }, onDragCancel = {
                            isRevealed = (offsetX < cardOffset / 2) && deleteEnabled
                            offsetX = 0f
                        }) { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        if (isRevealed && deleteEnabled && offset.x > rawChipWidth + cardOffset) {
                            onDelete()
                        }
                    }
                }
                .offset { IntOffset(offset.roundToInt(), 0) }
                .shadow(elevation = (offset / cardOffset) * 16.dp, shape = RoundedCornerShape(corner = CornerSize(50))),
            onClick = onClick,
            enabled = chipEnabled,
            label = label,
            secondaryLabel = secondaryLabel,
            colors = themedChipColors { colorScheme })
    }
}