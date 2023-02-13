package gay.depau.worldclocktile.presentation

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tiles.TileService.EXTRA_CLICKABLE_ID
import gay.depau.worldclocktile.AppSettings
import gay.depau.worldclocktile.R
import gay.depau.worldclocktile.WorldClockTileService
import gay.depau.worldclocktile.composables.MainView
import gay.depau.worldclocktile.composables.ScalingLazyColumnWithRSB
import gay.depau.worldclocktile.composables.YesNoConfirmationDialog
import gay.depau.worldclocktile.presentation.theme.themedChipColors
import gay.depau.worldclocktile.presentation.theme.themedSplitChipColors
import gay.depau.worldclocktile.presentation.viewmodels.TileManagementState
import gay.depau.worldclocktile.presentation.viewmodels.TileManagementViewModel
import gay.depau.worldclocktile.presentation.viewmodels.TileSettingsState
import gay.depau.worldclocktile.presentation.views.AboutView
import gay.depau.worldclocktile.utils.ColorScheme
import gay.depau.worldclocktile.utils.currentTimeAt
import gay.depau.worldclocktile.utils.timezoneSimpleNames
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


class TileManagementActivity : ComponentActivity() {
    private val mViewModel: TileManagementViewModel by viewModels()
    private lateinit var mSettings: Map<Int, AppSettings>

    private fun openTileSettingsActivity(id: Int) {
        startActivity(
            Intent(this, TileSettingsActivity::class.java).apply {
                putExtra(EXTRA_CLICKABLE_ID, "tile$id")
            }
        )
    }

    private fun refreshEnabledTiles() {
        lifecycleScope.launch {
            for (i in 0..WorldClockTileService.MAX_TILE_ID) {
                mViewModel.setTileEnabled(
                    i,
                    WorldClockTileService.isTileEnabled(this@TileManagementActivity, i)
                )
            }
            mViewModel.setCanAddRemoveTiles(true)
        }
    }

    override fun onResume() {
        super.onResume()

        for (i in 0..WorldClockTileService.MAX_TILE_ID) {
            mViewModel.refreshSettings(mSettings[i]!!)
        }
        refreshEnabledTiles()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = mutableMapOf<Int, AppSettings>()
        for (i in 0..WorldClockTileService.MAX_TILE_ID) {
            settings[i] = AppSettings(this, i).apply {
                addListener(mViewModel)
                mViewModel.refreshSettings(this)
            }

        }
        mSettings = settings

        refreshEnabledTiles()

        setContent {
            val navController = rememberSwipeDismissableNavController()
            SwipeDismissableNavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startDestination = "main"
            ) {
                composable("main") {
                    val canEnableMoreTiles by mViewModel.canAddTiles()
                    val canDeleteTiles by mViewModel.canRemoveTiles()

                    TileManagementView(
                        mViewModel,
                        setTileEnabled = { id, enabled ->
                            if ((enabled && canEnableMoreTiles) || (!enabled && canDeleteTiles)) {
                                try {
                                    mViewModel.setCanAddRemoveTiles(false)
                                    WorldClockTileService.setTileEnabled(
                                        this@TileManagementActivity,
                                        id,
                                        enabled
                                    )
                                    mViewModel.setTileEnabled(id, enabled)
                                    mSettings[id]!!.resetTileSettings()
                                } finally {
                                    refreshEnabledTiles()
                                    mViewModel.setCanAddRemoveTiles(true)
                                }
                            }
                        },
                        openTileSettings = { id ->
                            openTileSettingsActivity(id)
                        },
                        openAbout = {
                            navController.navigate("about")
                        }
                    )
                }
                composable("about") {
                    AboutView()
                }
            }
        }
    }
}

@Composable
fun ChipWithDeleteButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    allEnabled: Boolean = true,
    deleteEnabled: Boolean = true,
    label: @Composable RowScope.() -> Unit,
    secondaryLabel: @Composable (RowScope.() -> Unit)? = null,
    colorScheme: ColorScheme,
) {
    if (!deleteEnabled || !allEnabled) {
        Chip(
            modifier = modifier,
            onClick = onClick,
            enabled = allEnabled,
            label = label,
            secondaryLabel = secondaryLabel,
            colors = themedChipColors { colorScheme }
        )
    } else {
        SplitToggleChip(
            modifier = modifier,
            onClick = onClick,
            onCheckedChange = { onDelete() },
            checked = true,
            label = label,
            secondaryLabel = secondaryLabel,
            toggleControl = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete tile",
                )
            },
            colors = themedSplitChipColors(colorScheme = colorScheme)
        )
    }
}

