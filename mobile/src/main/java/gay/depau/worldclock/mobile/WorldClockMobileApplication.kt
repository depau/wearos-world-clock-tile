package gay.depau.worldclock.mobile

import android.app.Application
import android.content.Context
import gay.depau.worldclocktile.shared.tzdb.TimezoneDatabase

class WorldClockMobileApplication : Application() {
    val database: TimezoneDatabase by lazy {
        TimezoneDatabase.getInstance(this, BuildConfig.VERSION_CODE)
    }
}

val Application.thisApplicaton: WorldClockMobileApplication
    get() = this as WorldClockMobileApplication

val Context.thisApplication: WorldClockMobileApplication
    get() = applicationContext as WorldClockMobileApplication