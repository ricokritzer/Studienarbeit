package de.dhbw.studienarbeit.WebView.components;

import java.text.SimpleDateFormat;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.shared.communication.PushMode;

import de.dhbw.studienarbeit.WebView.data.DelayBean;
import de.dhbw.studienarbeit.WebView.data.DelayDataProvider;

@PageTitle("Verspätungen")
public class DelayDiv extends Div
{
	private static final long serialVersionUID = 1L;

	private static final int SECONDS_PER_MINUTE = 60;
	private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60;
	private static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;

	private final TextField txtDelaySum = new TextField();
	private final TextField txtDelayAvg = new TextField();
	private final TextField txtDelayMax = new TextField();
	private final TextField txtLastUpdate = new TextField();

	private Binder<DelayBean> delayBinder = new Binder<>();

	private DelayDataProvider dataProvider;

	public DelayDiv()
	{
		super();

		VerticalLayout layout = new VerticalLayout();

		delayBinder.forField(txtDelayMax).bind(db -> convertTimeToString(db.getMax()), null);
		delayBinder.forField(txtDelayAvg).bind(db -> convertTimeToString(db.getAvg()), null);
		delayBinder.forField(txtDelaySum).bind(db -> convertTimeToString(db.getSum()), null);
		delayBinder.forField(txtLastUpdate)
				.bind(db -> new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(db.getLastUpdate()), null);

		txtDelayMax.setLabel("Maximal");
		txtDelayMax.setReadOnly(true);
		layout.add(txtDelayMax);

		txtDelayAvg.setLabel("Durchschnitt");
		txtDelayAvg.setReadOnly(true);
		layout.add(txtDelayAvg);

		txtDelaySum.setLabel("Summe");
		txtDelaySum.setReadOnly(true);
		layout.add(txtDelaySum);

		txtLastUpdate.setLabel("Stand");
		txtLastUpdate.setReadOnly(true);
		layout.add(txtLastUpdate);

		add(layout);

		dataProvider = DelayDataProvider.getInstance();
		dataProvider.getDataFor(this);

		setVisible(false);
	}

	public void update(DelayBean bean)
	{
		try // TODO is this neccessary?
		{
			getUI().ifPresent(ui -> ui.access(() -> {
				ui.getPushConfiguration().setPushMode(PushMode.MANUAL);
				delayBinder.readBean(bean);
				ui.push();
			}));
		}
		catch (Exception e) // TODO which Exception?
		{
			e.printStackTrace(); // TODO log it.
		}
		finally
		{
			dataProvider.readyForUpdate(this);
		}
	}

	private String convertTimeToString(double time)
	{
		if (time > SECONDS_PER_DAY)
		{
			final double days = time / SECONDS_PER_DAY;
			return Double.toString(round(days)) + " Tage";
		}
		if (time > SECONDS_PER_HOUR)
		{
			final double hours = time / SECONDS_PER_HOUR;
			return Double.toString(round(hours)) + " Stunden";
		}
		if (time > SECONDS_PER_MINUTE)
		{
			final double minutes = time / SECONDS_PER_MINUTE;
			return Double.toString(round(minutes)) + " Minuten";
		}
		return Double.toString(time) + " Sekunden";
	}

	private double round(double value)
	{
		return Math.round(value * 100) / 100.0;
	}
}
