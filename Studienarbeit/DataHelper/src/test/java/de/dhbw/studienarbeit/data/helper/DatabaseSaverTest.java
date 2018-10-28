package de.dhbw.studienarbeit.data.helper;

import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Date;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

public class DatabaseSaverTest
{
	private final static String TEXT = "bla bla bla - auf jeden Fall ein fehlerhaftes SQL-Statement";
	private final DataModel model = new DataModel()
	{
		@Override
		public void updateData(int attempts) throws IOException
		{
			// do nothing
		}

		@Override
		public Date nextUpdate()
		{
			// do nothing
			return null;
		}

		@Override
		public String getSQLQuerry()
		{
			return TEXT;
		}
	};

	private String savedText;
	private final Saver saverMock = new Saver()
	{
		@Override
		public void save(DataModel model)
		{
			savedText = model.getSQLQuerry();
		}
	};

	@Test
	void testSavingWrongSQLStatement() throws Exception
	{
		DatabaseSaver saver = new DatabaseSaver();
		saver.setSaverForErrors(saverMock);
		saver.save(model);
		assertThat(savedText, Is.is(TEXT));
	}
}