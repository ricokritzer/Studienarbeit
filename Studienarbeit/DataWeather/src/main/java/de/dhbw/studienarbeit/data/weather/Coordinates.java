package de.dhbw.studienarbeit.data.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class Coordinates
{
	// Responsemode could be html, xml or (default) JSON
	private final String urlEnd = "&appid=b5923a1132896eba486d603bc6602a5f&mode=xml&units=metric";
	private final String urlPre = "https://api.openweathermap.org/data/2.5/weather?";
	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	private double lat;
	private double lon;

	private double temp;
	private double humitdity;
	private double pressure;
	private double wind;
	private double clouds;

	private URL requestURL;

	public Coordinates(final double lat, final double lon)
	{
		this.lat = lat;
		this.lon = lon;

		try
		{
			final String dynamicURL = urlPre + "lat=" + lat + "&lon=" + lon + urlEnd;
			requestURL = new URL(dynamicURL);
			updateData();
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
		}
	}

	public void updateData() throws IOException
	{
		try
		{
			final URLConnection con = requestURL.openConnection();
			con.setDoOutput(true);
			con.setConnectTimeout(1000); // long timeout, but not infinite
			con.setReadTimeout(3000);
			con.setUseCaches(false);
			con.setDefaultUseCaches(false);

			con.connect();
			waitForResponse(con);
		}
		catch (MalformedURLException e)
		{
			// ignore - never become true
		}

	}

	private void waitForResponse(URLConnection connection) throws IOException
	{
		try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream())))
		{
			final StringBuilder sbResponse = new StringBuilder();
			while (br.ready())
			{
				sbResponse.append(br.readLine());
			}
			setData(sbResponse.toString());
		}
	}

	void setData(final String response)
	{
		try
		{
			temp = Double.valueOf(extractDate(response, "temperature"));
			humitdity = Double.valueOf(extractDate(response, "humidity"));
			pressure = Double.valueOf(extractDate(response, "pressure"));
			wind = Double.valueOf(extractDate(response, "speed"));
			clouds = Double.valueOf(extractDate(response, "clouds"));
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
		}
	}

	private String extractDate(final String xmlText, final String tag) throws IOException
	{
		try
		{
			final DocumentBuilder parser = factory.newDocumentBuilder();
			final Document doc = parser.parse(new InputSource(new StringReader(xmlText)));
			final Node node = doc.getElementsByTagName(tag).item(0).getAttributes().getNamedItem("value");

			return node.getNodeValue();
		}
		catch (Exception e)
		{
			throw new IOException("Error in xml: Tag '" + tag + "' with parameter 'value' not found", e);
		}
	}

	@Override
	public String toString()
	{
		return "lon:\t" + lon + "\tlat:\t" + lat + "\ttemp:\t" + temp;
	}

	public double getLat()
	{
		return lat;
	}

	public double getLon()
	{
		return lon;
	}

	public double getTemp()
	{
		return temp;
	}

	public double getHumitdity()
	{
		return humitdity;
	}

	public double getPressure()
	{
		return pressure;
	}

	public double getWind()
	{
		return wind;
	}

	public double getClouds()
	{
		return clouds;
	}
}