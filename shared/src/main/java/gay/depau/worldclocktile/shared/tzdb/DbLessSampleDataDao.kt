package gay.depau.worldclocktile.shared.tzdb

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DbLessSampleDataDao : TimezoneDao {
    override fun getContinents(): Flow<List<String>> {
        return flowOf(listOf("Europe", "North America", "Asia", "Africa", "Oceania", "South America"))
    }

    override fun getCountriesInContinent(continent: String): Flow<List<CountryNameAndProvincesDenomination>> {
        return when (continent) {
            "Europe" -> flowOf(
                listOf(
                    CountryNameAndProvincesDenomination("Italy", "regions"),
                    CountryNameAndProvincesDenomination("United Kingdom", "regions"),
                    CountryNameAndProvincesDenomination("France", "regions"),
                    CountryNameAndProvincesDenomination("Germany", "regions"),
                    CountryNameAndProvincesDenomination("Spain", "regions"),
                    CountryNameAndProvincesDenomination("Ukraine", "regions"),
                    CountryNameAndProvincesDenomination("Sweden", "regions"),
                    CountryNameAndProvincesDenomination("Switzerland", "regions")
                )
            )

            "North America" -> flowOf(
                listOf(
                    CountryNameAndProvincesDenomination("United States", "states"),
                    CountryNameAndProvincesDenomination("Canada", "provinces")
                )
            )

            "Asia" -> flowOf(
                listOf(
                    CountryNameAndProvincesDenomination("Japan", "prefectures"),
                    CountryNameAndProvincesDenomination("China", "provinces"),
                    CountryNameAndProvincesDenomination("India", "states"),
                    CountryNameAndProvincesDenomination("Indonesia", "provinces"),
                    CountryNameAndProvincesDenomination("Pakistan", "provinces"),
                    CountryNameAndProvincesDenomination("Bangladesh", "divisions"),
                    CountryNameAndProvincesDenomination("Philippines", "regions"),
                    CountryNameAndProvincesDenomination("Vietnam", "provinces"),
                    CountryNameAndProvincesDenomination("Taiwan", "provinces")
                )
            )

            "Africa" -> flowOf(
                listOf(
                    CountryNameAndProvincesDenomination("Nigeria", "states"),
                    CountryNameAndProvincesDenomination("Ethiopia", "regions"),
                    CountryNameAndProvincesDenomination("Egypt", "governorates"),
                    CountryNameAndProvincesDenomination("Democratic Republic of the Congo", "provinces"),
                    CountryNameAndProvincesDenomination("South Africa", "provinces"),
                    CountryNameAndProvincesDenomination("Tanzania", "regions"),
                    CountryNameAndProvincesDenomination("Kenya", "counties"),
                    CountryNameAndProvincesDenomination("Algeria", "provinces"),
                    CountryNameAndProvincesDenomination("Sudan", "states")
                )
            )

            "Oceania" -> flowOf(
                listOf(
                    CountryNameAndProvincesDenomination("Australia", "states"),
                    CountryNameAndProvincesDenomination("Papua New Guinea", "provinces"),
                    CountryNameAndProvincesDenomination("New Zealand", "regions"),
                    CountryNameAndProvincesDenomination("Fiji", "divisions"),
                    CountryNameAndProvincesDenomination("Solomon Islands", "provinces"),
                    CountryNameAndProvincesDenomination("Vanuatu", "provinces")
                )
            )

            "South America" -> flowOf(
                listOf(
                    CountryNameAndProvincesDenomination("Brazil", "states"),
                    CountryNameAndProvincesDenomination("Colombia", "departments"),
                    CountryNameAndProvincesDenomination("Argentina", "provinces"),
                    CountryNameAndProvincesDenomination("Peru", "regions"),
                    CountryNameAndProvincesDenomination("Venezuela", "states"),
                    CountryNameAndProvincesDenomination("Chile", "regions"),
                    CountryNameAndProvincesDenomination("Ecuador", "provinces"),
                    CountryNameAndProvincesDenomination("Bolivia", "departments"),
                    CountryNameAndProvincesDenomination("Paraguay", "departments")
                )
            )

            else -> flowOf(emptyList())
        }
    }

    override fun getProvincesInCountry(country: String): Flow<List<String>> {
        return flowOf(
            listOf(
                "Abruzzo",
                "Aosta Valley",
                "Apulia",
                "Basilicata",
                "Calabria",
                "Campania",
                "Emilia-Romagna",
                "Friuli-Venezia Giulia",
                "Lazio",
                "Liguria",
                "Lombardy",
                "Marche",
                "Molise",
                "Piedmont",
                "Sardinia",
                "Sicily",
                "Trentino-South Tyrol",
                "Tuscany",
                "Umbria",
                "Veneto"
            )
        )
    }

    override fun getCitiesInProvince(country: String, province: String): Flow<List<City>> {
        return flowOf(
            listOf(
                City(
                    "Milan",
                    "Lombardy",
                    "regions",
                    "Italy",
                    "Europe",
                    "Europe/Rome",
                    "Central European Time"
                ).apply { rowid = 1 },
                City(
                    "Bergamo",
                    "Lombardy",
                    "regions",
                    "Italy",
                    "Europe",
                    "Europe/Rome",
                    "Central European Time"
                ).apply { rowid = 2 },
                City(
                    "Brescia",
                    "Lombardy",
                    "regions",
                    "Italy",
                    "Europe",
                    "Europe/Rome",
                    "Central European Time"
                ).apply { rowid = 3 },
                City(
                    "Como",
                    "Lombardy",
                    "regions",
                    "Italy",
                    "Europe",
                    "Europe/Rome",
                    "Central European Time"
                ).apply { rowid = 4 },
                City(
                    "Cremona",
                    "Lombardy",
                    "regions",
                    "Italy",
                    "Europe",
                    "Europe/Rome",
                    "Central European Time"
                ).apply { rowid = 5 },
                City(
                    "Lecco",
                    "Lombardy",
                    "regions",
                    "Italy",
                    "Europe",
                    "Europe/Rome",
                    "Central European Time"
                ).apply { rowid = 6 },
                City(
                    "Lodi",
                    "Lombardy",
                    "regions",
                    "Italy",
                    "Europe",
                    "Europe/Rome",
                    "Central European Time"
                ).apply { rowid = 7 },
                City(
                    "Mantua",
                    "Lombardy",
                    "regions",
                    "Italy",
                    "Europe",
                    "Europe/Rome",
                    "Central European Time"
                ).apply { rowid = 8 },
                City(
                    "Monza",
                    "Lombardy",
                    "regions",
                    "Italy",
                    "Europe",
                    "Europe/Rome",
                    "Central European Time"
                ).apply { rowid = 9 },
                City(
                    "Pavia",
                    "Lombardy",
                    "regions",
                    "Italy",
                    "Europe",
                    "Europe/Rome",
                    "Central European Time"
                )
            )
        )
    }

    override fun searchCitiesWithMatchInfo(query: String): Flow<List<CityWithMatchInfo>> {
        return flowOf(
            listOf(
                CityWithMatchInfo(
                    City(
                        "Milan",
                        "Lombardy",
                        "regions",
                        "Italy",
                        "Europe",
                        "Europe/Rome",
                        "Central European Time"
                    ),
                    byteArrayOf()
                ),
                CityWithMatchInfo(
                    City(
                        "Rome",
                        "Lazio",
                        "regions",
                        "Italy",
                        "Europe",
                        "Europe/Rome",
                        "Central European Time"
                    ),
                    byteArrayOf()
                ),
                CityWithMatchInfo(
                    City(
                        "New York",
                        "New York",
                        "states",
                        "United States",
                        "North America",
                        "America/New_York",
                        "Eastern Time"
                    ),
                    byteArrayOf()
                ),
                CityWithMatchInfo(
                    City(
                        "Los Angeles",
                        "California",
                        "states",
                        "United States",
                        "North America",
                        "America/Los_Angeles",
                        "Pacific Time"
                    ),
                    byteArrayOf()
                ),
            )
        )
    }

    override fun searchCities(query: String): Flow<List<City>> {
        return getCitiesInProvince("Italy", "Lombardy")
    }

    override fun getAllCities(): List<City> {
        return listOf(
            City(
                "Milan",
                "Lombardy",
                "regions",
                "Italy",
                "Europe",
                "Europe/Rome",
                "Central European Time"
            ),
            City(
                "Rome",
                "Lazio",
                "regions",
                "Italy",
                "Europe",
                "Europe/Rome",
                "Central European Time"
            ),
            City(
                "New York",
                "New York",
                "states",
                "United States",
                "North America",
                "America/New_York",
                "Eastern Time"
            ),
            City(
                "Los Angeles",
                "California",
                "states",
                "United States",
                "North America",
                "America/Los_Angeles",
                "Pacific Time"
            ),
            City(
                "Tokyo",
                "Tokyo",
                "prefectures",
                "Japan",
                "Asia",
                "Asia/Tokyo",
                "Japan Standard Time"
            ),
            City(
                "Beijing",
                "Beijing",
                "provinces",
                "China",
                "Asia",
                "Asia/Shanghai",
                "China Standard Time"
            ),
            City(
                "Shanghai",
                "Shanghai",
                "provinces",
                "China",
                "Asia",
                "Asia/Shanghai",
                "China Standard Time"
            ),
            City(
                "Delhi",
                "Delhi",
                "states",
                "India",
                "Asia",
                "Asia/Kolkata",
                "India Standard Time"
            ),
            City(
                "Mumbai",
                "Maharashtra",
                "states",
                "India",
                "Asia",
                "Asia/Kolkata",
                "India Standard Time"
            ),
            City(
                "Jakarta",
                "Jakarta",
                "provinces",
                "Indonesia",
                "Asia",
                "Asia/Jakarta",
                "Western Indonesia Time"
            ),
            City(
                "Lagos",
                "Lagos",
                "states",
                "Nigeria",
                "Africa",
                "Africa/Lagos",
                "West Africa Time"
            ),
            City(
                "Cairo",
                "Cairo",
                "governorates",
                "Egypt",
                "Africa",
                "Africa/Cairo",
                "Eastern European Time"
            )
        )
    }

    override fun getAllCountries(): List<String> {
        return listOf(
            "Italy",
            "United States",
            "Japan",
            "China",
            "India",
            "Indonesia",
            "Nigeria",
            "Egypt"
        )
    }

    override fun getCitiesInCountry(country: String): List<City> {
        return emptyList()
    }

    override fun updateCity(city: City) {
    }

    override fun insertCity(city: City): Long {
        return 0
    }
}