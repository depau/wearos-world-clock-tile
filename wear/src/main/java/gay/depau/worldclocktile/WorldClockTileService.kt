package gay.depau.worldclocktile

// SPDX-License-Identifier: GNU GPLv3
// This file is part of World Clock Tile.

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.protolayout.ActionBuilders.AndroidActivity
import androidx.wear.protolayout.ActionBuilders.LaunchAction
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders.Box
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.FONT_WEIGHT_BOLD
import androidx.wear.protolayout.LayoutElementBuilders.FontStyle
import androidx.wear.protolayout.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.protolayout.LayoutElementBuilders.Layout
import androidx.wear.protolayout.LayoutElementBuilders.SpanText
import androidx.wear.protolayout.LayoutElementBuilders.Spannable
import androidx.wear.protolayout.LayoutElementBuilders.TEXT_OVERFLOW_ELLIPSIZE_END
import androidx.wear.protolayout.LayoutElementBuilders.Text
import androidx.wear.protolayout.LayoutElementBuilders.TextOverflowProp
import androidx.wear.protolayout.LayoutElementBuilders.VERTICAL_ALIGN_CENTER
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ModifiersBuilders.Modifiers
import androidx.wear.protolayout.ModifiersBuilders.Padding
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders.TimeInterval
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.TimelineBuilders.TimelineEntry
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import gay.depau.worldclocktile.composables.TilePreview
import gay.depau.worldclocktile.presentation.TileSettingsActivity
import gay.depau.worldclocktile.shared.MAX_TILE_ID
import gay.depau.worldclocktile.shared.TileSettings
import gay.depau.worldclocktile.shared.utils.ColorScheme
import gay.depau.worldclocktile.shared.utils.currentTimeAt
import gay.depau.worldclocktile.shared.utils.getComponentEnabled
import gay.depau.worldclocktile.shared.utils.setComponentEnabled
import gay.depau.worldclocktile.shared.utils.timezoneOffsetDescription
import gay.depau.worldclocktile.utils.getColorProp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone


private const val RESOURCES_VERSION = "1"
private const val PRERENDER_MINUTES = 60

// Feel free to empty this map if you really want to see the time in these countries.
// This is a protest against the human rights violations in these countries.
// No, the list is not exhaustive in any sort of way. What-about-isms not welcome.
private val hostileCountries = mapOf(
    "Russia" to "Time to get out of Ukraine",
    "Afghanistan" to "Time to stop killing women & LGBTI+",
    "Iran" to "Time to stop killing women & LGBTI+",
)

fun Context.timeLayout(
    cityName: String,
    time: LocalTime?,
    offset: String,
    view24h: Boolean,
    colorScheme: ColorScheme,
    hostileText: String? = null,
): Box.Builder {
    val amPmString = if (time == null) " __" else if (time.hour < 12) " AM" else " PM"
    val timeString = if (view24h) time?.format(DateTimeFormatter.ofPattern("H:mm")) ?: "__:__"
    else time?.format(DateTimeFormatter.ofPattern("h:mm")) ?: "__:__"

    val timeColor = ColorBuilders.ColorProp.Builder(colorScheme.getColor(this).toArgb().run {
        if (time != null || hostileText != null) this
        else this and 0x00FFFFFF or 0x44000000
    }).build()

    val otherColor = ColorBuilders.ColorProp.Builder(colorScheme.getColorLight(this).toArgb().run {
        if (time != null || hostileText != null) this
        else this and 0x00FFFFFF or 0x77000000
    }).build()

    return Box
        .Builder()
        .setVerticalAlignment(VERTICAL_ALIGN_CENTER)
        .setWidth(DimensionBuilders.expand())
        .setHeight(DimensionBuilders.expand())
        .addContent(
            Column
                .Builder()
                .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
                .setWidth(DimensionBuilders.expand())
                .addContent(
                    Text.Builder().setMaxLines(1).setModifiers(
                        Modifiers.Builder().setPadding(
                            Padding.Builder().setStart(dp(16F)).setEnd(dp(16F)).build()
                        ).build()
                    ).setFontStyle(
                        FontStyle.Builder().setColor(otherColor).build()
                    ).setOverflow(
                        TextOverflowProp.Builder().setValue(TEXT_OVERFLOW_ELLIPSIZE_END).build()
                    ).setText(cityName).build()
                )
                .addContent(
                    if (hostileText == null) {
                        Spannable.Builder().addSpan(
                            SpanText.Builder().setText(timeString).setFontStyle(
                                FontStyle
                                    .Builder()
                                    .setColor(timeColor)
                                    .setWeight(FONT_WEIGHT_BOLD)
                                    .setSize(sp(48f))
                                    .build()
                            ).build()
                        ).apply {
                            if (!view24h) addSpan(
                                SpanText.Builder().setText(amPmString).setFontStyle(
                                    FontStyle.Builder().setColor(timeColor).setSize(sp(24f)).build()
                                ).build()
                            )
                        }.build()
                    } else {
                        Text.Builder().setText(hostileText).setMaxLines(3).setModifiers(
                            Modifiers.Builder().setPadding(
                                Padding.Builder().setAll(dp(8F)).build()
                            ).build()
                        ).setFontStyle(
                            FontStyle.Builder().setColor(colorScheme.getColorProp(this)).setSize(sp(24f)).build()
                        ).build()
                    }
                )
                .addContent(
                    Text.Builder().setFontStyle(
                        FontStyle.Builder().setColor(otherColor).build()
                    ).setText(offset).build()
                )
                .build()
        )
}


