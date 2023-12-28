package gay.depau.worldclocktile.composables

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.RevealActionType
import androidx.wear.compose.foundation.RevealValue
import androidx.wear.compose.foundation.rememberRevealState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.SwipeToRevealChip
import androidx.wear.compose.material.SwipeToRevealDefaults
import androidx.wear.compose.material.SwipeToRevealPrimaryAction
import androidx.wear.compose.material.SwipeToRevealUndoAction
import androidx.wear.compose.material.Text
import gay.depau.worldclocktile.presentation.theme.themedChipColors
import gay.depau.worldclocktile.shared.utils.ColorScheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalWearFoundationApi::class, ExperimentalWearMaterialApi::class)
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

    if (deleteEnabled) {
        val revealState = rememberRevealState()
        val scope = rememberCoroutineScope()
        var hide by remember { mutableStateOf(false) }
        var pendingDelete by remember { mutableStateOf(false) }

        LaunchedEffect(pendingDelete) {
            if (pendingDelete) {
                scope.launch {
                    revealState.animateTo(RevealValue.Revealed)
                    delay(2000)
                    if (pendingDelete) {
                        hide = true
                        delay(200)
                        onDelete()
                        pendingDelete = false
                        hide = false
                    }
                }
            } else {
                revealState.animateTo(RevealValue.Covered)
            }
        }

        AnimatedVisibility(!hide) {
            SwipeToRevealChip(
                revealState = revealState,
                modifier = Modifier
                    .fillMaxWidth()
                    // Use edgeSwipeToDismiss to allow SwipeToDismissBox to capture swipe events
//            .edgeSwipeToDismiss(swipeToDismissBoxState)
                    .semantics {
                        // Use custom actions to make the primary and secondary actions accessible
                        customActions = listOf(
                            CustomAccessibilityAction("Delete") {
                                /* Add the primary action click handler here */
                                true
                            },
                        )
                    },
                primaryAction = {
                    SwipeToRevealPrimaryAction(
                        revealState = revealState,
                        icon = { Icon(SwipeToRevealDefaults.Delete, "Delete") },
                        label = { Text("Delete") },
                        onClick = {
                            revealState.lastActionType = RevealActionType.PrimaryAction
                            pendingDelete = true
                        }
                    )
                },
                undoPrimaryAction = {
                    SwipeToRevealUndoAction(
                        revealState = revealState,
                        label = { Text("Undo") },
                        onClick = {
                            revealState.lastActionType = RevealActionType.UndoAction
                            pendingDelete = false
                        }
                    )
                },
                onFullSwipe = {
                    revealState.lastActionType = RevealActionType.PrimaryAction
                    pendingDelete = true
                }
            ) {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = onClick,
                    enabled = chipEnabled,
                    label = label,
                    secondaryLabel = secondaryLabel,
                    colors = themedChipColors { colorScheme })
            }
        }
    } else {
        Chip(
            modifier = modifier,
            onClick = onClick,
            enabled = chipEnabled,
            label = label,
            secondaryLabel = secondaryLabel,
            colors = themedChipColors { colorScheme })
    }
}