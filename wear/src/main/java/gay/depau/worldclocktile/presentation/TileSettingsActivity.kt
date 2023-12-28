package gay.depau.worldclocktile.presentation

// SPDX-License-Identifier: GNU GPLv3
// This file is part of World Clock Tile.

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ContentAlpha
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.SwitchDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tiles.TileService
import androidx.wear.tooling.preview.devices.WearDevices
import gay.depau.worldclocktile.R
import gay.depau.worldclocktile.WorldClockTileService
import gay.depau.worldclocktile.composables.MainView
import gay.depau.worldclocktile.composables.ScalingLazyColumnWithRSB
import gay.depau.worldclocktile.composables.TextInputDialog
import gay.depau.worldclocktile.composables.rememberForeverScalingLazyListState
import gay.depau.worldclocktile.presentation.theme.chipGradientColors
import gay.depau.worldclocktile.presentation.theme.themedChipColors
import gay.depau.worldclocktile.presentation.theme.toggleChipColors
import gay.depau.worldclocktile.presentation.viewmodels.TileSettingsViewModel
import gay.depau.worldclocktile.presentation.views.AboutView
import gay.depau.worldclocktile.shared.MAX_TILE_ID
import gay.depau.worldclocktile.shared.TileSettings
import gay.depau.worldclocktile.shared.tzdb.DbLessSampleDataDao
import gay.depau.worldclocktile.shared.tzdb.TimezoneDatabase
import gay.depau.worldclocktile.shared.tzdb.populateWithSampleData
import gay.depau.worldclocktile.shared.utils.ColorScheme
import gay.depau.worldclocktile.shared.utils.PreviewContextWrapper
import gay.depau.worldclocktile.shared.utils.composeColor
import gay.depau.worldclocktile.shared.utils.currentTimeAt
import gay.depau.worldclocktile.shared.utils.dynamicDarkColorPalette
import gay.depau.worldclocktile.shared.utils.reducedPrecision
import gay.depau.worldclocktile.shared.utils.timezoneOffsetDescription
import gay.depau.worldclocktile.shared.utils.timezoneSimpleNames
import gay.depau.worldclocktile.shared.viewmodels.TileSettingsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone
import kotlin.properties.Delegates

class TileSettingsActivity : ComponentActivity() {
    private val mViewModel: TileSettingsViewModel by viewModels { TileSettingsViewModel.Factory }
    private lateinit var mSettings: TileSettings
    private var tileId by Delegates.notNull<Int>()

    private fun refreshTile() {
        WorldClockTileService.requestUpdate(this@TileSettingsActivity, tileId)
    }

