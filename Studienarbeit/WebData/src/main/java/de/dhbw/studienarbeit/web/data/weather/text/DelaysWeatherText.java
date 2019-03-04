package de.dhbw.studienarbeit.web.data.weather.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.dhbw.studienarbeit.data.reader.database.DelayWeatherTextDB;
import de.dhbw.studienarbeit.web.data.update.DataUpdater;
import de.dhbw.studienarbeit.web.data.update.Updateable;

public class DelaysWeatherText extends Updateable
{
	private List<DelayWeatherTextDB> data = new ArrayList<>();

	public DelaysWeatherText(Optional<DataUpdater> updater)
	{
		updater.ifPresent(u -> u.updateEvery(3, HOURS, this));
	}

	public List<DelayWeatherTextDB> getData()
	{
		return data;
	}

	@Override
	protected void updateData() throws IOException
	{
		data = DelayWeatherTextDB.getDelays();
	}
}