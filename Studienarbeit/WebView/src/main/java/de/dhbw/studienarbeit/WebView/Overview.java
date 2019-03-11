package de.dhbw.studienarbeit.WebView;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.icon.VaadinIcon;

public abstract class Overview extends AppLayout
{
	private static final long serialVersionUID = 1L;

	public Overview()
	{
		super();
		
		AppLayoutMenuItem itemWelcome = new AppLayoutMenuItem(VaadinIcon.HOME.create(), "Willkommen", "");
		AppLayoutMenuItem itemMap = new AppLayoutMenuItem(VaadinIcon.GLOBE.create(), "Karte", "map");
		AppLayoutMenuItem itemWeather = new AppLayoutMenuItem(VaadinIcon.CLOUD.create(), "Wetter", "weather");
		AppLayoutMenuItem itemTime = new AppLayoutMenuItem(VaadinIcon.CLOCK.create(), "Zeit", "time");
		
		AppLayoutMenu menu = new AppLayoutMenu();
		menu.addMenuItems(itemWelcome, itemMap, itemWeather, itemTime);
		
		setMenu(menu);
	}
}
