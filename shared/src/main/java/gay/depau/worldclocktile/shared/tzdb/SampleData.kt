package gay.depau.worldclocktile.shared.tzdb

// SPDX-License-Identifier: Apache-2.0
// This file is part of World Clock Tile.

fun populateWithSampleData(dao: TimezoneDao) {
    dao.insertCity(
        City(
            "Milan",
            "Lombardy",
            "regions",
            "Italy",
            "Europe",
            "Europe/Rome",
            "Central European Time"
        )
    )
    dao.insertCity(
        City(
            "Rome",
            "Lazio",
            "regions",
            "Italy",
            "Europe",
            "Europe/Rome",
            "Central European Time"
        )
    )
    dao.insertCity(
        City(
            "New York",
            "New York",
            "states",
            "United States",
            "North America",
            "America/New_York",
            "Eastern Time"
        )
    )
    dao.insertCity(
        City(
            "Los Angeles",
            "California",
            "states",
            "United States",
            "North America",
            "America/Los_Angeles",
            "Pacific Time"
        )
    )
    dao.insertCity(
        City(
            "Tokyo",
            "Tokyo",
            "prefectures",
            "Japan",
            "Asia",
            "Asia/Tokyo",
            "Japan Standard Time"
        )
    )
    dao.insertCity(
        City(
            "Beijing",
            "Beijing",
            "municipalities",
            "China",
            "Asia",
            "Asia/Shanghai",
            "China Standard Time"
        )
    )
    dao.insertCity(
        City(
            "Manila",
            "Metro Manila",
            "regions",
            "Philippines",
            "Asia",
            "Asia/Manila",
            "Philippine Time"
        )
    )
    dao.insertCity(
        City(
            "Cebu",
            "Cebu",
            "regions",
            "Philippines",
            "Asia",
            "Asia/Manila",
            "Philippine Time"
        )
    )
    dao.insertCity(
        City(
            "Quezon City",
            "Metro Manila",
            "regions",
            "Philippines",
            "Asia",
            "Asia/Manila",
            "Philippine Time"
        )
    )
    dao.insertCity(
        City(
            "Bangkok",
            "Bangkok",
            "municipalities",
            "Thailand",
            "Asia",
            "Asia/Bangkok",
            "Indochina Time"
        )
    )
    dao.insertCity(
        City(
            "Seoul",
            "Seoul",
            "metropolitan cities",
            "South Korea",
            "Asia",
            "Asia/Seoul",
            "Korea Standard Time"
        )
    )
    dao.insertCity(
        City(
            "São Paulo",
            "São Paulo",
            "states",
            "Brazil",
            "South America",
            "America/Sao_Paulo",
            "Brasilia Time"
        )
    )
    dao.insertCity(
        City(
            "Rio de Janeiro",
            "Rio de Janeiro",
            "states",
            "Brazil",
            "South America",
            "America/Sao_Paulo",
            "Brasilia Time"
        )
    )
    dao.insertCity(
        City(
            "Dakar",
            "Dakar",
            "regions",
            "Senegal",
            "Africa",
            "Africa/Dakar",
            "Western African Time"
        )
    )
    dao.insertCity(
        City(
            "Sydney",
            "New South Wales",
            "states",
            "Australia",
            "Oceania",
            "Australia/Sydney",
            "Australian Eastern Daylight Time"
        )
    )
    dao.insertCity(
        City(
            "Melbourne",
            "Victoria",
            "states",
            "Australia",
            "Oceania",
            "Australia/Melbourne",
            "Australian Eastern Daylight Time"
        )
    )
}