    private fun goToManagementActivityAndClose() {
        startActivity(Intent(this, TileManagementActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val clickableId = intent.getStringExtra(TileService.EXTRA_CLICKABLE_ID)

        if (clickableId == null || !clickableId.startsWith("tile")) {
            return goToManagementActivityAndClose()
        } else {
            try {
                val id = clickableId.substring(4).toInt()
                if (id in 0..MAX_TILE_ID) {
                    tileId = id
                } else {
                    Toast.makeText(this, "Invalid tile", Toast.LENGTH_SHORT).show()
                    goToManagementActivityAndClose()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid tile", Toast.LENGTH_SHORT).show()
                goToManagementActivityAndClose()
            }
        }
        refreshTile()

        mSettings = TileSettings(this, tileId).apply {
            addListener(mViewModel)
            mViewModel.refreshSettings(this)
        }

        setContent {
            val navController = rememberSwipeDismissableNavController()
            val state by mViewModel.state.collectAsState()
            var queryStartTime by remember { mutableStateOf(System.currentTimeMillis()) }

            SwipeDismissableNavHost(
                modifier = Modifier.fillMaxSize(), navController = navController, startDestination = "main"
            ) {
                composable("main") {
                    MainSettingsView(mViewModel, set24Hour = {
                        mSettings.time24h = it
                        refreshTile()
                    }, openColorSelection = { navController.navigate("select_color") }, openTimeZoneSelection = {
                        mViewModel.resetDbCache()
                        navController.navigate("select_continent")
                        queryStartTime = System.currentTimeMillis()
                    }, openAbout = { navController.navigate("about") },
                        openTileManagement = { goToManagementActivityAndClose() },
                        renameCity = { mSettings.cityName = it })
                }
                composable("select_color") {
                    ColorSelectionView(
                        setColorScheme = {
                            mSettings.colorScheme = it
                            refreshTile()
                        }, selectedColor = state.colorScheme
                    )
                }
                composable("select_continent") {
                    ContinentSelectionView(viewModel = mViewModel,
                        openStdTimeZoneSelection = { navController.navigate("select_std_timezone") },
                        selectContinent = {
                            mViewModel.selectContinent(it)
                            navController.navigate("select_country")
                        }, openSearch = { navController.navigate("search") }, queryStartTime = queryStartTime
                    )
                }
                composable("search") {
                    SearchView(viewModel = mViewModel, queryStartTime = queryStartTime,
                        setTimezoneIDCity = { timezoneId, city ->
                            mSettings.timezoneId = timezoneId
                            mSettings.cityName = city
                            mViewModel.resetDbCache()
                            refreshTile()
                            navController.popBackStack("main", false)
                        })
                }
                composable("select_std_timezone") {
                    StdTimezoneSelectionView(
                        viewModel = mViewModel, setTimezoneIDCity = { timezoneId, city ->
                            mSettings.timezoneId = timezoneId
                            mSettings.cityName = city
                            mViewModel.resetDbCache()
                            refreshTile()
                            navController.popBackStack("main", false)
                        }, queryStartTime = queryStartTime
                    )
                }
                composable("select_country") {
                    CountrySelectionView(viewModel = mViewModel, continent = state.selectedContinent,
                        openProvinces = { country, denomination ->
                            mViewModel.selectCountry(
                                country, provincesDenomination = denomination
                            )
                            navController.navigate("select_province")
                        }, openCities = { country, province ->
                            mViewModel.selectCountry(
                                country, province = province
                            )
                            navController.navigate("select_city")
                        }, queryStartTime = queryStartTime
                    )
                }
                composable("select_province") {
                    ProvinceSelectionView(
                        viewModel = mViewModel, country = state.selectedCountry,
                        provincesDenomination = state.provincesDenomination, openCities = { country, province ->
                            mViewModel.selectProvince(country, province)
                            navController.navigate("select_city")
                        }, queryStartTime = queryStartTime
                    )
                }
                composable("select_city") {
                    CitySelectionView(
                        viewModel = mViewModel, country = state.selectedCountry, province = state.selectedProvince,
                        setTimezoneIDCity = { timezoneId, city ->
                            mSettings.timezoneId = timezoneId
                            mSettings.cityName = city
                            mViewModel.resetDbCache()
                            refreshTile()
                            navController.popBackStack("main", false)
                        }, queryStartTime = queryStartTime
                    )
                }
                composable("about") {
                    AboutView()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        refreshTile()
        finish() // Force a brand new activity on resume
    }
}

@Composable
fun ColorIcon(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    appColorScheme: ColorScheme,
) {
    val context = LocalContext.current
    val iconColor by remember { derivedStateOf { colorScheme.getColor(context).composeColor } }
    val appColorSeed by remember { derivedStateOf { appColorScheme.getColor(context).composeColor } }
    val appPalette = dynamicDarkColorPalette(appColorSeed)
    val colorName: String by remember {
        derivedStateOf { colorScheme.getName(context) }
    }

    Icon(
        modifier = modifier.border(
            3.dp, shape = CircleShape, color = appPalette.onPrimary
        ), imageVector = ImageVector.vectorResource(R.drawable.color_preview), contentDescription = "Color: $colorName",
        tint = iconColor
    )
}

@Composable
fun TilePreviewModule(viewModel: TileSettingsViewModel, previewTime: LocalDateTime? = null) {
    val state by viewModel.state.collectAsState()
    val colorScheme = state.colorScheme
    val context = LocalContext.current
    val color by remember { derivedStateOf { colorScheme.getColor(context).composeColor } }
    val colorLight by remember { derivedStateOf { colorScheme.getColorLight(context).composeColor } }
    val timezoneId by remember { derivedStateOf { state.timezoneId ?: TimeZone.getDefault().id } }

    var currentTime by remember { mutableStateOf(previewTime ?: currentTimeAt(timezoneId).reducedPrecision) }
    if (previewTime == null) {
        LaunchedEffect(Unit) {
            while (true) {
                currentTime = currentTimeAt(timezoneId).reducedPrecision
                delay(1000)
            }
        }
    }

    return Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp), text = state.cityName ?: "Current location",
            color = colorLight, maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = if (state.time24h) {
                    currentTime.format(DateTimeFormatter.ofPattern("H:mm"))
                } else {
                    currentTime.format(DateTimeFormatter.ofPattern("h:mm"))
                },
                style = MaterialTheme.typography.title1.copy(
                    fontSize = 48.sp, color = color, fontWeight = FontWeight.Bold
                ),
            )
            if (!state.time24h) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = if (currentTime.hour < 12) {
                        "AM"
                    } else {
                        "PM"
                    },
                    style = MaterialTheme.typography.title1.copy(
                        fontSize = 24.sp, color = color
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Clip,

                    )
            }
        }
        Text(text = timezoneOffsetDescription(timezoneId), color = colorLight)
    }
}

@Composable
fun MainSettingsView(
    viewModel: TileSettingsViewModel,
    set24Hour: (Boolean) -> Unit,
    openColorSelection: () -> Unit,
    openTimeZoneSelection: () -> Unit,
    openAbout: () -> Unit,
    openTileManagement: () -> Unit,
    renameCity: (String) -> Unit,
    previewTime: LocalDateTime? = null,
) {
    val listState = rememberScalingLazyListState(initialCenterItemIndex = 0)
    val state by viewModel.state.collectAsState()
    var showRenameDialog by remember { mutableStateOf(false) }

    val itemsModifier = Modifier.padding(horizontal = 10.dp)

    MainView(listState) {
        ScalingLazyColumnWithRSB(
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 0),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp, alignment = Alignment.Top
            ),
            anchorType = ScalingLazyListAnchorType.ItemCenter,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item("preview") {
                TilePreviewModule(viewModel = viewModel, previewTime = previewTime)
            }
            item("spacer1") { Spacer(modifier = itemsModifier.height(8.dp)) }
            item("caret") {
                Row(
                    modifier = itemsModifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_caret_down),
                        contentDescription = "Caret", tint = MaterialTheme.colors.onSurface
                    )
                }
            }
            item("spacer2") { Spacer(modifier = itemsModifier.height(8.dp)) }
            item("heading") { Text("Settings") }
            item("spacer3") { Spacer(modifier = itemsModifier) }
            item("nameButton") {
                Chip(modifier = itemsModifier.fillMaxWidth(), label = { Text("Name") }, secondaryLabel = {
                    Text(
                        text = state.cityName ?: "[not set]", maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }, onClick = { showRenameDialog = true }, colors = themedChipColors { state.colorScheme }, icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_label), contentDescription = "Name"
                    )
                })
            }
            item("timezoneButton") {
                Chip(modifier = itemsModifier.fillMaxWidth(), label = { Text("Time zone") }, secondaryLabel = {
                    if (state.timezoneId != null) Text(
                        timezoneSimpleNames[state.timezoneId] ?: state.timezoneId!!, maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }, onClick = openTimeZoneSelection, colors = themedChipColors { state.colorScheme }, icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_world), contentDescription = "Time zone"
                    )
                })
            }
            item("colorButton") {
                Chip(modifier = itemsModifier.fillMaxWidth(), label = { Text("Color") }, secondaryLabel = {
                    val context = LocalContext.current
                    val colorName by remember {
                        derivedStateOf {
                            state.colorScheme.getName(
                                context
                            )
                        }
                    }

                    Text(colorName)
                }, onClick = openColorSelection, colors = themedChipColors { state.colorScheme }, icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_palette), contentDescription = "Color"
                    )
                })
            }

            item("24hourButton") {
                val context = LocalContext.current
                val themeColor by remember { derivedStateOf { state.colorScheme.getColor(context).composeColor } }
                val palette = dynamicDarkColorPalette(themeColor)

                ToggleChip(modifier = itemsModifier.fillMaxWidth(), checked = state.time24h,
                    onCheckedChange = { set24Hour(it) }, label = { Text("24 hour time") },
                    colors = toggleChipColors(colorScheme = state.colorScheme), toggleControl = {
                        Switch(
                            checked = state.time24h, onCheckedChange = { set24Hour(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = palette.primary,
                                checkedTrackColor = palette.primary.copy(alpha = 0.5f),
                                uncheckedThumbColor = palette.onSurface.copy(alpha = 0.6f),
                                uncheckedTrackColor = palette.onSurface.copy(alpha = 0.6f * ContentAlpha.disabled),
                            )
                        )
                    })
            }

            item("spacer4") { Spacer(modifier = itemsModifier.height(8.dp)) }

            item("manageTilesButton") {
                Chip(modifier = itemsModifier.fillMaxWidth(), label = { Text("All cities…") },
                    onClick = openTileManagement, colors = themedChipColors { state.colorScheme }, icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_globe),
                            contentDescription = "View all cities"
                        )
                    })
            }

            item("spacer5") { Spacer(modifier = itemsModifier.height(8.dp)) }

            item("aboutButton") {
                Row(
                    modifier = itemsModifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
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

    TextInputDialog(showDialog = showRenameDialog, title = { Text("Set city name") }, inputLabel = "Name",
        initialValue = { state.cityName ?: "" }, keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
        ), onSubmit = { name ->
            renameCity(name)
            showRenameDialog = false
        }, dismissDialog = { showRenameDialog = false }, colorScheme = state.colorScheme
    )
}

