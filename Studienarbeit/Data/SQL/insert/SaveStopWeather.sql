INSERT INTO StopWeather

SELECT stopID, Weather.id

FROM Stop, Station, Weather
WHERE Stop.stationID = Station.stationID
        AND Stop.stopID = 190010
        AND Stop.stopID NOT IN (SELECT stopID FROM StopWeather)
        
        AND Weather.lat = ROUND(Station.lat, 2)
        AND Weather.lon = ROUND(Station.lon, 2)
        AND Weather.timeStamp < Stop.realTime
     
GROUP BY Stop.stopID, Stop.realTime, Weather.timeStamp, Weather.id

HAVING count(Weather.timeStamp) > 0 AND max(Weather.timeStamp) > DATE_SUB(Stop.realTime,INTERVAL 30 MINUTE)
;