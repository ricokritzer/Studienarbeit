package de.dhbw.studienarbeit.data.trias.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.dhbw.studienarbeit.data.helper.database.DatabaseSaver;
import de.dhbw.studienarbeit.data.helper.database.Saver;
import de.dhbw.studienarbeit.data.trias.Station;

public class StationUtil
{
	public static void main(String[] args)
	{
		getAllStations();
	}

	public static List<Station> getAllStations()
	{
		List<Station> stations = new ArrayList<>();
		try
		{
			File file = new File("stops.txt");
			FileReader fr;
			fr = new FileReader(file);
			BufferedReader reader = new BufferedReader(fr);
			while (reader.ready())
			{
				String stop = reader.readLine();
				if (stop.startsWith("\"de"))
				{
					String[] splittedStop = stop.split("\",\"");
					String stationIDLong = splittedStop[0].replaceAll("\"", "");
					String[] stationIDSplitted = stationIDLong.split(":");
					String stationID = stationIDSplitted[0] + ":" + stationIDSplitted[1] + ":" + stationIDSplitted[2];
					
					String name = splittedStop[1];
					
					if (testAlreadySaved(stations, stationID))
					{
						continue;
					}
					if (name.isEmpty())
					{
						continue;
					}
					stations.add(new Station(stationID, name, Double.valueOf(splittedStop[2]),
							Double.valueOf(splittedStop[3].replaceAll("\"", ""))));
				}
			}
			reader.close();
			Saver saver = new DatabaseSaver();
			stations.forEach(station -> saver.save(station));
		}
		catch (IOException | SQLException | ReflectiveOperationException e)
		{
			e.printStackTrace();
		}
		return stations;
	}

	private static boolean testAlreadySaved(List<Station> stations, String stationID)
	{
		for (Station s : stations)
		{
			if (s.getStationID().equals(stationID))
			{
				return true;
			}
		}
		return false;
	}
}
