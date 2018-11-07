package de.dhbw.studienarbeit.data.helper.database.table;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class DatabaseTableLineTest
{
	@Test
	void testCounting() throws Exception
	{
		try
		{
			new DatabaseTableLine().count();
		}
		catch (IOException e)
		{
			fail("Unable to count stations" + e.getMessage());
		}
	}
}