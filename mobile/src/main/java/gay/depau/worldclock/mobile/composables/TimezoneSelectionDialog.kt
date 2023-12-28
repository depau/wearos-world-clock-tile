package gay.depau.worldclock.mobile.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import gay.depau.worldclock.mobile.thisApplication
import gay.depau.worldclocktile.shared.tzdb.City
import gay.depau.worldclocktile.shared.tzdb.DbLessSampleDataDao
import gay.depau.worldclocktile.shared.tzdb.TimezoneDao
import gay.depau.worldclocktile.shared.utils.currentTimeAt
import kotlinx.coroutines.flow.emptyFlow
import java.time.format.DateTimeFormatter

@Composable
fun TimezoneSelectionDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onTimezoneSelected: (city: City) -> Unit,
    time24h: Boolean = false,
    dao: TimezoneDao = LocalContext.current.thisApplication.database.getTimezoneDao(),
    initialQuery: String = "",
) {
    var query by rememberSaveable { mutableStateOf(initialQuery) }

    val results by (if (query.length >= 3)
        dao.searchCities("*$query*")
    else
        emptyFlow()).collectAsState(initial = emptyList())

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
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        label = { Text("Search cities and timezones") },
                    )

                    LazyColumn(
                        modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                    ) {
                        if (results.isEmpty()) {
                            item {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    textAlign = TextAlign.Center,
                                    text = if (query.length >= 3) "No results" else "Type to search",
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                )
                            }
                        } else {
                            items(results) { city ->
                                val localTimeStr by remember {
                                    derivedStateOf {
                                        currentTimeAt(city.timezone).let {
                                            if (time24h) it.format(
                                                DateTimeFormatter.ofPattern("H:mm")
                                            )
                                            else it.format(DateTimeFormatter.ofPattern("h:mm a"))
                                        }
                                    }
                                }

                                ListItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onTimezoneSelected(city)
                                        },
                                    colors = ListItemDefaults.colors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        headlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        overlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                    headlineContent = { Text(city.cityName) },
                                    overlineContent = { Text(city.timezoneName) },
                                    trailingContent = { Text(localTimeStr) },
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
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
}


@Preview
@Composable
fun TimezoneSelectionDialogPreview() {
    TimezoneSelectionDialog(
        dao = DbLessSampleDataDao(),
        onDismiss = {},
        onTimezoneSelected = {},
        initialQuery = "Lombardy",
    )
}