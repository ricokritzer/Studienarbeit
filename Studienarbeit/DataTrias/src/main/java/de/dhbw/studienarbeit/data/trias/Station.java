package de.dhbw.studienarbeit.data.trias;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.dhbw.studienarbeit.data.helper.DataModel;

public class Station implements DataModel
{
	private String stationID;
	private String name;
	private Double lat;
	private Double lon;
	
	public Station(String stationID, String name, Double lat, Double lon)
	{
		this.stationID = stationID;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}
	
	public String getStationID()
	{
		return stationID;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Double getLat()
	{
		return lat;
	}
	
	public Double getLon()
	{
		return lon;
	}

	@Override
	public String getSQLQuerry()
	{
		return "INSERT INTO Station " + values() + ";";
	}

	private String values()
	{
		return "values ('" + stationID + "', '" + name + "', " + lat + ", " + lon + ")";
	}

	@Override
	public void updateData(int attempts) throws IOException
	{
		
	}

	@Override
	public Date nextUpdate()
	{
		return new Date();
	}

}
