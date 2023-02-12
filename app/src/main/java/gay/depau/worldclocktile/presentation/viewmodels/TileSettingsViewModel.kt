package gay.depau.worldclocktile.presentation.viewmodels

// SPDX-License-Identifier: GNU GPLv3
// This file is part of World Clock Tile.

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import gay.depau.worldclocktile.AppSettings
import gay.depau.worldclocktile.SettingChangeListener
import gay.depau.worldclocktile.thisApplicaton
import gay.depau.worldclocktile.tzdb.City
import gay.depau.worldclocktile.tzdb.TimezoneDao
import gay.depau.worldclocktile.utils.ColorScheme
import kotlinx.coroutines.flow.*

data class TileSettingsState(
    val timezoneId: String?,
    val cityName: String?,
    val time24h: Boolean,
    val colorScheme: ColorScheme,
    val selectedContinent: String = "",
    val selectedCountry: String = "",
    val selectedProvince: String = "",
    val provincesDenomination: String = "",
) {
    companion object {
        val Empty: TileSettingsState
            get() = TileSettingsState(null, null, false, ColorScheme.Default)
    }
}

class TileSettingsViewModel(
    private val timezoneDao: TimezoneDao
) : ViewModel(), SettingChangeListener {
    private val _state = MutableStateFlow(TileSettingsState.Empty)
    val state: StateFlow<TileSettingsState> = _state.asStateFlow()


    override fun refreshSettings(settings: AppSettings) {
        _state.update {
            it.copy(
                timezoneId = settings.timezoneId,
                cityName = settings.cityName,
                time24h = settings.time24h,
                colorScheme = settings.colorScheme,
            )
        }
    }

    fun setState(state: TileSettingsState) {
        _state.update { state }
    }

    fun selectContinent(continent: String) {
        _state.update {
            it.copy(
                selectedContinent = continent,
                selectedCountry = "",
                selectedProvince = "",
            )
        }
    }

    fun selectCountry(
        country: String,
        provincesDenomination: String? = null,
        province: String? = null
    ) {
        _state.update {
            it.copy(
                selectedCountry = country,
                provincesDenomination = provincesDenomination ?: "provinces",
                selectedProvince = province ?: "",
            )
        }
    }

    fun selectProvince(country: String, province: String) {
        _state.update {
            it.copy(
                selectedCountry = country,
                selectedProvince = province,
            )
        }
    }

    fun getContinents() = timezoneDao.getContinents()

    fun getCountriesInContinent(continent: String) = timezoneDao.getCountriesInContinent(continent)

    fun getProvincesInCountry(country: String) = timezoneDao.getProvincesInCountry(country)

    fun getCitiesInProvince(country: String, province: String) =
        timezoneDao.getCitiesInProvince(country, province)

    fun searchCities(query: String): Flow<List<City>> {
        if (query.length < 3)
            return emptyFlow()
        return timezoneDao.searchCities("*$query*")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])

                if (modelClass.isAssignableFrom(TileSettingsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return TileSettingsViewModel(application.thisApplicaton.database.getTimezoneDao()) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}