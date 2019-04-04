package de.dhbw.studienarbeit.WebView.overview;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vaadin.gatanaso.MultiselectComboBox;

import com.syndybat.chartjs.ChartJs;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import be.ceau.chart.BarChart;
import be.ceau.chart.color.Color;
import be.ceau.chart.data.BarData;
import be.ceau.chart.dataset.BarDataset;
import be.ceau.chart.options.BarOptions;
import be.ceau.chart.options.scales.BarScale;
import be.ceau.chart.options.scales.YAxis;
import be.ceau.chart.options.ticks.LinearTicks;
import de.dhbw.studienarbeit.WebView.components.DateTimePicker;
import de.dhbw.studienarbeit.data.reader.data.Delay;
import de.dhbw.studienarbeit.data.reader.data.DelayAverage;
import de.dhbw.studienarbeit.data.reader.data.DelayMaximum;
import de.dhbw.studienarbeit.data.reader.data.count.CountData;
import de.dhbw.studienarbeit.data.reader.data.line.Line;
import de.dhbw.studienarbeit.data.reader.data.request.DelayCountData;
import de.dhbw.studienarbeit.data.reader.data.request.DelayRequestFixedTime;
import de.dhbw.studienarbeit.data.reader.data.request.InvalidTimeSpanException;
import de.dhbw.studienarbeit.data.reader.data.station.DelayStationData;
import de.dhbw.studienarbeit.data.reader.data.station.OperatorName;
import de.dhbw.studienarbeit.data.reader.data.station.Position;
import de.dhbw.studienarbeit.data.reader.data.station.StationID;
import de.dhbw.studienarbeit.data.reader.data.station.StationName;
import de.dhbw.studienarbeit.web.data.Data;

@Route("analyseFixedTime")
public class AnalyseFixedTimeOverview extends Overview
{
	private static final int exponential = 500;

	private static final int maxItems = 100;

	private static final long serialVersionUID = 1L;

	private ComboBox<DelayStationData> stations;
	private MultiselectComboBox<Line> lines;
	private DateTimePicker begin;
	private DateTimePicker end;
	private Button btnSearch;

	private DelayRequestFixedTime request;

	private List<Component> filters = new ArrayList<>();

	private VerticalLayout layout;

	private Div divChart = new Div();

	@SuppressWarnings("rawtypes")
	public AnalyseFixedTimeOverview()
	{
		super();

		begin = new DateTimePicker(LocalTime.of(0, 0));
		begin.setLabel("von");
		end = new DateTimePicker(LocalTime.of(23, 59));
		end.setLabel("bis");

		btnSearch = new Button("Suchen", e -> search());
		btnSearch.setEnabled(false);

		lines = new MultiselectComboBox<>();
		lines.setLabel("Linien");
		lines.setItemLabelGenerator(item -> item.toString());
		filters.add(lines);

		filters.add(begin);
		filters.add(end);

		filters.forEach(e -> ((HasValueAndElement) e).setReadOnly(true));

		// Zum Testen:
		DelayStationData test = new DelayStationData(new DelayMaximum(0), new DelayAverage(0),
				new StationID("de:08212:1"), new StationName("Test Marktplatz"), new OperatorName("kvv"),
				new Position(0, 0), 0);
		stations = new ComboBox<>("Station", test);

		// stations = new ComboBox<>("Station", Data.getDelaysStation());
		stations.setItemLabelGenerator(item -> item.getName().toString());
		stations.addValueChangeListener(e -> createRequest());

		HorizontalLayout filter = new HorizontalLayout(stations);
		filter.setHeight("100px");
		filter.setAlignItems(Alignment.CENTER);

		Div divLines = new Div(lines);
		divLines.setWidth("25%");

		filter.add(divLines, begin, end, btnSearch);

		layout = new VerticalLayout(filter);
		layout.setSizeFull();
		layout.setAlignItems(Alignment.STRETCH);
		divChart.setWidth("80%");
		layout.add(divChart);

		setContent(layout);
	}

