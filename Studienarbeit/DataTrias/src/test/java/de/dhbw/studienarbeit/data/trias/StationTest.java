package de.dhbw.studienarbeit.data.trias;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class StationTest
{

	@Test
	void equalMethodReturnsTrue_forTwoStops_ThatAreEqual()
	{
		Stop stop1 = new Stop("test", "test", "test", new Date(1000), Optional.ofNullable(new Date(2000)));
		Stop stop2 = new Stop("test", "test", "test", new Date(1000), Optional.ofNullable(new Date(2000)));
		assertThat(stop1.equals(stop1), is(true));
		assertThat(stop1.equals(stop2), is(true));
	}
	
	@Test
	void equalMethodReturnsTrue_forTwoStops_ThatHaveDifferentRealTime()
	{
		Stop stop1 = new Stop("test", "test", "test", new Date(1000), Optional.ofNullable(new Date(2000)));
		Stop stop2 = new Stop("test", "test", "test", new Date(1000), Optional.ofNullable(new Date(3000)));
		assertThat(stop1.equals(stop2), is(true));
	}
}