@Composable
fun ColorSelectionView(setColorScheme: (ColorScheme) -> Unit, selectedColor: ColorScheme) {
    val listState = rememberScalingLazyListState(
        initialCenterItemIndex = ColorScheme.entries.indexOf(selectedColor) + 2
    )

    var boxOpacity by remember { mutableStateOf(0f) }
    var doSmoothRedraw by remember { mutableStateOf(false) }
    var keySuffix by remember { mutableIntStateOf(0) }
    var refreshScrollPosition by remember { mutableIntStateOf(0) }

    // We try to hide the above procedure by fading the list in and out
    LaunchedEffect(doSmoothRedraw) {
        if (doSmoothRedraw) {
            try {
                val animationSpec = spring<Float>()

                // Fade to black
                boxOpacity = 0f
                animate(initialValue = 0f, targetValue = 1f, animationSpec = animationSpec) { value, _ ->
                    boxOpacity = value
                }

                // Refill list
                keySuffix++

                // Wait for that to finish
                withTimeout(1000) {
                    val itemInfo = listState.layoutInfo.visibleItemsInfo
                    do {
                        delay(100)
                    } while (itemInfo.isEmpty())
                }

                println("scrolling to $refreshScrollPosition")
                listState.scrollToItem(refreshScrollPosition)

                // Wait a little more for scrolling to finish
                delay(100)

                // Fade back to normal
                animate(initialValue = 1f, targetValue = 0f, animationSpec = animationSpec) { value, _ ->
                    boxOpacity = value
                }
            } finally {
                boxOpacity = 0f
                doSmoothRedraw = false
            }
        }
    }

    MainView(listState) {
        Box {
            ScalingLazyColumnWithRSB(
                state = listState,
                autoCentering = AutoCenteringParams(itemIndex = 1),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    space = 4.dp, alignment = Alignment.Top
                ),
                anchorType = ScalingLazyListAnchorType.ItemCenter,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item("heading") { Text("Colors") }
                item("spacer") { Spacer(modifier = Modifier) }

                items(items = ColorScheme.entries.toTypedArray(), key = { "${it.name}${keySuffix}" }) { colorScheme ->
                    val isCurrentColor = remember(colorScheme, selectedColor) {
                        colorScheme == selectedColor
                    }

                    Chip(modifier = Modifier.fillMaxWidth(),
                        label = { Text(colorScheme.getName(LocalContext.current)) }, onClick = {
                            setColorScheme(colorScheme)
                            refreshScrollPosition = listState.centerItemIndex
                            doSmoothRedraw = true
                        }, colors = chipGradientColors(
                            checked = isCurrentColor, colorScheme = selectedColor
                        ), icon = {
                            ColorIcon(
                                modifier = Modifier.size(20.dp), colorScheme, selectedColor
                            )
                        })
                }
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = boxOpacity))
            ) {}
        }
    }
}

