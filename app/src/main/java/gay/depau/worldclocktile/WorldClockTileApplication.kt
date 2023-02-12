package gay.depau.worldclocktile

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.app.Application
import android.content.Context
import gay.depau.worldclocktile.tzdb.TimezoneDatabase

class WorldClockTileApplication : Application() {
    val database: TimezoneDatabase by lazy {
        TimezoneDatabase.getInstance(this)
    }
}

val Application.thisApplicaton: WorldClockTileApplication
    get() = this as WorldClockTileApplication

val Context.thisApplication: WorldClockTileApplication
    get() = applicationContext as WorldClockTileApplication