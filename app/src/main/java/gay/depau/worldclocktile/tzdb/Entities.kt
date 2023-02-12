package gay.depau.worldclocktile.tzdb

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

// TODO: See if other countries like to duplicate city names
private val usStates = mapOf(
    "Alabama" to "AL",
    "Alaska" to "AK",
    "Arizona" to "AZ",
    "Arkansas" to "AR",
    "California" to "CA",
    "Colorado" to "CO",
    "Connecticut" to "CT",
    "Delaware" to "DE",
    "District of Columbia" to "DC",
    "Florida" to "FL",
    "Georgia" to "GA",
    "Hawaii" to "HI",
    "Idaho" to "ID",
    "Illinois" to "IL",
    "Indiana" to "IN",
    "Iowa" to "IA",
    "Kansas" to "KS",
    "Kentucky" to "KY",
    "Louisiana" to "LA",
    "Maine" to "ME",
    "Maryland" to "MD",
    "Massachusetts" to "MA",
    "Michigan" to "MI",
    "Minnesota" to "MN",
    "Mississippi" to "MS",
    "Missouri" to "MO",
    "Montana" to "MT",
    "Nebraska" to "NE",
    "Nevada" to "NV",
    "New Hampshire" to "NH",
    "New Jersey" to "NJ",
    "New Mexico" to "NM",
    "New York" to "NY",
    "North Carolina" to "NC",
    "North Dakota" to "ND",
    "Ohio" to "OH",
    "Oklahoma" to "OK",
    "Oregon" to "OR",
    "Pennsylvania" to "PA",
    "Rhode Island" to "RI",
    "South Carolina" to "SC",
    "South Dakota" to "SD",
    "Tennessee" to "TN",
    "Texas" to "TX",
    "Utah" to "UT",
    "Vermont" to "VT",
    "Virginia" to "VA",
    "Washington" to "WA",
    "West Virginia" to "WV",
    "Wisconsin" to "WI",
    "Wyoming" to "WY",
)

@Fts4
@Entity
data class City(
    val name: String,
    val province: String,
    val provincesDenomination: String,
    val country: String,
    val continent: String,
    val timezone: String,
    val timezoneName: String,
) {
    @PrimaryKey(autoGenerate = true)
    var rowid: Long = 0

    val countryName: String
        // TODO: See if other countries can be shortened meaningfully
        get() = when (country) {
            "United States of America" -> "USA"
            "United Arab Emirates" -> "UAE"
            "United Kingdom" -> "UK"
            else -> country
        }

    val cityName: String
        get() {
            if (country == "United States of America") {
                return "$name, ${usStates[province]}, $countryName"
            }
            return "$name, $countryName"
        }
}
