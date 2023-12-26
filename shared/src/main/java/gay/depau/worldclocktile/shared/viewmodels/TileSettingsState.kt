package gay.depau.worldclocktile.shared.viewmodels

import gay.depau.worldclocktile.shared.utils.ColorScheme

data class TileSettingsState(
    val timezoneId: String?,
    val cityName: String?,
    val time24h: Boolean,
    val listOrder: Int = 0,
    val colorScheme: ColorScheme,
    val selectedContinent: String = "",
    val selectedCountry: String = "",
    val selectedProvince: String = "",
    val provincesDenomination: String = "",
) {
    companion object {
        val Empty: TileSettingsState
            get() = TileSettingsState(null, null, false, 0, ColorScheme.Default)
    }
}