@Composable
fun ContinentSelectionView(
    viewModel: TileSettingsViewModel,
    openStdTimeZoneSelection: () -> Unit,
    openSearch: () -> Unit,
    selectContinent: (String) -> Unit,
    queryStartTime: Long = 0L,
) {
    val listState = rememberForeverScalingLazyListState(
        key = "continentSelection", params = queryStartTime, initialCenterItemIndex = 0
    )
    val state by viewModel.state.collectAsState()
    val continents by viewModel.getContinents().collectAsState(initial = viewModel.getContinentsCache())

    LaunchedEffect(continents) {
        viewModel.cacheContinents(continents)
    }

    MainView(listState) {
        ScalingLazyColumnWithRSB(
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 0),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp, alignment = Alignment.Top
            ),
            anchorType = ScalingLazyListAnchorType.ItemCenter,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item("searchButton") {
                Chip(modifier = Modifier.fillMaxWidth(), label = { Text("Search") }, onClick = { openSearch() },
                    colors = themedChipColors { state.colorScheme }, icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_world_search),
                            contentDescription = "Search"
                        )
                    })
            }
            item("stdTimezoneButton") {
                Chip(modifier = Modifier.fillMaxWidth(), label = { Text("Standard time zones") },
                    onClick = { openStdTimeZoneSelection() }, colors = themedChipColors { state.colorScheme }, icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_world),
                            contentDescription = "Standard timezones"
                        )
                    })
            }
            item("spacer") { Spacer(modifier = Modifier) }
            item("heading") { Text("Continents") }

            items(continents, key = { it }) {
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                    onClick = { selectContinent(it) },
                    colors = themedChipColors { state.colorScheme },
                )
            }
        }
    }
}

