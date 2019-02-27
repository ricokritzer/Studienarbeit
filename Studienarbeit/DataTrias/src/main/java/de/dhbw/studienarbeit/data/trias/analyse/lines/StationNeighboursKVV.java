package de.dhbw.studienarbeit.data.trias.analyse.lines;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import de.dhbw.studienarbeit.data.helper.database.saver.DatabaseSaver;
import de.dhbw.studienarbeit.data.reader.database.DatabaseReader;
import de.dhbw.studienarbeit.data.trias.StationNeighbour;

public class StationNeighboursKVV
{
	private static Map<String, List<String>> stationNeighbours = new HashMap<>();
	private static Set<SimpleStation> stations = new HashSet<>();
	private static List<LineStation> stopsForLine;
	private static List<String> savedStations;

	public static void main(String[] args)
	{
		try
		{
			getAllStationNeighbours();
			saveNeighboursWithoutLines();
			saveStationNeighboursForLines();
		}
		catch (SQLException | IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void saveNeighboursWithoutLines()
	{
		for (SimpleStation station : stations)
		{
			for (SimpleStation neighbour : station.getNeighbours())
			{
				DatabaseSaver.saveData(new StationNeighbour(station.getID(), neighbour.getID(), 0));
				System.out.println("added neighbours " + station.getID() + " and " + neighbour.getID() + " - no line");
			}
		}
	}

	private static void saveStationNeighboursForLines() throws SQLException, IOException
	{
		DatabaseReader reader = new DatabaseReader();
		reader.select(new Consumer<ResultSet>()
		{

			@Override
			public void accept(ResultSet rs)
			{
				try
				{
					int lineID = rs.getInt(1);
					saveStationNeighboursForLine(lineID);
				}
				catch (SQLException | IOException e)
				{
					e.printStackTrace();
				}
			}
		}, reader.getPreparedStatement("SELECT lineID FROM Line"));
	}

	protected static void saveStationNeighboursForLine(int lineID) throws SQLException, IOException
	{
		getStopsForLine(lineID);
		saveNeighboursInList(lineID);
	}

	private static void saveNeighboursInList(int lineID)
	{
		savedStations = new ArrayList<>();
		Boolean newNeighboursFound = true;

		while (stopsForLine.size() > 1 && newNeighboursFound)
		{
			newNeighboursFound = false;
			for (int i = 0; i < stopsForLine.size() - 1; i++)
			{
				String stationID = stopsForLine.get(i).getStationID();
				String neighbourID = stopsForLine.get(i + 1).getStationID();

				if (neighbours(stationID, neighbourID))
				{
					DatabaseSaver.saveData(new StationNeighbour(stationID, neighbourID, lineID));
					savedStations.add(stationID);
					System.out.println("saved neighbours " + stationID + " and " + neighbourID + " for line " + lineID);
					newNeighboursFound = true;
				}
			}

			deleteSavedStations(savedStations);
		}

		for (int i = 0; i < stopsForLine.size(); i++)
		{
			saveRemainingNeighbours(stopsForLine.get(i).getStationID(), i, lineID);
		}
	}

	private static void saveRemainingNeighbours(String station, int start, int lineID)
	{
		for (int i = start + 1; i < stopsForLine.size(); i++)
		{
			if (neighbours(station, stopsForLine.get(i).getStationID()))
			{
				DatabaseSaver.saveData(new StationNeighbour(station, stopsForLine.get(i).getStationID(), lineID));
				savedStations.add(station);
				System.out.println("saved neighbours " + station + " and " + stopsForLine.get(i).getStationID()
						+ " for line " + lineID);
				return;
			}
		}

		for (int i = 0; i < savedStations.size(); i++)
		{
			if (savedStations.get(i).equals(station))
			{
				DatabaseSaver.saveData(new StationNeighbour(station, stopsForLine.get(i).getStationID(), lineID));
				System.out.println("saved neighbours " + station + " and " + stopsForLine.get(i).getStationID()
						+ " for line " + lineID);
				return;
			}
		}
	}

	private static void deleteSavedStations(List<String> savedStations)
	{
		for (int i = stopsForLine.size() - 1; i >= 0; i--)
		{
			if (savedStations.contains(stopsForLine.get(i).getStationID()))
			{
				stopsForLine.remove(i);
			}
		}
	}

	private static boolean neighbours(String station, String neighbour)
	{
		try
		{
			return getStation(new SimpleStation(station)).getNeighbours()
					.contains(getStation(new SimpleStation(neighbour)));
		}
		catch (Exception e)
		{
			return false;
		}
	}

	private static void getStopsForLine(int lineID) throws SQLException, IOException
	{
		stopsForLine = new ArrayList<>();
		DatabaseReader reader = new DatabaseReader();

		reader.select(new Consumer<ResultSet>()
		{
			@Override
			public void accept(ResultSet rs)
			{
				try
				{
					stopsForLine.add(new LineStation(rs.getString(1), lineID));
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}

			}
		}, reader.getPreparedStatement(
				"SELECT DISTINCT Station.stationID, min(Stop.timeTabledTime) AS firstTime FROM Station, Stop WHERE Station.stationID = Stop.stationID AND Stop.lineID = "
						+ lineID
						+ " AND Stop.timeTabledTime >= '2019-01-02 00:00:00' GROUP BY Station.stationID ORDER BY firstTime"));
	}

	public static void getAllStationNeighbours() throws SQLException, IOException
	{
		stationNeighbours = new HashMap<>();
		getRoutes();
		for (String s : stationNeighbours.keySet())
		{
			for (int i = 0; i < stationNeighbours.get(s).size() - 1; i++)
			{
				SimpleStation station = new SimpleStation(stationNeighbours.get(s).get(i));
				SimpleStation neighbour = new SimpleStation(stationNeighbours.get(s).get(i + 1));
				getStation(station).addNeighbourIfNotExists(neighbour);
			}
		}
	}

	private static SimpleStation getStation(SimpleStation station)
	{
		for (SimpleStation ss : stations)
		{
			if (ss.getID().equals(station.getID()))
			{
				return ss;
			}
		}
		return null;
	}

	private static void getRoutes()
	{
		try
		{
			File file = new File("stop_times.txt");
			FileReader fr;
			fr = new FileReader(file);
			BufferedReader reader = new BufferedReader(fr);
			while (reader.ready())
			{
				String line = reader.readLine();

				// Nur Zeilen mit Daten auslesen
				if (line.startsWith("\""))
				{
					String[] splitted = line.split("\",\"");
					String tripID = splitted[0].replaceAll("\"", "");
					String stationIDLong = splitted[3].replaceAll("\"", "");
					String[] stationIDSplitted = stationIDLong.split(":");
					String stationID = stationIDSplitted[0] + ":" + stationIDSplitted[1] + ":" + stationIDSplitted[2];

					stations.add(new SimpleStation(stationID));
					addStationToTrip(tripID, stationID);
				}
			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void addStationToTrip(String tripID, String stationID)
	{
		if (!stationNeighbours.keySet().contains(tripID))
		{
			stationNeighbours.put(tripID, new ArrayList<>());
		}
		stationNeighbours.get(tripID).add(stationID);
	}
}
