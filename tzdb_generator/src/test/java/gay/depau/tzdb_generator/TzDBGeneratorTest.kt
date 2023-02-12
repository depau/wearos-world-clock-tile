package gay.depau.tzdb_generator

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import android.app.Application
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import gay.depau.worldclocktile.tzdb.City
import gay.depau.worldclocktile.tzdb.TimezoneDatabase
import gay.depau.worldclocktile.utils.iterator
import gay.depau.worldclocktile.utils.timezoneSimpleNames
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.*

const val CITY_DB_URL =
    "https://raw.githubusercontent.com/kevinroberts/city-timezones/master/data/cityMap.json"

// I just wanted to make a timezone app and suddenly I have to deal with politics, fml

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O], manifest = Config.NONE)
class TzDBGeneratorTest {
    @Test
    fun generateTzDB() {
        // Fetch timezone database JSON
        val (_, _, result) = Fuel.get(CITY_DB_URL).responseString()
        when (result) {
            is Result.Failure -> {
                throw result.getException()
            }
            else -> {}
        }
        val tzdbString = result.get()
        val tzdbJson = JSONArray(tzdbString)

        // Create database
        val app = getApplicationContext<Application>()
        val db =
            Room.databaseBuilder(app.applicationContext, TimezoneDatabase::class.java, "tzdb.db")
                .allowMainThreadQueries().build()
        val timezoneDao = db.getTimezoneDao()

        // Populate database
        db.runInTransaction {
            for (any in tzdbJson) {
                val cityInfo = any as JSONObject
                var provinceName = cityInfo.optString("province", "")
                var countryName = cityInfo.getString("country")
                var tzName = cityInfo.optString("timezone")

                if (countryName in countryNameFixups) {
                    countryName = countryNameFixups[countryName]!!
                    if (provinceName == "")
                        provinceName = cityInfo.getString("country")
                }
                if (countryName in missingProvinces && provinceName == "")
                    provinceName = missingProvinces[countryName]!!

                if (tzName == "" || tzName == "null")
                    continue

                if (tzName in tzNameFixups)
                    tzName = tzNameFixups[tzName]!!

                if (tzName !in timezoneSimpleNames) {
                    println("Unknown timezone $tzName")
                    assert(false)
                }

                assert(tzName in androidTimezoneNames)

                timezoneDao.insertCity(
                    City(
                        cityInfo.getString("city"),
                        provinceName,
                        provincesDenominations[countryName] ?: "provinces",
                        countryName,
                        continents[countryName]!!,
                        tzName,
                        timezoneSimpleNames[tzName]!!,
                    )
                )
            }
        }

        // Remove provinces from countries with less than 30 cities
        db.runInTransaction {
            val countryCities = mutableMapOf<String, MutableList<City>>()
            for (city in timezoneDao.getAllCities()) {
                countryCities.getOrPut(city.country) { mutableListOf() }.add(city)
            }

            for ((_, cities) in countryCities) {
                if (cities.size < 30) {
                    // println("Country $country has less than 30 cities, wiping provinces")
                    for (city in cities) {
                        timezoneDao.updateCity(
                            city.copy(province = "", provincesDenomination = "")
                                .apply { rowid = city.rowid }
                        )
                    }
                }
            }
        }

        // Remove provinces from countries with less than 5 provinces
        db.runInTransaction {
            val countryProvinces = mutableMapOf<String, MutableList<String>>()
            for (city in timezoneDao.getAllCities()) {
                countryProvinces.getOrPut(city.country) { mutableListOf() }.add(city.province)
            }

            for ((country, provinces) in countryProvinces) {
                if (provinces.size < 5) {
                    // println("Country $country has less than 5 provinces (${provinces.size}), wiping provinces")
                    for (city in timezoneDao.getCitiesInCountry(country)) {
                        timezoneDao.updateCity(
                            city.copy(province = "", provincesDenomination = "")
                                .apply { rowid = city.rowid }
                        )
                    }
                }
            }
        }

        // Remove provinces from countries in which the average number of cities per province is less than 5
        db.runInTransaction {
            val countries = timezoneDao.getAllCountries()
            for (country in countries) {
                val cities = timezoneDao.getCitiesInCountry(country)
                val provinces = cities.map { it.province }.distinct()

                // Avoid division by zero, or needless work
                if (provinces.size < 2)
                    continue

                val provinceCities = mutableMapOf<String, MutableList<City>>()
                for (city in cities) {
                    provinceCities.getOrPut(city.province) { mutableListOf() }.add(city)
                }

                var totalCities = 0
                for ((_, cities) in provinceCities) {
                    totalCities += cities.size
                }

                if (totalCities / provinces.size < 5) {
                    // println("Country $country has less than 5 cities per province (${totalCities / provinces.size}), wiping provinces")
                    for (city in cities) {
                        timezoneDao.updateCity(
                            city.copy(province = "", provincesDenomination = "")
                                .apply { rowid = city.rowid }
                        )
                    }
                }
            }
        }

        // Verify that all countries have either provinces set on every record or none at all
        db.runInTransaction {
            var foundBroken = false
            val countryHasProvinces = mutableMapOf<String, Boolean>()
            for (city in timezoneDao.getAllCities()) {
                if (city.country !in countryHasProvinces) {
                    countryHasProvinces[city.country] = city.province != ""
                } else {
                    if ((city.province != "") != countryHasProvinces[city.country]) {
                        println("Country ${city.country} has both provinces and no provinces")
                        foundBroken = true
                    }
                }
            }

            assert(!foundBroken)
        }

        // Save database to file
        val dbPath = db.openHelper.writableDatabase.path!!
        db.close()

        val dbFile = File(dbPath)
        val newDbFile = File("../app/src/main/assets", dbFile.name)

        if (newDbFile.exists())
            newDbFile.delete()
        dbFile.copyTo(newDbFile)

        val filePath = File(newDbFile.absolutePath).normalize().absolutePath
        println("\n\nGenerated database saved to '$filePath'\n")
    }
}