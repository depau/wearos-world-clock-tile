package gay.depau.worldclocktile.composables

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.tooling.preview.devices.WearDevices
import gay.depau.worldclocktile.utils.ColorScheme
import gay.depau.worldclocktile.utils.composeColor
import gay.depau.worldclocktile.utils.foreground

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TextInputDialog(
    showDialog: Boolean,
    title: @Composable ColumnScope.() -> Unit,
    inputLabel: String = "",
    initialValue: () -> String = { "" },
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
    ),
    onSubmit: (String) -> Unit,
    onCancel: () -> Unit = {},
    dismissDialog: () -> Unit,
    colorScheme: ColorScheme = ColorScheme.Default,
    content: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Dialog(
        showDialog = showDialog,
        onDismissRequest = {
            onCancel()
            dismissDialog()
        }
    ) {
        var text by rememberSaveable { mutableStateOf(initialValue()) }
        val context = LocalContext.current
        val themeColor by remember { derivedStateOf { colorScheme.getColor(context).composeColor } }

        Alert(
            title = title,
            positiveButton = {
                Button(
                    onClick = {
                        onSubmit(text)
                        dismissDialog()
                    },
                    colors = ButtonDefaults.primaryButtonColors(
                        backgroundColor = themeColor,
                        contentColor = themeColor.foreground
                    )
                ) {
                    Icon(Icons.TwoTone.Done, "Submit")
                }
            },
            negativeButton = {
                Button(colors = ButtonDefaults.secondaryButtonColors(), onClick = {
                    onCancel()
                    dismissDialog()
                }) {
                    Icon(Icons.TwoTone.Close, "Cancel")
                }
            },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                content?.invoke(this)

                val keyboardController = LocalSoftwareKeyboardController.current

                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    label = { Text(inputLabel) },
                    keyboardOptions = keyboardOptions,
                    keyboardActions = KeyboardActions(
                        onAny = { keyboardController?.hide() }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(TextFieldDefaults.MinHeight / 2),
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColor,
                        cursorColor = themeColor,
                        selectionColors = TextSelectionColors(
                            backgroundColor = themeColor.copy(alpha = 0.3f),
                            handleColor = themeColor,
                        ),
                        focusedTextColor = MaterialTheme.colors.onSurface,
                        unfocusedTextColor = MaterialTheme.colors.onSurface
                    ),
                )
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun TextInputDialogPreview() {
    MainView {
        Box(modifier = Modifier.fillMaxSize()) {
            TextInputDialog(
                showDialog = true,
                title = { Text("Set city name") },
                inputLabel = "Name",
                initialValue = { "Initial value" },
                onSubmit = {},
                dismissDialog = {},
            )
        }
    }
}