@Composable
fun TileManagementView(
    viewModel: TileManagementViewModel,
    setTileEnabled: (Int, Boolean) -> Unit,
    openTileSettings: (Int) -> Unit,
    openAbout: () -> Unit,
) {
    val listState = rememberScalingLazyListState()
    val state by viewModel.state.collectAsState()
    var tileToDelete by remember { mutableStateOf<Int?>(null) }
    val canRemoveTiles by viewModel.canRemoveTiles()
    val showDeleteDialog by remember { derivedStateOf { tileToDelete != null && canRemoveTiles } }

    MainView(listState) {
        ScalingLazyColumnWithRSB(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp, alignment = Alignment.Top
            ),
            anchorType = ScalingLazyListAnchorType.ItemCenter,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item("title") {
                Text(
                    text = "World clock tiles",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.title2
                )
            }
            item("spacer1") { Spacer(modifier = Modifier.height(8.dp)) }
            items(state.enabledTileIds, key = { it }) { i ->
                val settings by remember { derivedStateOf { state.tileSettings[i]!! } }

                ChipWithDeleteButton(
                    modifier = Modifier.fillMaxWidth(),
                    deleteEnabled = canRemoveTiles,
                    label = {
                        if ((settings.cityName ?: "") == "")
                            Text(
                                text = "[Not set]",
                                fontStyle = FontStyle.Italic,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        else
                            Text(
                                text = settings.cityName!!,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                    },
                    secondaryLabel = {
                        if (settings.timezoneId != null) {
                            val time = currentTimeAt(settings.timezoneId!!).let {
                                if (settings.time24h) it.format(DateTimeFormatter.ofPattern("H:mm"))
                                else it.format(DateTimeFormatter.ofPattern("h:mm a"))
                            }
                            Text(
                                text = "$time, ${timezoneSimpleNames[settings.timezoneId] ?: settings.timezoneId!!}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = "Tap to set up",
                                fontStyle = FontStyle.Italic,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    colorScheme = settings.colorScheme,
                    onClick = { openTileSettings(i) },
                    onDelete = { tileToDelete = i },
                )
            }
            item("spacer2") { Spacer(modifier = Modifier.height(8.dp)) }
            item("addBtn") {
                val canAddTiles by viewModel.canAddTiles()
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    CompactChip(
                        label = {
                            Text(text = "Add tile")
                        },
                        enabled = canAddTiles,
                        onClick = {
                            setTileEnabled(state.firstAvailableTileId, true)
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                                contentDescription = "Add tile"
                            )
                        }
                    )
                }
            }
            item("spacer3") { Spacer(modifier = Modifier.height(8.dp)) }
            item("helpCard") {
                Card(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                ) {
                    Text(
                        text = "Add up to 10 tiles here, then add them to your carousel from your watch home.",
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            item("spacer4") { Spacer(modifier = Modifier.height(8.dp)) }
            item("aboutBtn") {
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = openAbout, modifier = Modifier.size(40.dp), content = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_info),
                            contentDescription = "About app"
                        )
                    })
                }
            }
        }
    }

    val tileToDeleteName by remember { derivedStateOf { state.tileSettings[tileToDelete]?.cityName } }
    YesNoConfirmationDialog(
        showDialog = showDeleteDialog,
        title = {
            Text(text = "Delete tile?")
        },
        onYes = {
            setTileEnabled(tileToDelete!!, false)
            tileToDelete = null
        },
        dismissDialog = { tileToDelete = null }) {
        Text(
            text = tileToDeleteName ?: "[Not set]",
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontStyle = if (tileToDeleteName == null) FontStyle.Italic else null
        )
    }
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun TileManagementViewPreview() {
    val viewModel = TileManagementViewModel().apply {
        setState(
            TileManagementState(
                enabledTileIds = listOf(0, 1, 2, 3),
                tileSettings = mapOf(
                    0 to TileSettingsState.Empty,
                    1 to TileSettingsState(
                        cityName = "New York, NY, USA",
                        timezoneId = "America/New_York",
                        colorScheme = ColorScheme.ALMOND,
                        time24h = true
                    ),
                    2 to TileSettingsState.Empty,
                    3 to TileSettingsState.Empty,
                    4 to TileSettingsState.Empty,
                    5 to TileSettingsState.Empty,
                    6 to TileSettingsState.Empty,
                    7 to TileSettingsState.Empty,
                    8 to TileSettingsState.Empty,
                    9 to TileSettingsState.Empty,
                ),
                canAddRemoveTiles = true
            )
        )
    }

    TileManagementView(
        viewModel,
        setTileEnabled = { id, enabled ->
            viewModel.setTileEnabled(id, enabled)
        },
        openTileSettings = { },
        openAbout = { }
    )
}