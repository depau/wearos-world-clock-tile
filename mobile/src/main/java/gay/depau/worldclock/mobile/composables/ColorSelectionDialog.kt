package gay.depau.worldclock.mobile.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import gay.depau.worldclock.mobile.R
import gay.depau.worldclocktile.shared.utils.ColorScheme
import gay.depau.worldclocktile.shared.utils.composeColor

@Composable
fun ColorSelectionDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onColorSelected: (ColorScheme) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier,
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
            ) {
                val context = LocalContext.current

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.padding(16.dp),
                ) {
                    items(ColorScheme.entries) { colorScheme ->
                        val iconColor by remember {
                            derivedStateOf {
                                colorScheme.getColor(
                                    context
                                ).composeColor
                            }
                        }

                        Icon(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(48.dp)
                                .border(
                                    3.dp, shape = CircleShape,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                .clickable { onColorSelected(colorScheme) },
                            imageVector = ImageVector.vectorResource(R.drawable.color_preview),
                            contentDescription = "Color: ${colorScheme.name}",
                            tint = iconColor
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ColorSelectionDialogPreview() {
    ColorSelectionDialog(onDismiss = {}, onColorSelected = {})
}
