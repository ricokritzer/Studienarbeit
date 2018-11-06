package de.dhbw.studienarbeit.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.dhbw.studienarbeit.data.helper.database.model.StationDB;
import de.dhbw.studienarbeit.data.helper.database.saver.DatabaseSaver;
import de.dhbw.studienarbeit.data.helper.database.table.DatabaseTableStation;
import de.dhbw.studienarbeit.data.repairer.DataRepairerApp;
import de.dhbw.studienarbeit.data.trias.DataTriasApp;
import de.dhbw.studienarbeit.data.weather.DataWeatherApp;

public class App
{
	public static void main(String[] args) throws IOException
	{
		setLogLevel(Level.INFO);

		final DatabaseTableStation databaseTableStation = new DatabaseTableStation();
		final List<String> operators = databaseTableStation.selectObservedOperators();
		for (String operator : operators)
		{
			final List<StationDB> stationsOfOperator = databaseTableStation.selectObservedStations(operator);
			new DataWeatherApp().startDataCollection(stationsOfOperator);
			new DataTriasApp().startDataCollection(operator, stationsOfOperator);
		}

		new DataRepairerApp(new DatabaseSaver()).startDataRepairing();
	}

	private static void setLogLevel(Level level)
	{
		final Logger rootLogger = LogManager.getLogManager().getLogger("");
		rootLogger.setLevel(level);
		Arrays.asList(rootLogger.getHandlers()).forEach(h -> h.setLevel(level));
	}
}
