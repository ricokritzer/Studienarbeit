package de.dhbw.studienarbeit.web.data.counts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import de.dhbw.studienarbeit.data.reader.database.Count;
import de.dhbw.studienarbeit.web.data.update.DataUpdater;
import de.dhbw.studienarbeit.web.data.update.Updateable;

public class CountList extends Updateable
{
	private static final int MAX_COUNT_ITEMS = 10;

	private List<Counts> data = new ArrayList<>();

	public CountList(Optional<DataUpdater> updater)
	{
		updater.ifPresent(u -> u.updateEvery(5, MINUTES, this));
	}

	public final List<Counts> getData()
	{
		return data;
	}

	@Override
	protected void updateData() throws IOException
	{
		final Count countStations = Count.countStations();
		final Count countObservedStations = Count.countObservedStations();
		final Count countStationsWithRealtimeData = Count.countStationsWithRealtimeData();
		final Count countLines = Count.countLines();
		final Count countStops = Count.countStops();
		final Count countWeathers = Count.countWeather();
		final Count countOperators = Count.countObservedOperators();
		final Date lastUpdate = new Date();

		data.add(0, new Counts(countStations, countObservedStations, countStationsWithRealtimeData, countLines,
				countStops, countWeathers, countOperators, lastUpdate));

		reduceListElements(data);
	}

	protected static void reduceListElements(List<? extends Object> list)
	{
		while (list.size() > MAX_COUNT_ITEMS)
		{
			list.remove(MAX_COUNT_ITEMS);
		}
	}
}