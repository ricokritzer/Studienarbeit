package de.dhbw.studienarbeit.data.helper.datamanagement;

import java.util.Date;

public interface Manageable
{
	void updateAndSaveData(final ApiKeyData apiKey) throws UpdateException, ServerNotAvailableException;

	Date nextUpdate();
}
