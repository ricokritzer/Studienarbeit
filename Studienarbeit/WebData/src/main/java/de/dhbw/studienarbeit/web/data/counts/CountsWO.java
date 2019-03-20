package de.dhbw.studienarbeit.web.data.counts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import de.dhbw.studienarbeit.data.reader.data.count.CountData;
import de.dhbw.studienarbeit.web.data.Data;
import de.dhbw.studienarbeit.web.data.update.DataUpdater;
import de.dhbw.studienarbeit.web.data.update.Updateable;

@Deprecated
public class CountsWO extends Updateable
{
	private static final int MAX_COUNT_ITEMS = 10;

	private List<Counts> data = new ArrayList<>();

	public CountsWO(Optional<DataUpdater> updater)
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
		final CountData stations = Data.getCountStationsLatest();
		final CountData observedStations = Data.getCountObservedStationsLatest();
		final CountData stationsWithRealtimeData = Data.getCountStationsWithRealtimeDataLatest();
		final CountData lines = Data.getCountLinesLatest();
		final CountData stops = Data.getCountStopsLatest();
		final CountData weather = Data.getCountWeathersLatest();
		final CountData operators = Data.getCountObservedOperatorsLatest();
		final Date lastUpdate = new Date();

		data.add(0, new Counts(stations, observedStations, stationsWithRealtimeData, lines, stops, weather, operators,
				lastUpdate));

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
