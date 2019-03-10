package de.dhbw.studienarbeit.WebView.components;

import java.text.SimpleDateFormat;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;

import de.dhbw.studienarbeit.data.reader.data.weather.temperature.DelayTemperatureData;
import de.dhbw.studienarbeit.web.data.Data;

public class DelayTemperatureDiv extends Div
{
	private static final long serialVersionUID = 6L;

	private final Grid<DelayTemperatureData> grid = new Grid<>();
	private final TextField field = new TextField();

	public DelayTemperatureDiv()
	{
		super();
		setSizeFull();

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		field.setLabel("Stand");
		field.setReadOnly(true);
		field.setValue(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Data.getDelayTemperatureWO().getLastUpdated()));
		layout.add(field);

		grid.addColumn(db -> db.getValue()).setHeader("Temperatur")
				.setComparator((db1, db2) -> Double.compare(db1.getValue(), db2.getValue())).setSortable(true);
		grid.addColumn(db -> db.getAverage().toString()).setHeader("Durchschnitt")
				.setComparator((db1, db2) -> Double.compare(db1.getAverage().getValue(), db2.getAverage().getValue())).setSortable(true);
		grid.addColumn(db -> db.getMaximum().toString()).setHeader("Maximal")
				.setComparator((db1, db2) -> Double.compare(db1.getMaximum().getValue(), db2.getMaximum().getValue())).setSortable(true);

		grid.setSizeFull();
		grid.setSelectionMode(SelectionMode.NONE);
		grid.setDataProvider(DataProvider.ofCollection(Data.getDelayTemperatureWO().getData()));

		layout.add(grid);
		add(layout);

		setVisible(false);
	}
}
