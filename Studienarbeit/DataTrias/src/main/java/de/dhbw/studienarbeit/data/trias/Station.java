package de.dhbw.studienarbeit.data.trias;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.dhbw.studienarbeit.data.helper.datamanagement.ApiKey;
import de.dhbw.studienarbeit.data.helper.datamanagement.DataModel;

public class Station implements DataModel
{
	private String stationID;
	private String name;
	private Double lat;
	private Double lon;
	private String operator;
	private List<Stop> previousStops = new ArrayList<>();
	private List<Stop> currentStops = new ArrayList<>();
	private Date nextUpdate;

	public Station(String stationID, String name, Double lat, Double lon, String operator)
	{
		this.stationID = stationID;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.operator = operator;
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

	public String getOperator()
	{
		return operator;
	}

	@Override
	public String getSQLQuerry()
	{
		return "";
	}

	@Override
	public Date nextUpdate()
	{
		return new Date();
	}

	@Override
	public void updateData(ApiKey apiKey) throws IOException
	{
		previousStops = currentStops;
		TriasXMLRequest request = new TriasXMLRequest(apiKey, this);
		currentStops = request.getResponse();
		checkNextUpdate();
	}

	private void checkNextUpdate()
	{
		if (nextUpdate == null)
		{
			nextUpdate = new Date();
			return;
		}
		
		Calendar cal = Calendar.getInstance();
		if (currentStops.isEmpty())
		{
			cal.setTime(nextUpdate);
			cal.add(Calendar.HOUR_OF_DAY, 2);
			nextUpdate = cal.getTime();
			return;
		}
		
		Stop nextStop = currentStops.get(0);
		cal.setTime(nextStop.getRealTime());
		cal.add(Calendar.MINUTE, -5);
		
		Calendar inFiveMinutes = Calendar.getInstance();
		inFiveMinutes.setTime(new Date());
		inFiveMinutes.add(Calendar.MINUTE, 5);
		
		if (cal.before(inFiveMinutes))
		{
			cal.add(Calendar.MINUTE, 4);
			nextUpdate = cal.getTime();
			return;
		}
		
		nextUpdate = cal.getTime();
	}
}
