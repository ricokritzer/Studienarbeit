package de.dhbw.studienarbeit.data.helper.database;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dhbw.studienarbeit.data.helper.Settings;
import de.dhbw.studienarbeit.data.helper.database.model.LineDB;
import de.dhbw.studienarbeit.data.helper.database.model.StationDB;
import de.dhbw.studienarbeit.data.helper.database.model.StopDB;
import de.dhbw.studienarbeit.data.helper.datamanagement.ApiKey;

public class DatabaseReader extends DatabaseConnector
{
	private static final String UNABLE_TO_READ = "Unable to read at table ";
	private static final String START_READING_AT_TABLE = "Start reading at table ";
	private static final String ENTRYS_READ = " entrys read.";
	private static final String SELECT_FROM = "SELECT * FROM ";
	private static final Logger LOGGER = Logger.getLogger(TextSaver.class.getName());

	public DatabaseReader() throws IOException
	{
		super();
	}

	@Override
	protected void connectToDatabase() throws SQLException
	{
		connectToDatabase(Settings.getInstance().getDatabaseReaderUser(),
				Settings.getInstance().getDatabaseReaderPassword());
	}

	@Deprecated
	public List<ApiKey> readApiKeys(final String name) throws SQLException
	{
		return readApiKeys(new SqlCondition("name", name));
	}

	public List<ApiKey> readApiKeys(final SqlCondition... conditions) throws SQLException
	{
		final String tableName = "Api";
		final List<ApiKey> list = new ArrayList<>();
		select(r -> getApiKey(r).ifPresent(list::add), tableName, conditions);
		return list;
	}

	@Deprecated
	public List<LineDB> readLine(final String destination, final String name) throws SQLException
	{
		return readLine(new SqlCondition("name", name), //
				new SqlCondition("destination", destination));
	}

	public List<LineDB> readLine(final SqlCondition... conditions) throws SQLException
	{
		final String tableName = "Line";
		final List<LineDB> list = new ArrayList<>();
		select(r -> getLine(r).ifPresent(list::add), tableName, conditions);
		return list;
	}

	@Deprecated
	public List<StationDB> readStations() throws SQLException
	{
		return readStations(new SqlCondition("observe", true));
	}

	public final List<StationDB> readStations(final SqlCondition... conditions) throws SQLException
	{
		final String tableName = "Station";
		final List<StationDB> stations = new ArrayList<>();
		select(r -> getStation(r).ifPresent(stations::add), tableName, conditions);
		return stations;
	}

	public List<StopDB> readStops(SqlCondition... conditions) throws SQLException
	{
		final String tableName = "Station";
		final List<StopDB> list = new ArrayList<>();
		select(r -> getStop(r).ifPresent(list::add), tableName, conditions);
		return list;
	}

	protected String createSQLStatement(final String tableName, final SqlCondition... condition)
	{
		final StringBuilder sb = new StringBuilder(SELECT_FROM).append(tableName);
		final List<String> conditionStrings = new ArrayList<>();
		Arrays.asList(condition).forEach(c -> conditionStrings.add(c.toString()));

		if (!conditionStrings.isEmpty())
		{
			sb.append(" WHERE ");
		}

		sb.append(String.join(" AND ", conditionStrings));
		sb.append(";");
		return sb.toString();
	}

	private void reconnectIfNeccessary() throws SQLException
	{
		if (connection.isClosed())
		{
			connectToDatabase();
		}
	}

	private void select(Consumer<ResultSet> consumer, String tableName, SqlCondition... conditions) throws SQLException
	{
		LOGGER.log(Level.INFO, START_READING_AT_TABLE + tableName);

		reconnectIfNeccessary();

		final String sql = createSQLStatement(tableName, conditions);
		try (ResultSet result = connection.prepareStatement(sql).executeQuery())
		{
			int counter = 0;
			while (result.next())
			{
				consumer.accept(result);
				counter++;
			}
			LOGGER.log(Level.INFO, counter + ENTRYS_READ);
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, UNABLE_TO_READ + tableName, e);
			throw e;
		}
	}

	private Optional<StationDB> getStation(ResultSet result)
	{
		try
		{
			final String stationID = result.getString("stationID");
			final String name = result.getString("name");
			final double lat = result.getDouble("lat");
			final double lon = result.getDouble("lon");
			final String operator = result.getString("operator");
			return Optional.of(new StationDB(stationID, name, lat, lon, operator));
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Unable to parse to station.", e);
			return Optional.empty();
		}
	}

	private Optional<StopDB> getStop(ResultSet result)
	{
		try
		{
			final int stopID = result.getInt("stopID");
			final String stationID = result.getString("stationID");
			final int lineID = result.getInt("lineID");
			final Date timeTabledTime = result.getDate("timeTabledTime");
			final Date realTime = result.getDate("realTime");

			return Optional.of(new StopDB(stopID, stationID, lineID, timeTabledTime, realTime));
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Unable to parse to stop.", e);
			return Optional.empty();
		}
	}

	private Optional<LineDB> getLine(ResultSet result)
	{
		try
		{
			final int lineID = result.getInt("lineID");
			final String lineName = result.getString("name");
			final String lineDestination = result.getString("destination");
			return Optional.of(new LineDB(lineID, lineName, lineDestination));
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Unable to parse to line.", e);
			return Optional.empty();
		}
	}

	private Optional<ApiKey> getApiKey(ResultSet result)
	{
		try
		{
			final String key = result.getString("apiKey");
			final int requests = result.getInt("maximumRequests");
			final String url = result.getString("url");
			return Optional.of(new ApiKey(key, requests, url));
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, "Unable to parse to api key.", e);
			return Optional.empty();
		}
	}
}
