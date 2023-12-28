package gay.depau.worldclock.mobile

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExpandCircleDown
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gay.depau.worldclock.mobile.composables.ColorSelectionDialog
import gay.depau.worldclock.mobile.composables.DismissBackground
import gay.depau.worldclock.mobile.composables.ExpandableCard
import gay.depau.worldclock.mobile.composables.TextInputDialog
import gay.depau.worldclock.mobile.composables.TimezoneSelectionDialog
import gay.depau.worldclock.mobile.ui.theme.WorldClockTheme
import gay.depau.worldclock.mobile.utils.DraggableItem
import gay.depau.worldclock.mobile.utils.dragContainer
import gay.depau.worldclock.mobile.utils.rememberDragDropState
import gay.depau.worldclocktile.shared.MAX_TILE_ID
import gay.depau.worldclocktile.shared.TileSettings
import gay.depau.worldclocktile.shared.tzdb.City
import gay.depau.worldclocktile.shared.utils.ColorScheme
import gay.depau.worldclocktile.shared.utils.composeColor
import gay.depau.worldclocktile.shared.utils.currentTimeAt
import gay.depau.worldclocktile.shared.utils.dynamicDarkColorPalette
import gay.depau.worldclocktile.shared.utils.dynamicLightColorPalette
import gay.depau.worldclocktile.shared.utils.reducedPrecision
import gay.depau.worldclocktile.shared.utils.timezoneOffsetDescription
import gay.depau.worldclocktile.shared.viewmodels.TileManagementState
import gay.depau.worldclocktile.shared.viewmodels.TileManagementViewModel
import gay.depau.worldclocktile.shared.viewmodels.TileSettingsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class MainActivity : ComponentActivity() {
    private val mViewModel: TileManagementViewModel by viewModels()
    private lateinit var mSettings: MutableMap<Int, TileSettings>

    private fun recomputeListOrders() {
        mSettings.values
            .sortedBy { it.listOrder }
            .forEachIndexed { index, settings ->
                settings.listOrder = index
            }
    }

    private fun addTile() {
        if (mSettings.size > MAX_TILE_ID) return
        try {
            mViewModel.setCanAddRemoveTiles(false)
            recomputeListOrders()
            val newTileId = TileSettings.getFirstAvailableTileId(this)
            val newListOrder = mSettings.size
            mSettings[newTileId] = TileSettings(this, newTileId).apply {
                ensureConfigPopulated()
                listOrder = newListOrder
                addListener(mViewModel)
                mViewModel.refreshSettings(this)
            }
        } finally {
            mViewModel.setCanAddRemoveTiles(true)
        }
    }

    private fun deleteTile(tileId: Int) {
        if (mSettings.size <= 1) return
        try {
            mViewModel.setCanAddRemoveTiles(false)
            mSettings.remove(tileId)!!.apply {
                resetTileSettings()
            }
            recomputeListOrders()
        } finally {
            mViewModel.setCanAddRemoveTiles(true)
        }
    }

    override fun onPause() {
        super.onPause()

        // Ensure tiles the we pretended to delete are actually deleted before the system kills us
        for (tileId in mViewModel.state.value.hiddenTileIds) {
            deleteTile(tileId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSettings = TileSettings.getEnabledTileSettings(this).toMutableMap()
        for (settings in mSettings.values) {
            mViewModel.refreshSettings(settings)
            settings.addListener(mViewModel)
        }
        mViewModel.setCanAddRemoveTiles(true)
        if (mSettings.isEmpty()) {
            addTile()
        }

        setContent {
            val darkTheme = isSystemInDarkTheme()
            WorldClockTheme(useDarkTheme = darkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainView(
                        viewModel = mViewModel,
                        darkTheme = darkTheme,
                        showAbout = {
                            startActivity(Intent(this, AboutActivity::class.java))
                        },
                        addTile = ::addTile,
                        deleteTile = ::deleteTile,
                        hideTile = { tileId -> mViewModel.setTileHidden(tileId, true) },
                        undoHideTile = { tileId -> mViewModel.setTileHidden(tileId, false) },
                        swapTiles = { from, to ->
                            val fromSettings = mSettings[from]!!
                            val toSettings = mSettings[to]!!
                            fromSettings.listOrder =
                                toSettings.listOrder.also { toSettings.listOrder = fromSettings.listOrder }
                        },
                        setTileColor = { tileId, colorScheme ->
                            mSettings[tileId]!!.colorScheme = colorScheme
                        },
                        setTile24h = { tileId, time24h ->
                            mSettings[tileId]!!.time24h = time24h
                        },
                        setTileTimezone = { tileId, city ->
                            mSettings[tileId]!!.timezoneId = city.timezone
                            mSettings[tileId]!!.cityName = city.cityName
                        },
                        setTileName = { tileId, name ->
                            mSettings[tileId]!!.cityName = name
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TileManagementViewModel.canAddTiles(): State<Boolean> {
    val stateValue by state.collectAsState()
    return remember(stateValue.enabledTileIds, stateValue.canAddRemoveTiles) {
        derivedStateOf {
            stateValue.canAddRemoveTiles &&
                    stateValue.enabledTileIds.size <= MAX_TILE_ID
        }
    }
}

@Composable
fun TileManagementViewModel.canRemoveTiles(): State<Boolean> {
    val stateValue by state.collectAsState()
    return remember(stateValue.enabledTileIds, stateValue.canAddRemoveTiles, stateValue.hiddenTileIds) {
        derivedStateOf {
            stateValue.canAddRemoveTiles &&
                    (stateValue.enabledTileIds.size - stateValue.hiddenTileIds.size) > 1
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissableTimezoneCard(
    modifier: Modifier,
    tileSettings: TileSettingsState,
    canDelete: Boolean,
    darkTheme: Boolean = false,
    previewTime: LocalDateTime? = null,
    setName: () -> Unit = {},
    selectTimezone: () -> Unit = {},
    selectColorScheme: () -> Unit = {},
    set24h: (Boolean) -> Unit = {},
    delete: () -> Unit = {},
) {
    var show by remember { mutableStateOf(true) }
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                show = false
                true
            } else false
        },
    )
    LaunchedEffect(show) {
        if (!show) {
            delay(500)
            delete()
            // Undo things in case the user undoes the delete
            show = true
            dismissState.reset()
        }
    }

    if (canDelete) {
        AnimatedVisibility(
            show,
            exit = fadeOut(spring())
        ) {
            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier,
                background = {
                    DismissBackground(
                        modifier = Modifier.clip(CardDefaults.shape),
                        dismissState = dismissState
                    )
                },
                directions = setOf(DismissDirection.EndToStart),
                dismissContent = {
                    TimezoneCard(
                        modifier = modifier,
                        tileSettings = tileSettings,
                        canDelete = canDelete,
                        darkTheme = darkTheme,
                        previewTime = previewTime,
                        setName = setName,
                        selectTimezone = selectTimezone,
                        selectColorScheme = selectColorScheme,
                        set24h = set24h,
                        delete = delete,
                    )
                })
        }
    } else {
        TimezoneCard(
            modifier = modifier,
            tileSettings = tileSettings,
            canDelete = canDelete,
            darkTheme = darkTheme,
            previewTime = previewTime,
            setName = setName,
            selectTimezone = selectTimezone,
            selectColorScheme = selectColorScheme,
            set24h = set24h,
            delete = delete,
        )
    }
}

@Composable
fun TimezoneCard(
    modifier: Modifier,
    tileSettings: TileSettingsState,
    canDelete: Boolean,
    darkTheme: Boolean = false,
    previewTime: LocalDateTime? = null,
    setName: () -> Unit = {},
    selectTimezone: () -> Unit = {},
    selectColorScheme: () -> Unit = {},
    set24h: (Boolean) -> Unit = {},
    delete: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val colorPalette by remember(darkTheme, tileSettings.colorScheme) {
        derivedStateOf {
            val color = tileSettings.colorScheme.getColor(context).composeColor
            if (darkTheme) {
                dynamicDarkColorPalette(color)
            } else {
                dynamicLightColorPalette(color)
            }
        }
    }

    ExpandableCard(
        modifier = modifier,
        expanded = expanded,
        colors = CardDefaults.cardColors(
            containerColor = colorPalette.primaryContainer,
            contentColor = colorPalette.onPrimaryContainer,
        ),
        setExpanded = { expanded = it },
        mainContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .let {
                                if (expanded)
                                    it
                                        .clickable { setName() }
                                        .semantics { role = Role.Button }
                                else
                                    it
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AnimatedVisibility(visible = expanded) {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 8.dp, top = 16.dp, bottom = 8.dp)
                                    .size(24.dp),
                                imageVector = Icons.Outlined.Label,
                                contentDescription = "Edit",
                                tint = colorPalette.onPrimaryContainer
                            )
                        }
                        Text(
                            modifier =
                            Modifier.padding(top = 16.dp, bottom = 8.dp),
                            text = tileSettings.cityName
                                ?: if (!expanded) "Current location" else "Set name"
                        )
                    }

                    val rotation = if (expanded) 180f else 0f
                    Icon(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 8.dp)
                            .rotate(rotation),
                        imageVector = Icons.Outlined.ExpandCircleDown,
                        contentDescription = "Expand",
                        tint = colorPalette.onPrimaryContainer
                    )
                }
            }

            // Time
            val timezoneId by remember {
                derivedStateOf {
                    tileSettings.timezoneId ?: TimeZone.getDefault().id
                }
            }
            var currentTime by remember {
                mutableStateOf(
                    previewTime ?: currentTimeAt(timezoneId).reducedPrecision
                )
            }
            if (previewTime == null) {
                LaunchedEffect(Unit) {
                    while (true) {
                        currentTime = currentTimeAt(timezoneId).reducedPrecision
                        delay(1000)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = if (tileSettings.time24h) {
                        currentTime.format(DateTimeFormatter.ofPattern("H:mm"))
                    } else {
                        currentTime.format(DateTimeFormatter.ofPattern("h:mm"))
                    },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 48.sp, /*color = color*/ fontWeight = FontWeight.Bold
                    ),
                )
                AnimatedVisibility(!tileSettings.time24h) {
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = if (currentTime.hour < 12) {
                            "AM"
                        } else {
                            "PM"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp, /* color = color */
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Clip,

                        )
                }
            }
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp),
                text = timezoneOffsetDescription(timezoneId), /* color = colorLight */
            )

        },
        expandedContent = {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // Time zone
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clickable { selectTimezone() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = Icons.Outlined.Public,
                        contentDescription = "Timezone",
                        tint = colorPalette.onPrimaryContainer
                    )
                    Text(
                        text = tileSettings.timezoneId ?: "Current location",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Color scheme
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clickable {
                            selectColorScheme()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = "Color scheme",
                        tint = colorPalette.onPrimaryContainer
                    )
                    Text(
                        text = tileSettings.colorScheme.getName(context),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                // 24-hour time
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clickable { set24h(!tileSettings.time24h) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "24-hour time",
                        tint = colorPalette.onPrimaryContainer
                    )
                    Text(
                        text = "24-hour time",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        modifier = Modifier.size(24.dp),
                        checked = tileSettings.time24h,
                        onCheckedChange = set24h,
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorPalette.primary,
                            uncheckedColor = colorPalette.onSurfaceVariant,
                            checkmarkColor = colorPalette.onPrimary,
                        )
                    )
                }

                // Delete
                if (canDelete) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clickable {
                                delete()
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .size(24.dp),
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Color scheme",
                            tint = colorPalette.onPrimaryContainer
                        )
                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        })
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun MainView(
    modifier: Modifier = Modifier,
    viewModel: TileManagementViewModel,
    darkTheme: Boolean = false,
    addTile: () -> Unit = { },
    swapTiles: (Int, Int) -> Unit = { _, _ -> },
    deleteTile: (Int) -> Unit = { },
    hideTile: (Int) -> Unit = { },
    undoHideTile: (Int) -> Unit = { },
    setTileColor: (Int, ColorScheme) -> Unit = { _, _ -> },
    setTileName: (Int, String) -> Unit = { _, _ -> },
    setTile24h: (Int, Boolean) -> Unit = { _, _ -> },
    setTileTimezone: (Int, City) -> Unit = { _, _ -> },
    showAbout: () -> Unit = { },
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val canRemoveTiles by viewModel.canRemoveTiles()

    var selectColorForTile by remember { mutableStateOf<Int?>(null) }
    var selectTimezoneForTile by remember { mutableStateOf<Int?>(null) }
    var setTileNameForTile by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .then(modifier),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    var menuOpen by remember { mutableStateOf(false) }
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Localized description"
                        )
                    }
                    if (menuOpen) {
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(
                                onClick = {
                                    showAbout()
                                    menuOpen = false
                                },
                                text = { Text("About") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.TwoTone.Info,
                                        contentDescription = "About"
                                    )
                                },
                            )
                        }
                    }

                }
            )
        },
        floatingActionButton = {
            val canAddTiles by viewModel.canAddTiles()
            AnimatedVisibility(visible = canAddTiles) {
                LargeFloatingActionButton(onClick = addTile) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->

        val sortedTileIds by remember {
            derivedStateOf {
                state.enabledTileIds
                    .filter { it !in state.hiddenTileIds }
                    .distinct()
                    .toList()
                    .sortedBy { state.tileSettings[it]!!.listOrder }
            }
        }
        val dragDropState = rememberDragDropState(
            listState, maxIndex = sortedTileIds.size - 1
        ) { fromIndex, toIndex ->
            if (toIndex >= sortedTileIds.size || fromIndex >= sortedTileIds.size) return@rememberDragDropState
            swapTiles(sortedTileIds[fromIndex], sortedTileIds[toIndex])
        }

        var lastNumTiles by remember { mutableIntStateOf(sortedTileIds.size) }
        LaunchedEffect(sortedTileIds.size) {
            if (sortedTileIds.size > lastNumTiles) {
                delay(100)
                listState.animateScrollToItem(sortedTileIds.size - 1)
            }
            lastNumTiles = sortedTileIds.size
        }

        LazyColumn(
            state = listState,
            modifier =
            Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .dragContainer(dragDropState),
            // Dodge FAB
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            println("sortedTileIds: $sortedTileIds")
            items(sortedTileIds.size, key = { sortedTileIds[it] }) { index ->
                val i = sortedTileIds[index]
                DraggableItem(dragDropState, index) { isDragging ->
                    val elevation by animateDpAsState(
                        if (isDragging) 8.dp else 0.dp,
                        label = "drag elevation"
                    )
                    DismissableTimezoneCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation),
                        tileSettings = state.tileSettings[i]!!,
                        canDelete = canRemoveTiles,
                        darkTheme = darkTheme,
                        setName = {
                            setTileNameForTile = i
                        },
                        selectTimezone = {
                            selectTimezoneForTile = i
                        },
                        selectColorScheme = {
                            selectColorForTile = i
                        },
                        set24h = {
                            setTile24h(i, it)
                        },
                        delete = {
                            hideTile(i)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    "Tile deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true,
                                )
                                undoHideTile(i)
                                if (result != SnackbarResult.ActionPerformed)
                                    deleteTile(i)
                                else
                                    listState.animateScrollToItem(index)
                            }
                        },
                    )
                }
            }
            item("spacer") {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }

        if (selectColorForTile != null) {
            ColorSelectionDialog(
                onDismiss = { selectColorForTile = null },
                onColorSelected = {
                    setTileColor(selectColorForTile!!, it)
                    selectColorForTile = null
                }
            )
        }
        if (selectTimezoneForTile != null) {
            TimezoneSelectionDialog(
                onDismiss = { selectTimezoneForTile = null },
                onTimezoneSelected = {
                    setTileTimezone(selectTimezoneForTile!!, it)
                    selectTimezoneForTile = null
                },
                time24h = state.tileSettings[selectTimezoneForTile!!]!!.time24h
            )
        }
        if (setTileNameForTile != null) {
            TextInputDialog(
                onDismiss = { setTileNameForTile = null },
                onSubmit = {
                    setTileName(setTileNameForTile!!, it)
                    setTileNameForTile = null
                },
                label = { Text("Name") },
                initialText = state.tileSettings[setTileNameForTile!!]!!.cityName ?: ""
            )
        }
    }
}

