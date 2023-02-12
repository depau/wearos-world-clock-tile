package gay.depau.worldclocktile.tzdb

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


data class CountryNameAndProvincesDenomination(
    var country: String,
    var provincesDenomination: String,
)

data class CityWithMatchInfo(
    @Embedded val city: City, val matchInfo: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CityWithMatchInfo

        if (city != other.city) return false
        if (!matchInfo.contentEquals(other.matchInfo)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = city.hashCode()
        result = 31 * result + matchInfo.contentHashCode()
        return result
    }
}

fun rank(matchInfo: IntArray): Double {
    val numPhrases = matchInfo[0]
    val numColumns = matchInfo[1]

    var score = 0.0
    for (phrase in 0 until numPhrases) {
        val offset = 2 + phrase * numColumns * 3
        for (column in 0 until numColumns) {
            val numHitsInRow = matchInfo[offset + 3 * column]
            val numHitsInAllRows = matchInfo[offset + 3 * column + 1]
            if (numHitsInAllRows > 0) {
                score += numHitsInRow.toDouble() / numHitsInAllRows.toDouble()
            }
        }
    }

    return score
}

fun ByteArray.skip(skipSize: Int): IntArray {
    val cleanedArr = IntArray(this.size / skipSize)
    var pointer = 0
    for (i in this.indices step skipSize) {
        cleanedArr[pointer] = this[i].toInt()
        pointer++
    }

    return cleanedArr
}

@Dao
interface TimezoneDao {
    // fun getContinents(): Flow<List<String>>
    @Query("SELECT DISTINCT continent FROM City ORDER BY continent ASC")
    fun getContinents(): Flow<List<String>>

    @Query("SELECT DISTINCT country, provincesDenomination FROM City WHERE continent = :continent ORDER BY country ASC")
    fun getCountriesInContinent(continent: String): Flow<List<CountryNameAndProvincesDenomination>>

    @Query("SELECT DISTINCT province FROM City WHERE country = :country ORDER BY province ASC")
    fun getProvincesInCountry(country: String): Flow<List<String>>

    @Query("SELECT *, City.`rowid` FROM City WHERE country = :country AND province = :province ORDER BY name ASC")
    fun getCitiesInProvince(country: String, province: String): Flow<List<City>>

    @Query("SELECT *, City.`rowid`, matchinfo(City, 'pcx') as matchInfo FROM City WHERE City MATCH :query")
    fun searchCitiesWithMatchInfo(query: String): Flow<List<CityWithMatchInfo>>

    fun searchCities(query: String): Flow<List<City>> {
        return searchCitiesWithMatchInfo(query).map { list ->
            list.sortedByDescending {
                rank(it.matchInfo.skip(4))
            }.map { it.city }.take(50)
        }
    }

    @Query("SELECT *, City.`rowid` FROM City ORDER BY name ASC")
    fun getAllCities(): List<City>

    @Query("SELECT DISTINCT country FROM City ORDER BY country ASC")
    fun getAllCountries(): List<String>

    @Query("SELECT *, City.`rowid` FROM City WHERE country = :country ORDER BY name ASC")
    fun getCitiesInCountry(country: String): List<City>

    @Update
    fun updateCity(city: City)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCity(city: City): Long
}