@OptIn(ExperimentalHorologistApi::class)
abstract class WorldClockTileService(private val tileId: Int) : SuspendingTileService() {
    private lateinit var mSettings: TileSettings

    override fun onCreate() {
        super.onCreate()
        mSettings = TileSettings(this, tileId)
    }

    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): Tile {
        assert(PRERENDER_MINUTES > 2)

        val locationName = mSettings.cityName ?: "Current location"
        val localTimeAtLocation = currentTimeAt(mSettings.timezoneId ?: TimeZone.getDefault().id)
        val curTimeMillis = System.currentTimeMillis() - localTimeAtLocation.second * 1000L

        val hostileText = hostileCountries.keys.find { locationName.endsWith(it) }?.let {
            hostileCountries[it]
        }

        val modifiers = Modifiers.Builder().setClickable(
            Clickable.Builder().setId("tile${tileId}").setOnClick(
                LaunchAction.Builder().setAndroidActivity(
                    AndroidActivity
                        .Builder()
                        .setClassName(TileSettingsActivity::class.java.name)
                        .setPackageName(packageName)
                        .build()
                ).build()
            ).build()
        ).build()

        return Tile
            .Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setFreshnessIntervalMillis((PRERENDER_MINUTES - 1) * 60 * 1000L)
            .setTileTimeline(
                Timeline.Builder().addTimelineEntry(
                    TimelineEntry.Builder().setLayout(
                        Layout.Builder().setRoot(
                            timeLayout(
                                locationName, null, timezoneOffsetDescription(
                                    mSettings.timezoneId ?: TimeZone.getDefault().id, hostile = hostileText != null
                                ), mSettings.time24h, mSettings.colorScheme, hostileText = hostileText
                            ).setModifiers(modifiers).build()
                        ).build()
                    ).build()
                ).apply {
                    for (i in 0 until PRERENDER_MINUTES) {
                        val offset = timezoneOffsetDescription(
                            mSettings.timezoneId ?: TimeZone.getDefault().id, minutesOffset = i,
                            hostile = hostileText != null
                        )

                        addTimelineEntry(
                            TimelineEntry.Builder().setLayout(
                                Layout.Builder().setRoot(
                                    timeLayout(
                                        locationName, localTimeAtLocation.plusMinutes(i.toLong()).toLocalTime(), offset,
                                        mSettings.time24h, mSettings.colorScheme, hostileText = hostileText
                                    ).setModifiers(modifiers).build()
                                ).build()
                            ).setValidity(
                                TimeInterval
                                    .Builder()
                                    .setStartMillis(curTimeMillis + i * 60 * 1000L)
                                    .setEndMillis(curTimeMillis + (i + 1) * 60 * 1000L)
                                    .build()
                            ).build()
                        )
                    }
                }.build()
            )
            // create your tile here
            .build()
    }

    override suspend fun resourcesRequest(requestParams: ResourcesRequest): ResourceBuilders.Resources =
        ResourceBuilders.Resources.Builder().setVersion(RESOURCES_VERSION).build()


    companion object {
        @JvmStatic
        private fun getTileClass(tileId: Int) = when (tileId) {
            0 -> WorldClockTileService0::class
            1 -> WorldClockTileService1::class
            2 -> WorldClockTileService2::class
            3 -> WorldClockTileService3::class
            4 -> WorldClockTileService4::class
            5 -> WorldClockTileService5::class
            6 -> WorldClockTileService6::class
            7 -> WorldClockTileService7::class
            8 -> WorldClockTileService8::class
            9 -> WorldClockTileService9::class
            10 -> WorldClockTileService10::class
            11 -> WorldClockTileService11::class
            12 -> WorldClockTileService12::class
            13 -> WorldClockTileService13::class
            14 -> WorldClockTileService14::class
            15 -> WorldClockTileService15::class
            16 -> WorldClockTileService16::class
            17 -> WorldClockTileService17::class
            18 -> WorldClockTileService18::class
            19 -> WorldClockTileService19::class
            20 -> WorldClockTileService20::class
            21 -> WorldClockTileService21::class
            22 -> WorldClockTileService22::class
            23 -> WorldClockTileService23::class
            24 -> WorldClockTileService24::class
            25 -> WorldClockTileService25::class
            26 -> WorldClockTileService26::class
            27 -> WorldClockTileService27::class
            28 -> WorldClockTileService28::class
            29 -> WorldClockTileService29::class
            30 -> WorldClockTileService30::class
            31 -> WorldClockTileService31::class
            32 -> WorldClockTileService32::class
            33 -> WorldClockTileService33::class
            34 -> WorldClockTileService34::class
            35 -> WorldClockTileService35::class
            36 -> WorldClockTileService36::class
            37 -> WorldClockTileService37::class
            38 -> WorldClockTileService38::class
            39 -> WorldClockTileService39::class
            40 -> WorldClockTileService40::class
            41 -> WorldClockTileService41::class
            42 -> WorldClockTileService42::class
            43 -> WorldClockTileService43::class
            44 -> WorldClockTileService44::class
            45 -> WorldClockTileService45::class
            46 -> WorldClockTileService46::class
            47 -> WorldClockTileService47::class
            48 -> WorldClockTileService48::class
            49 -> WorldClockTileService49::class

            else -> throw IllegalArgumentException("Invalid tile id")
        }

        @JvmStatic
        fun requestUpdate(context: Context, tileId: Int) = getUpdater(context).requestUpdate(
            getTileClass(tileId).java,
        )

        @JvmStatic
        fun isTileEnabled(context: Context, tileId: Int): Boolean {
            assert(tileId in 0..MAX_TILE_ID)
            val state = getTileClass(tileId).getComponentEnabled(context)
            if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) return true
            // Tile 0 is enabled by default in the manifest
            val enabled = tileId == 0 && state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT

            val settings = TileSettings(context, tileId)
            if (settings.enabled != enabled) settings.enabled = enabled

            return enabled
        }

        @JvmStatic
        fun setTileEnabled(context: Context, tileId: Int, enabled: Boolean) {
            assert(tileId in 0..MAX_TILE_ID)
            getTileClass(tileId).setComponentEnabled(context, enabled)

            TileSettings(context, tileId).enabled = enabled
        }
    }
}

