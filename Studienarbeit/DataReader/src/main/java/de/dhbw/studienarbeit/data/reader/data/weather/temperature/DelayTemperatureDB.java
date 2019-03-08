package de.dhbw.studienarbeit.data.reader.data.weather.temperature;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dhbw.studienarbeit.data.reader.data.DelayAverage;
import de.dhbw.studienarbeit.data.reader.data.DelayMaximum;
import de.dhbw.studienarbeit.data.reader.data.weather.DelayWeatherDBHelper;
import de.dhbw.studienarbeit.data.reader.database.DatabaseReader;

public class DelayTemperatureDB implements DelayTemperature
{
	private static final Logger LOGGER = Logger.getLogger(DelayTemperatureDB.class.getName());
	private static final String FIELD = "Round(temp, 0)";
	private static final String NAME = "rounded";

	private static final Optional<DelayTemperatureData> getDelays(ResultSet result)
	{
		try
		{
			final DelayMaximum delayMaximum = new DelayMaximum(result.getDouble("delay_max"));
			final DelayAverage delayAverage = new DelayAverage(result.getDouble("delay_avg"));
			final double wind = result.getDouble(NAME);

			return Optional.of(new DelayTemperatureData(delayMaximum, delayAverage, wind));
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Unable to parse to " + DelayTemperatureDB.class.getName(), e);
			return Optional.empty();
		}
	}

	public final List<DelayTemperatureData> getDelays() throws IOException
	{
		final String sql = DelayWeatherDBHelper.buildSQL(FIELD, NAME);

		final DatabaseReader database = new DatabaseReader();
		try (PreparedStatement preparedStatement = database.getPreparedStatement(sql))
		{
			final List<DelayTemperatureData> list = new ArrayList<>();
			database.select(r -> DelayTemperatureDB.getDelays(r).ifPresent(list::add), preparedStatement);
			return list;
		}
		catch (SQLException e)
		{
			throw new IOException("Selecting does not succeed.", e);
		}
	}
}
