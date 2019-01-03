package de.dhbw.studienarbeit.data.weatherStoplinker;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dhbw.studienarbeit.data.helper.database.saver.DatabaseSaver;
import de.dhbw.studienarbeit.data.helper.database.saver.Saveable;
import de.dhbw.studienarbeit.data.reader.database.Count;

public class WeatherStopLinker implements Saveable
{
	private static final Logger LOGGER = Logger.getLogger(WeatherStopLinker.class.getName());
	private static long idx = 1;

	public static void main(String[] args)
	{
		while (true)
		{
			new WeatherStopLinker().linkWeatherAndStops();
		}
	}

	private void linkWeatherAndStops()
	{
		Count stopCount = Count.countStops();
		while (idx <= stopCount.getValue())
		{
			DatabaseSaver.saveData(this);
			LOGGER.log(Level.FINE, "Stop with index " + idx + " linked to weather.");
			idx++;
		}
	}

	@Override
	public String getSQLQuerry()
	{
		return "INSERT INTO StopWeather SELECT stopID, max(Weather.timeStamp) FROM Stop, Station, Weather "
				+ "WHERE Stop.stationID = Station.stationID AND Stop.stopID = ? AND Stop.stopID NOT IN (SELECT stopID FROM StopWeather) "
				+ "AND Weather.lat = ROUND(Station.lat, 2) AND Weather.lon = ROUND(Station.lon, 2) AND Weather.timeStamp < Stop.realTime "
				+ "GROUP BY Stop.stopID, Stop.realTime, Weather.timeStamp HAVING count(Weather.timeStamp) > 0 AND max(Weather.timeStamp) > DATE_SUB(Stop.realTime,INTERVAL 30 MINUTE)";
	}

	@Override
	public void setValues(PreparedStatement preparedStatement) throws SQLException
	{
		preparedStatement.setLong(1, idx);
	}
}