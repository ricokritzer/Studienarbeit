package de.dhbw.studienarbeit.data.reader.database;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class DelayLineDBTest
{
	@Test
	void testSelectingDelays() throws Exception
	{
		List<DelayLineDB> list = DelayLineDB.getDelays();
		assertTrue(list.size() > 0);
	}
}
