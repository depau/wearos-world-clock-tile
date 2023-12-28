package gay.depau.worldclock.mobile.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(modifier: Modifier = Modifier, dismissState: DismissState) {
    // The progress goes from 0 to 1 but it's always 1 when not dismissing
    // The progress starts updating at 15% of the swipe, so we rescale it to go from 0 to 1
    val progress = ((if (dismissState.progress == 1f) 0f else dismissState.progress - 0.15f) / 0.85f).coerceIn(0f, 1f)
    println("progress: $progress")
    val color = MaterialTheme.colorScheme.error.copy(alpha = progress)
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = "delete",
            tint = MaterialTheme.colorScheme.error
                .copy(alpha = 1f - progress)
                .compositeOver(MaterialTheme.colorScheme.onError)
        )
    }
}