@Composable
fun StdTimezoneSelectionView(
    viewModel: TileSettingsViewModel,
    queryStartTime: Long = 0L,
    setTimezoneIDCity: (String, String) -> Unit,
) {
    val listState = rememberForeverScalingLazyListState("stdTimezoneSelection", queryStartTime)
    val state by viewModel.state.collectAsState()
    val stdTimezones by remember {
        derivedStateOf {
            mutableListOf<String>().apply {
                addAll((-12..14).map { "UTC${if (it >= 0) "+" else ""}$it" })
                replaceAll { if (it == "UTC+0") "UTC" else it }
                addAll(
                    listOf(
                        "CET",
                        "EET",
                        "EST",
                        "GMT",
                        "HST",
                        "MET",
                        "MST",
                        "UCT",
                        "WET",
                    )
                )
            }
        }
    }

    MainView(listState) {
        ScalingLazyColumnWithRSB(
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 1),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp, alignment = Alignment.Top
            ),
            anchorType = ScalingLazyListAnchorType.ItemCenter,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item("heading") { Text("Standard time zones") }
            item("spacer") { Spacer(modifier = Modifier) }

            items(stdTimezones, key = { it }) {
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(it) },
                    onClick = {
                        setTimezoneIDCity(if (it.startsWith("GMT")) "Etc/$it" else it, it)
                    },
                    colors = themedChipColors { state.colorScheme },
                )
            }
        }
    }
}

@Composable
fun CountrySelectionView(
    viewModel: TileSettingsViewModel,
    continent: String,
    openProvinces: (String, String) -> Unit,
    openCities: (String, String) -> Unit,
    queryStartTime: Long = 0L,
) {
    val listState = rememberForeverScalingLazyListState(
        key = "countrySelection", params = queryStartTime
    )
    val state by viewModel.state.collectAsState()
    val countries by viewModel
        .getCountriesInContinent(continent)
        .collectAsState(initial = viewModel.getCountriesCache(continent))
    LaunchedEffect(countries) {
        viewModel.cacheCountries(countries, continent)
    }

    MainView(listState) {
        ScalingLazyColumnWithRSB(
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 1),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp, alignment = Alignment.Top
            ),
            anchorType = ScalingLazyListAnchorType.ItemCenter,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item("heading") { Text("Countries in $continent", textAlign = TextAlign.Center) }
            item("spacer") { Spacer(modifier = Modifier) }

            items(countries, key = { it.country }) {
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(it.country, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                    onClick = {
                        if (it.provincesDenomination.isEmpty()) {
                            openCities(it.country, "")
                        } else {
                            openProvinces(it.country, it.provincesDenomination)
                        }
                    },
                    colors = themedChipColors { state.colorScheme },
                )
            }
        }
    }
}

