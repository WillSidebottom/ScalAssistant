package com.scalassistant

/* The following is a collection of case classes used to define our result from the WeatherUnderground API */

object WeatherUnderground {
  case class Weather(
    response: Response,
    current_observation: CurrentObservation)

  case class Response(version: String, termsofService: String, features: Features)
  case class Features(conditions: Int)

  case class CurrentObservation(
    image: Image,
    display_location: DisplayLocation,
    observation_location: ObservationLocation,
    estimated: Any,
    station_id: String,
    observation_time: String,
    observation_time_rfc822: String,
    observation_epoch: String,
    local_time_rfc822: String,
    local_epoch: String,
    local_tz_short: String,
    local_tz_long: String,
    local_tz_offset: String,
    weather: String,
    temperature_string: String,
    temp_f: Double,
    temp_c: Double,
    relative_humidity: String,
    wind_string: String,
    wind_dir: String,
    wind_degrees: Int,
    wind_mph: Double,
    wind_gust_mph: String,
    wind_kph: Double,
    wind_gust_kph: String,
    pressure_mb: String,
    pressure_in: String,
    pressure_trend: String,
    dewpoint_string: String,
    dewpoint_f: Int,
    dewpoint_c: Int,
    heat_index_string: String,
    heat_index_f: String,
    heat_index_c: String,
    windchill_string: String,
    windchill_f: String,
    windchill_c: String,
    feelslike_string: String,
    feelslike_f: String,
    feelslike_c: String,
    visibility_mi: String,
    visibility_km: String,
    solarradiation: String,
    UV: String,
    precip_1hr_string: String,
    precip_1hr_in: String,
    precip_1hr_metric: String,
    precip_today_string: String,
    precip_today_in: String,
    precip_today_metric: String,
    icon: String,
    icon_url: String,
    forecast_url: String,
    history_url: String,
    ob_url: String
  )
  case class Image(url: String, title: String, link: String)
  case class DisplayLocation(
    full: String,
    city: String,
    state: String,
    state_name: String,
    country: String,
    country_iso3166: String,
    zip: String,
    latitude: String,
    longitude: String,
    elevation: String
  )
  case class ObservationLocation(
    full: String,
    city: String,
    state: String,
    country: String,
    country_iso3166: String,
    latitude: String,
    longitude: String,
    elevation: String
  )
}
