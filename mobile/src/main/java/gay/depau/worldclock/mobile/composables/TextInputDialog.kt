package gay.depau.worldclock.mobile.composables

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TextInputDialog(
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    initialText: String = "",
) {
    var text by rememberSaveable { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSubmit(text) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = modifier,
                singleLine = true,
                label = label,
                placeholder = placeholder,
                shape = MaterialTheme.shapes.medium,
                keyboardActions = KeyboardActions(
                    onDone = { onSubmit(text) }
                )
            )
        }
    )
}

@Preview
@Composable
fun TextInputDialogPreview() {
    TextInputDialog(onDismiss = {}, onSubmit = {}, label = { Text("Label") })
}