@Composable
fun ProvinceSelectionView(
    viewModel: TileSettingsViewModel,
    country: String,
    provincesDenomination: String,
    queryStartTime: Long = 0L,
    openCities: (String, String) -> Unit,
) {
    val listState = rememberForeverScalingLazyListState(
        key = "provinceSelection", params = queryStartTime
    )
    val state by viewModel.state.collectAsState()
    val provinces by viewModel
        .getProvincesInCountry(country)
        .collectAsState(initial = viewModel.getProvincesCache(country))
    LaunchedEffect(provinces) {
        viewModel.cacheProvinces(provinces, country)
    }

    MainView(listState) {
        ScalingLazyColumnWithRSB(
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 1),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp, alignment = Alignment.Top
            ),
            anchorType = ScalingLazyListAnchorType.ItemCenter,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item("heading") {
                @Suppress("DEPRECATION") Text(
                    "${provincesDenomination.capitalize(Locale.ENGLISH)} in $country", textAlign = TextAlign.Center
                )
            }
            item("spacer") { Spacer(modifier = Modifier) }

            items(provinces, key = { it }) {
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                    onClick = { openCities(country, it) },
                    colors = themedChipColors { state.colorScheme },
                )
            }
        }
    }
}

@Composable
fun CitySelectionView(
    viewModel: TileSettingsViewModel,
    country: String,
    province: String,
    queryStartTime: Long = 0L,
    setTimezoneIDCity: (String, String) -> Unit,
) {
    val listState = rememberForeverScalingLazyListState(
        key = "citySelection", params = queryStartTime
    )
    val state by viewModel.state.collectAsState()
    val cities by viewModel
        .getCitiesInProvince(country, province)
        .collectAsState(initial = viewModel.getCitiesCache(country, province))
    LaunchedEffect(cities) {
        viewModel.cacheCities(cities, country, province)
    }

    MainView(listState) {
        ScalingLazyColumnWithRSB(
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 1),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp, alignment = Alignment.Top
            ),
            anchorType = ScalingLazyListAnchorType.ItemCenter,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item("heading") {
                if (province.isEmpty()) Text("Cities in $country", textAlign = TextAlign.Center)
                else Text("Cities in $province", textAlign = TextAlign.Center)
            }
            item("spacer") { Spacer(modifier = Modifier) }

            items(cities, key = { it.rowid }) {
                val localTimeStr by remember {
                    derivedStateOf {
                        currentTimeAt(it.timezone).let {
                            if (state.time24h) it.format(DateTimeFormatter.ofPattern("H:mm"))
                            else it.format(DateTimeFormatter.ofPattern("h:mm a"))
                        }
                    }
                }

                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(it.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    secondaryLabel = {
                        Text(
                            "${localTimeStr}, ${it.timezoneName}", maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = { setTimezoneIDCity(it.timezone, it.cityName) },
                    colors = themedChipColors { state.colorScheme },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchView(
    viewModel: TileSettingsViewModel,
    queryStartTime: Long = 0L,
    initialQuery: String = "",
    setTimezoneIDCity: (String, String) -> Unit,
) {
    val listState = rememberForeverScalingLazyListState(
        key = "search", params = queryStartTime
    )
    var query by remember { mutableStateOf(initialQuery) }
    val state by viewModel.state.collectAsState()
    val results by viewModel.searchCities(query).collectAsState(initial = emptyList())
    val context = LocalContext.current

    MainView(listState) {
        ScalingLazyColumnWithRSB(
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 1),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp, alignment = Alignment.Top
            ),
            anchorType = ScalingLazyListAnchorType.ItemCenter,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item("searchInput") {
                val themeColor by remember { derivedStateOf { state.colorScheme.getColor(context).composeColor } }
                val keyboardController = LocalSoftwareKeyboardController.current

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search, keyboardType = KeyboardType.Ascii
                    ),
                    keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                    singleLine = true,
                    shape = RoundedCornerShape(TextFieldDefaults.MinHeight / 2),
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeColor,
                        cursorColor = themeColor,
                        selectionColors = TextSelectionColors(
                            backgroundColor = themeColor.copy(alpha = 0.3f),
                            handleColor = themeColor,
                        ),
                        focusedTextColor = MaterialTheme.colors.onSurface,
                        unfocusedTextColor = MaterialTheme.colors.onSurface,
                    ),
                )
            }
            item("spacer") { Spacer(modifier = Modifier) }

            items(results, key = { it.rowid }) {
                val localTimeStr by remember {
                    derivedStateOf {
                        currentTimeAt(it.timezone).let {
                            if (state.time24h) it.format(DateTimeFormatter.ofPattern("H:mm"))
                            else it.format(DateTimeFormatter.ofPattern("h:mm a"))
                        }
                    }
                }

                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(it.cityName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    secondaryLabel = {
                        Text(
                            "${localTimeStr}, ${it.timezoneName}", maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = { setTimezoneIDCity(it.timezone, it.cityName) },
                    colors = themedChipColors { state.colorScheme },
                )
            }
        }
    }
}

fun previewViewModel(context: Context): TileSettingsViewModel {
    val dao = try {
        val contextWrapper = PreviewContextWrapper(context)
        val db =
            Room.inMemoryDatabaseBuilder(contextWrapper, TimezoneDatabase::class.java).allowMainThreadQueries().build()
        val dbDao = db.getTimezoneDao()
        populateWithSampleData(dbDao)
        dbDao
    } catch (e: Exception) {
        e.printStackTrace()
        DbLessSampleDataDao()
    }

    return TileSettingsViewModel(dao).apply {
        setState(
            TileSettingsState(
                cityName = "Awa'atlu, Pandora", colorScheme = ColorScheme.Default, time24h = false,
                timezoneId = "Asia/Manila"
            )
        )
    }
}

@Composable
fun MainSettingsPreviewBase(previewTime: LocalDateTime? = null) {
    val context = LocalContext.current
    val viewModel = remember { previewViewModel(context) }
    val state by viewModel.state.collectAsState()

    MainSettingsView(viewModel, {
        viewModel.setState(state.copy(time24h = it))
    }, {}, {}, {}, {}, {
        viewModel.setState(state.copy(cityName = it))
    }, previewTime)
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun MainSettingsPreviewSmallRound() {
    MainSettingsPreviewBase(LocalDateTime.of(2023, 12, 31, 0, 59, 59))
}

@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Composable
fun MainSettingsPreviewLargeRound() {
    MainSettingsPreviewBase(LocalDateTime.of(2023, 12, 31, 0, 59, 59))
}

@Preview(device = WearDevices.SQUARE, showSystemUi = true)
@Composable
fun MainSettingsPreviewSquare() {
    MainSettingsPreviewBase(LocalDateTime.of(2023, 12, 31, 0, 59, 59))
}

@Preview(device = WearDevices.RECT, showSystemUi = true)
@Composable
fun MainSettingsPreviewRect() {
    MainSettingsPreviewBase(LocalDateTime.of(2023, 12, 31, 0, 59, 59))
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ColorSelectionPreview() {
    var colorScheme by remember { mutableStateOf(ColorScheme.Default) }
    ColorSelectionView({ colorScheme = it }, colorScheme)
}


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ContinentSelectionPreview() {
    val context = LocalContext.current
    val viewModel = remember { previewViewModel(context) }
    ContinentSelectionView(viewModel, {}, {}, {})
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun StdTimezoneSelectionPreview() {
    val context = LocalContext.current
    val viewModel = remember { previewViewModel(context) }
    StdTimezoneSelectionView(viewModel) { _, _ -> }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun CountrySelectionPreview() {
    val context = LocalContext.current
    val viewModel = remember { previewViewModel(context) }
    CountrySelectionView(viewModel, "Asia", { _, _ -> }, { _, _ -> })
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ProvinceSelectionPreview() {
    val context = LocalContext.current
    val viewModel = remember { previewViewModel(context) }
    ProvinceSelectionView(viewModel, "Italy", "regions") { _, _ -> }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun CitySelectionPreview() {
    val context = LocalContext.current
    val viewModel = remember { previewViewModel(context) }
    CitySelectionView(viewModel, "Italy", "Lombardy") { _, _ -> }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun SearchResultsSelectPreview() {
    val context = LocalContext.current
    val viewModel = remember { previewViewModel(context) }
    SearchView(viewModel, initialQuery = "strawberries") { _, _ -> }
}
