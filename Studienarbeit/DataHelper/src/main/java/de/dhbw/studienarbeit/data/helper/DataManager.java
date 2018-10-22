package de.dhbw.studienarbeit.data.helper;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DataManager
{
	private final Saver saver = new Saver();
	private final Timer timer;

	public DataManager(List<DataModel> models)
	{
		timer = new Timer();
		for (DataModel dataModel : models)
		{
			updateAndSave(dataModel);
			scheduleUpdate(dataModel);
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				// ignorieren
			}
		}
	}

	private void scheduleUpdate(DataModel model)
	{
		TimerTask task = new TimerTask()
		{
			@Override
			public void run()
			{
				updateAndSave(model);
			}
		};

		timer.schedule(task, model.nextUpdate());
	}

	private void updateAndSave(DataModel model)
	{
		try
		{
			model.updateData(3);
			saver.save(model);
		}
		catch (IOException e)
		{
			saver.logError(e);
		}
	}

	public void stop()
	{
		timer.cancel();
	}
}
