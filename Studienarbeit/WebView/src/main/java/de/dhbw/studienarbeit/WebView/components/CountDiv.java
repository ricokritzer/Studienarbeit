package de.dhbw.studienarbeit.WebView.components;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Timer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.communication.PushMode;

import de.dhbw.studienarbeit.data.helper.datamanagement.MyTimerTask;
import de.dhbw.studienarbeit.data.reader.database.Count;
import de.dhbw.studienarbeit.web.data.Counts;
import de.dhbw.studienarbeit.web.data.Data;

public class CountDiv extends Div
{
	private static final long serialVersionUID = 3L;

	private final Grid<Counts> grid = new Grid<>();

	public CountDiv()
	{
		super();
		setSizeFull();

		VerticalLayout layout = new VerticalLayout();

		grid.addColumn(db -> getStringOf(db.getCountLines())).setHeader("Linien").setSortable(false);
		grid.addColumn(db -> getStringOf(db.getCountOperators())).setHeader("Verkehrsverbünde").setSortable(false);
		grid.addColumn(db -> getStringOf(db.getCountStations())).setHeader("Stationen").setSortable(false);
		grid.addColumn(db -> getStringOf(db.getCountStops())).setHeader("Stops").setSortable(false);
		grid.addColumn(db -> getStringOf(db.getCountWeathers())).setHeader("Wettereinträge").setSortable(false);
		grid.addColumn(db -> new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(db.getLastUpdate()))
				.setHeader("Zeitpunkt").setSortable(false);

		grid.setHeightByRows(true);
		grid.setSizeFull();

		layout.add(grid);

		add(layout);

		setVisible(false);

		final Timer timer = new Timer();
		timer.schedule(new MyTimerTask(this::update), new Date(), 1000);
	}

	private String getStringOf(Count value)
	{
		if (value.equals(Count.UNABLE_TO_COUNT))
		{
			return "NaN";
		}
		return new DecimalFormat().format(value);
	}

	public void update()
	{
		UI ui = getUI().orElse(UI.getCurrent());
		Optional.ofNullable(ui).ifPresent(currentUI -> currentUI.access(() -> {
			currentUI.getPushConfiguration().setPushMode(PushMode.MANUAL);
			grid.setItems(Data.getCounts());
			currentUI.push();
		}));
	}
}