// Unfortunately there's no way to dynamically define tiles.
// We can however define a lot of them and disable them by default.

class WorldClockTileService0 : WorldClockTileService(0)

class WorldClockTileService1 : WorldClockTileService(1)

class WorldClockTileService2 : WorldClockTileService(2)

class WorldClockTileService3 : WorldClockTileService(3)

class WorldClockTileService4 : WorldClockTileService(4)

class WorldClockTileService5 : WorldClockTileService(5)

class WorldClockTileService6 : WorldClockTileService(6)

class WorldClockTileService7 : WorldClockTileService(7)

class WorldClockTileService8 : WorldClockTileService(8)

class WorldClockTileService9 : WorldClockTileService(9)

class WorldClockTileService10 : WorldClockTileService(10)

class WorldClockTileService11 : WorldClockTileService(11)

class WorldClockTileService12 : WorldClockTileService(12)

class WorldClockTileService13 : WorldClockTileService(13)

class WorldClockTileService14 : WorldClockTileService(14)

class WorldClockTileService15 : WorldClockTileService(15)

class WorldClockTileService16 : WorldClockTileService(16)

class WorldClockTileService17 : WorldClockTileService(17)

class WorldClockTileService18 : WorldClockTileService(18)

class WorldClockTileService19 : WorldClockTileService(19)