	private void search()
	{
		end.setInvalid(false);
		try
		{
			request.setLines(lines.getValue());
			request.setTimestampStart(begin.getOptionalValue());
			request.setTimestampEnd(end.getOptionalValue());

			showDelays(request.getDelayCounts());
		}
		catch (InvalidTimeSpanException itse)
		{
			end.setErrorMessage("Ende darf nicht vor Beginn liegen");
			end.setInvalid(true);
		}
		catch (IOException e)
		{
			Notification.show("Fehler beim Suchen der Verspätungen");
		}
	}

	private void showDelays(List<DelayCountData> delays)
	{
		DelayCountData[] data = getData(delays);

		if (data.length == 0)
		{
			Notification.show("Ihre Suche ergibt keine Ergebnisse");
			return;
		}

		BigDecimal[] delaysAdded = getDelays(data);
		BarDataset dataset = new BarDataset().setData(delaysAdded).setLabel("Verspätungen")
				.setBackgroundColor(Color.BLUE).setBorderColor(Color.BLUE);

		BarData barData = new BarData().addLabels(getLabels(data)).addDataset(dataset);

		BarChart chart = new BarChart().setData(barData);

		if (greatestValueIsMoreThan(exponential, delaysAdded))
		{
			BarOptions options = new BarOptions().setScales(new BarScale().setyAxes(getYAxis()));
			chart.setOptions(options);
		}

		ChartJs chartJS = new ChartJs(chart.toJson());

		divChart.removeAll();
		divChart.add(chartJS);
	}

	private boolean greatestValueIsMoreThan(int max, BigDecimal[] delaysAdded)
	{
		return delaysAdded[0].compareTo(BigDecimal.valueOf(max)) > 0;
	}

	private BigDecimal[] getDelays(DelayCountData[] data)
	{
		BigDecimal[] delayArray = new BigDecimal[data.length];
		delayArray[delayArray.length - 1] = BigDecimal.valueOf(data[data.length - 1].getCountValue());

		for (int i = delayArray.length - 2; i >= 0; i--)
		{
			delayArray[i] = BigDecimal.valueOf(data[i].getCountValue()).add(delayArray[i + 1]);
		}

		return delayArray;
	}

	private String[] getLabels(DelayCountData[] data)
	{
		return Arrays.asList(data).stream().map(e -> "> " + e.getDelayInMinutes() + " min").toArray(String[]::new);
	}

	protected static DelayCountData[] getData(List<DelayCountData> delays)
	{
		if (delays.isEmpty())
		{
			return new DelayCountData[0];
		}

		int begin = delays.get(0).getDelayInMinutes();
		int end = delays.get(delays.size() - 1).getDelayInMinutes();

		DelayCountData[] data = new DelayCountData[(end - begin) + 1];

		for (int i = data.length - 1; i >= 0; i--)
		{
			final int idx = i;
			data[i] = delays.stream().filter(e -> e.getDelayInMinutes() == (idx + begin)).findFirst()
					.orElse(new DelayCountData(new Delay((i + begin) * 60), new CountData(0)));
		}

		int max = data.length > maxItems ? maxItems : data.length;
		DelayCountData[] shorterArray = Arrays.copyOfRange(data, 0, max);

		return shorterArray;
	}

	private List<YAxis<LinearTicks>> getYAxis()
	{
		List<YAxis<LinearTicks>> axis = new ArrayList<>();
		YAxis<LinearTicks> ticks = new YAxis<>();
		ticks.setType("logarithmic");
		ticks.setStacked(true);
		axis.add(ticks);
		return axis;
	}

	@SuppressWarnings("rawtypes")
	private void createRequest()
	{
		if (!stations.getOptionalValue().isPresent())
		{
			filters.forEach(e -> ((HasValueAndElement) e).setReadOnly(true));
			btnSearch.setEnabled(false);
			return;
		}

		request = Data.getDelayRequestForFixedTimeFor(stations.getValue().getStationID());
		lines.setItems(Data.getLinesAt(stations.getValue().getStationID()));

		filters.forEach(e -> ((HasValueAndElement) e).setReadOnly(false));
		btnSearch.setEnabled(true);
	}
}
