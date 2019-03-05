package de.dhbw.studienarbeit.web.data.weather.clouds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.dhbw.studienarbeit.data.reader.data.weather.DelayCloudsData;
import de.dhbw.studienarbeit.data.reader.database.DelayCloudsDB;
import de.dhbw.studienarbeit.web.data.update.DataUpdater;
import de.dhbw.studienarbeit.web.data.update.Updateable;

public class DelayCloudsWO extends Updateable
{
	private List<DelayCloudsDB> data = new ArrayList<>();

	public DelayCloudsWO(Optional<DataUpdater> updater)
	{
		updater.ifPresent(u -> u.updateEvery(3, HOURS, this));
	}

	public List<DelayCloudsData> getData()
	{
		return new ArrayList<>(data);
	}

	@Deprecated
	public List<DelayCloudsDB> getList()
	{
		return data;
	}

	@Override
	protected void updateData() throws IOException
	{
		data = DelayCloudsDB.getDelays();
	}
}