private fun previewViewModel(): TileManagementViewModel {
    return TileManagementViewModel().apply {
        setState(
            TileManagementState(
                enabledTileIds = listOf(7, 3, 5),
                tileSettings = mapOf(
                    7 to TileSettingsState(
                        timezoneId = "America/New York",
                        cityName = "New York",
                        time24h = false,
                        listOrder = 0,
                        colorScheme = ColorScheme.Default
                    ),
                    3 to TileSettingsState(
                        timezoneId = "Europe/Rome",
                        cityName = "Los Angeles",
                        time24h = true,
                        listOrder = 1,
                        colorScheme = ColorScheme.BUBBLE_GUM
                    ),
                    5 to TileSettingsState(
                        timezoneId = "Asia/Tokyo",
                        cityName = "Tokyo",
                        time24h = true,
                        listOrder = 2,
                        colorScheme = ColorScheme.AMETHYST
                    ),
                ),
                canAddRemoveTiles = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    val viewModel = previewViewModel()
    val darkTheme = isSystemInDarkTheme()

    fun updateTileSettings(tileId: Int, predicate: (TileSettingsState) -> TileSettingsState) {
        viewModel.setState(
            viewModel.state.value.copy(
                tileSettings = viewModel.state.value.tileSettings.toMutableMap().apply {
                    this[tileId] = predicate(this[tileId]!!)
                }
            )
        )
    }

    WorldClockTheme(useDarkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            MainView(
                viewModel = viewModel,
                darkTheme = darkTheme,
                addTile = {
                    viewModel.setState(
                        viewModel.state.value.copy(
                            canAddRemoveTiles = true,
                            enabledTileIds = viewModel.state.value.enabledTileIds + viewModel.state.value.firstAvailableTileId,
                            tileSettings = viewModel.state.value.tileSettings + (viewModel.state.value.firstAvailableTileId to TileSettingsState(
                                timezoneId = TimeZone.getDefault().id,
                                cityName = null,
                                time24h = false,
                                listOrder = viewModel.state.value.tileSettings.size,
                                colorScheme = ColorScheme.Default
                            ))
                        )
                    )
                },
                swapTiles = { from, to ->
                    viewModel.setState(
                        viewModel.state.value.copy(
                            tileSettings = viewModel.state.value.tileSettings.toMutableMap().apply {
                                val fromOrder = viewModel.state.value.tileSettings[from]!!.listOrder
                                val toOrder = viewModel.state.value.tileSettings[to]!!.listOrder
                                this[from] = viewModel.state.value.tileSettings[from]!!.copy(
                                    listOrder = toOrder
                                )
                                this[to] = viewModel.state.value.tileSettings[to]!!.copy(
                                    listOrder = fromOrder
                                )
                            }
                        )
                    )
                },
                deleteTile = {
                    viewModel.setState(
                        viewModel.state.value.copy(
                            enabledTileIds = viewModel.state.value.enabledTileIds - it,
                            tileSettings = viewModel.state.value.tileSettings - it,
                        )
                    )
                },
                setTileColor = { tileId, colorScheme ->
                    updateTileSettings(tileId) {
                        it.copy(colorScheme = colorScheme)
                    }
                },
                setTile24h = { tileId, time24h ->
                    updateTileSettings(tileId) {
                        it.copy(time24h = time24h)
                    }
                },
                setTileTimezone = { tileId, city ->
                    updateTileSettings(tileId) {
                        it.copy(
                            cityName = city.cityName,
                            timezoneId = city.timezone
                        )
                    }
                },
                setTileName = { tileId, name ->
                    updateTileSettings(tileId) {
                        it.copy(cityName = name)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimezoneCardPreview() {
    val viewModel = previewViewModel()
    val state by viewModel.state.collectAsState()
    val darkTheme = isSystemInDarkTheme()

    WorldClockTheme(useDarkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            TimezoneCard(
                modifier = Modifier.fillMaxWidth(),
                tileSettings = state.tileSettings[0]!!,
                canDelete = true,
                darkTheme = darkTheme,
            )
        }
    }
}