class WorldClockTileService20 : WorldClockTileService(20)

class WorldClockTileService21 : WorldClockTileService(21)

class WorldClockTileService22 : WorldClockTileService(22)

class WorldClockTileService23 : WorldClockTileService(23)

class WorldClockTileService24 : WorldClockTileService(24)

class WorldClockTileService25 : WorldClockTileService(25)

class WorldClockTileService26 : WorldClockTileService(26)

class WorldClockTileService27 : WorldClockTileService(27)

class WorldClockTileService28 : WorldClockTileService(28)

class WorldClockTileService29 : WorldClockTileService(29)

class WorldClockTileService30 : WorldClockTileService(30)

class WorldClockTileService31 : WorldClockTileService(31)

class WorldClockTileService32 : WorldClockTileService(32)

class WorldClockTileService33 : WorldClockTileService(33)

class WorldClockTileService34 : WorldClockTileService(34)

class WorldClockTileService35 : WorldClockTileService(35)

class WorldClockTileService36 : WorldClockTileService(36)

class WorldClockTileService37 : WorldClockTileService(37)

class WorldClockTileService38 : WorldClockTileService(38)

class WorldClockTileService39 : WorldClockTileService(39)

class WorldClockTileService40 : WorldClockTileService(40)

class WorldClockTileService41 : WorldClockTileService(41)

class WorldClockTileService42 : WorldClockTileService(42)

class WorldClockTileService43 : WorldClockTileService(43)

class WorldClockTileService44 : WorldClockTileService(44)

class WorldClockTileService45 : WorldClockTileService(45)

class WorldClockTileService46 : WorldClockTileService(46)

class WorldClockTileService47 : WorldClockTileService(47)

class WorldClockTileService48 : WorldClockTileService(48)

class WorldClockTileService49 : WorldClockTileService(49)


@Preview(
    name = "Empty Layout",
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Small Round",
)
@Composable
fun EmptyLayoutPreview() {
    TilePreview {
        timeLayout(
            "Manila, Philippines", null, "+7 Tomorrow", false, ColorScheme.Default
        ).build()
    }
}

@Preview(
    name = "Time in Russia",
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Small Round",
)
@Composable
fun TimeInRussiaPreview() {
    TilePreview {
        timeLayout(
            "Moscow, Russia", null, "-2 centuries", true, ColorScheme.Default,
            hostileCountries["Russia"] ?: "Be glad you're not cannon fodder"
        ).build()
    }
}

@Preview(
    name = "Time in Iran",
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Small Round",
)
@Composable
fun TimeInIranPreview() {
    TilePreview {
        timeLayout(
            "Tehran, Iran", null, "-2 centuries", true, ColorScheme.Default,
            hostileCountries["Iran"] ?: "Be glad they haven't killed you"
        ).build()
    }
}

@Preview(
    name = "Time Layout",
    device = WearDevices.SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Small Round",
)
@Composable
fun TimeLayoutPreview() {
    TilePreview {
        timeLayout(
            "Awa'atlu, Pandora", LocalTime.of(16, 20), "+7 Tomorrow", false, ColorScheme.Default
        ).build()
    }
}
