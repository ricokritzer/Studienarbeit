package de.dhbw.studienarbeit.data.helper.database.saver;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dhbw.studienarbeit.data.helper.Settings;
import de.dhbw.studienarbeit.data.helper.database.DatabaseConnector;

public class DatabaseSaver extends DatabaseConnector implements Saver
{
	private static final Logger LOGGER = Logger.getLogger(DatabaseSaver.class.getName());

	private static final DatabaseSaver INSTANCE = new DatabaseSaver();

	private static final String fileName = "errors.txt";

	private static final TextSaver saver = new TextSaver(fileName);

	public static DatabaseSaver getInstance()
	{
		return INSTANCE;
	}

	@Override
	protected void connectToDatabase() throws SQLException
	{
		connectToDatabase(Settings.getInstance().getDatabaseHostname(), Settings.getInstance().getDatabasePort(),
				Settings.getInstance().getDatabaseName(), Settings.getInstance().getDatabaseWriterUser(),
				Settings.getInstance().getDatabaseWriterPassword());
	}

	@Override
	public void save(Saveable model) throws IOException
	{
		final String sqlQuerry = model.getSQLQuerry();
		if (sqlQuerry.isEmpty())
		{
			LOGGER.log(Level.INFO, "Empty SQLQuerry at " + model.toString());
			return;
		}

		reconnectIfNeccessary();

		String sql = "Building SQL failed.";

		try (PreparedStatement statement = connection.prepareStatement(sqlQuerry))
		{
			model.setValues(statement);
			sql = statement.toString();
			statement.executeUpdate();
			LOGGER.log(Level.FINE, model.toString() + " saved.");
		}
		catch (SQLException e)
		{
			final String whatHappens = new StringBuilder().append("Unable to save ").append(model)
					.append(". Saving SQL in ").append(fileName).append(". SQL: ").append(sql).toString();
			LOGGER.log(Level.WARNING, whatHappens, e);
			saver.write(sql);
			throw new IOException(whatHappens, e);
		}
	}
}
