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
import gay.depau.worldclocktile.tzdb.CountryNameAndProvincesDenomination
import gay.depau.worldclocktile.tzdb.TimezoneDao
import gay.depau.worldclocktile.utils.ColorScheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update

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

data class DbListResultCache(
    val continents: List<String> = emptyList(),
    val queriedContinent: String = "",
    val countries: List<CountryNameAndProvincesDenomination> = emptyList(),
    val queriedCountry: String = "",
    val provinces: List<String> = emptyList(),
    val queriedProvince: String = "",
    val cities: List<City> = emptyList(),
)

class TileSettingsViewModel(
    private val timezoneDao: TimezoneDao,
) : ViewModel(), SettingChangeListener {
    private val _state = MutableStateFlow(TileSettingsState.Empty)
    val state: StateFlow<TileSettingsState> = _state.asStateFlow()

    private val _dbListResultCache = MutableStateFlow(DbListResultCache())
    private val dbListResultCache: StateFlow<DbListResultCache> = _dbListResultCache.asStateFlow()

    fun resetDbCache() {
        _dbListResultCache.update { DbListResultCache() }
    }

    fun cacheContinents(continents: List<String>) {
        _dbListResultCache.update { it.copy(continents = continents) }
    }

    fun cacheCountries(countries: List<CountryNameAndProvincesDenomination>, continent: String) {
        _dbListResultCache.update { it.copy(countries = countries, queriedContinent = continent) }
    }

    fun cacheProvinces(provinces: List<String>, country: String) {
        _dbListResultCache.update { it.copy(provinces = provinces, queriedCountry = country) }
    }

    fun cacheCities(cities: List<City>, province: String, country: String) {
        _dbListResultCache.update {
            it.copy(
                cities = cities,
                queriedProvince = province,
                queriedCountry = country
            )
        }
    }

    fun getContinentsCache() = dbListResultCache.value.continents

    fun getCountriesCache(continent: String): List<CountryNameAndProvincesDenomination> {
        return if (dbListResultCache.value.queriedContinent == continent) {
            dbListResultCache.value.countries
        } else {
            _dbListResultCache.update {
                it.copy(
                    countries = emptyList(),
                    queriedContinent = continent
                )
            }
            emptyList()
        }
    }

    fun getProvincesCache(country: String): List<String> {
        return if (dbListResultCache.value.queriedCountry == country) {
            dbListResultCache.value.provinces
        } else {
            _dbListResultCache.update {
                it.copy(
                    provinces = emptyList(),
                    queriedCountry = country
                )
            }
            emptyList()
        }
    }

    fun getCitiesCache(province: String, country: String): List<City> {
        return if (dbListResultCache.value.queriedProvince == province && dbListResultCache.value.queriedCountry == country) {
            dbListResultCache.value.cities
        } else {
            _dbListResultCache.update {
                it.copy(
                    cities = emptyList(),
                    queriedProvince = province,
                    queriedCountry = country
                )
            }
            emptyList()
        }
    }


    override fun refreshSettings(settings: AppSettings) {
        _state.update {
            it.copy(
                timezoneId = settings.timezoneId,
                cityName = settings.cityName,
                time24h = settings.time24h,
                colorScheme = settings.colorScheme,
                listOrder = settings.listOrder,
            )
        }
    }

    fun setListOrder(order: Int) {
        _state.update {
            it.copy(listOrder = order)
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
        province: String? = null,
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