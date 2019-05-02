/* 
 * Copyright(c) 2018-2019 hdactech.com
 * Original code was distributed under the MIT software license.
 *
 */

package com.hdac.comparator;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class will be moved to the contractlib
 * Deprecated
 * 
 * @version 0.8
 * @see     java.util.Comparator
 */
public class JsonComparator implements Comparator<JSONObject>
{
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(JSONObject o1, JSONObject o2)
	{
		try
		{
			if (o2.has("confirmations") && (o2.getLong("confirmations") == 0))
			{
				if (o1.has("confirmations") && (o1.getLong("confirmations") == 0))
					return 0;
				return 1;
			}

			if (o1.has("time") && o2.has("time"))
			{
				long time1 = o1.getLong("time");
				long time2 = o2.getLong("time");

				return (int)(time2 - time1);
			}
		}
		catch (JSONException e)
		{
		}
		return 0;
	}
}