SELECT * FROM StopWeather, Stop, Weather WHERE StopWeather.stopID = Stop.stopID AND StopWeather.weatherId = Weather.id