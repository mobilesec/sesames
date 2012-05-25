package at.sesame.fhooe.notification;

import java.util.ArrayList;
import java.util.Date;

import android.util.Log;
import at.sesame.fhooe.lib2.data.EnhancedSesameNotification;
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;
import at.sesame.fhooe.lib2.data.semantic.RepositoryAccess;
import at.sesame.fhooe.lib2.data.semantic.SemanticSesameDataSource;
import at.sesame.fhooe.lib2.data.semantic.parsing.SemanticQueryResultParser;
import at.sesame.fhooe.lib2.data.semantic.parsing.SemanticRepoHelper;

public class NotificationCache 
{

	private static final String TAG = "NotificationCache";

	private static NotificationCache mInstance;
	private static SemanticSesameDataSource mSemanticSource;
	private static ArrayList<SesameMeasurementPlace> mLightPlaces = new ArrayList<SesameMeasurementPlace>();

	private static Date mDefaultFilterStartDate = new Date(110, 11, 1);
	
	private static Date mUserFilterStartDate;
	private static Date mUserFilterEndDate;
	
	private static boolean mUseUserFilter = false;

	private NotificationCache()
	{
		mSemanticSource = new SemanticSesameDataSource();
		mLightPlaces = mSemanticSource.getLightMeasurementPlaces();
	}

	public static NotificationCache getInstance()
	{
		if(null==mInstance)
		{
			mInstance = new NotificationCache();
		}

		return mInstance;
	}

	public ArrayList<EnhancedSesameNotification> getEnhancedNotifications(String _smpName, String _sensorId)
	{
		Date start = mDefaultFilterStartDate;
		Date end = new Date();
		if(mUseUserFilter)
		{
			if(null!=mUserFilterStartDate && null !=mUserFilterEndDate)
			{
				start = mUserFilterStartDate;
				end = mUserFilterEndDate;
			}
		}
		return getEnhancedNotifications(_smpName, _sensorId, start, end);
	}

	private synchronized ArrayList<EnhancedSesameNotification> getEnhancedNotifications(String _smpName, String _sensorId, Date _start, Date _end)
	{
		String query = SemanticRepoHelper.getEnhancedNotificationQuery(_smpName, _sensorId, _start, _end);
		Log.e(TAG, "Query: "+query);
		final ArrayList<EnhancedSesameNotification> nots = SemanticQueryResultParser.parseEnhancedNotifications(RepositoryAccess.executeQuery(query));
		//			Log.e(TAG, Arrays.toString((EnhancedSesameNotification[]) nots.toArray(new EnhancedSesameNotification[nots.size()])));
		//			for(EnhancedSesameNotification esn:nots)
		//			{
		//				Log.e(TAG, "NOTIFICATION: "+esn.toString());
		//			}
		return nots;

	}

	public static Date getStartFilterDate() {
		return mDefaultFilterStartDate;
	}

	public static void setStartFilterDate(Date _startFilterDate) {
		NotificationCache.mDefaultFilterStartDate = _startFilterDate;
	}

	public static ArrayList<SesameMeasurementPlace> getLightPlaces() {
		return mLightPlaces;
	}

	public static SesameMeasurementPlace getLightMeasurementPlaceByName(String _name)
	{
		for(SesameMeasurementPlace smp:mLightPlaces)
		{
			if(smp.getName().equals(_name))
			{
				return smp;
			}
		}
		return null;
	}
	
	public static void setUserDateFilters(Date _start, Date _end)
	{
		mUserFilterStartDate = _start;
		mUserFilterEndDate = _end;
	}
	
	public static void setUseUserFilter(boolean _use)
	{
		mUseUserFilter = _use;
	}
	
	public static Date getUserFilterStartDate()
	{
		return mUserFilterStartDate;
	}
	
	public static Date getUserFilterEndDate()
	{
		return mUserFilterEndDate;
	}

}
