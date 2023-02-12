package gay.depau.worldclocktile.composables

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Done
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog

@Composable
fun YesNoConfirmationDialog(
    showDialog: Boolean,
    title: @Composable ColumnScope.() -> Unit,
    onYes: () -> Unit,
    onNo: () -> Unit? = {},
    dismissDialog: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Dialog(
        showDialog = showDialog,
        onDismissRequest = {
            onNo()
            dismissDialog()
        }
    ) {
        Alert(
            title = title,
            positiveButton = {
                Button(onClick = {
                    onYes()
                    dismissDialog()
                }) {
                    Icon(Icons.TwoTone.Done, "Yes")
                }
            },
            negativeButton = {
                Button(colors = ButtonDefaults.secondaryButtonColors(), onClick = {
                    onNo()
                    dismissDialog()
                }) {
                    Icon(Icons.TwoTone.Close, "No")
                }
            },
            content = content
        )
    }
}