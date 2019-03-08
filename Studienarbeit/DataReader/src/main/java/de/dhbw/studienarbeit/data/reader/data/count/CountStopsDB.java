package de.dhbw.studienarbeit.data.reader.data.count;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dhbw.studienarbeit.data.reader.database.DatabaseReader;

public class CountStopsDB implements CountStops
{
	private static final Logger LOGGER = Logger.getLogger(CountStopsDB.class.getName());

	@Override
	public Count countStops()
	{
		try
		{
			return new DatabaseReader().count("SELECT count(*) AS total FROM Stop;");
		}
		catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Unable to count stops.", e);
			return Count.UNABLE_TO_COUNT;
		}
	}
}