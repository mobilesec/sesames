package at.sesame.fhooe.notification;


import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;


public class NotificationAppActivity 
extends Activity
implements IFilterResultReceiver
{	
	private static final String TAG = "NotificationAppActivity";
	private ArrayList<SesameNotificationFragment> mFrags = new ArrayList<SesameNotificationFragment>();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_app_layout);
		ImageButton filterButt = (ImageButton)findViewById(R.id.notification_app_filter_settings_butt);
		filterButt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FilterDialog fd = new FilterDialog();
				fd.setResultReceiver(NotificationAppActivity.this);
				fd.show(getFragmentManager(), null);
			}
		});
		
		CheckBox filterToggle = (CheckBox)findViewById(R.id.notification_app_filter_toggle_butt);
		filterToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				NotificationCache.setUseUserFilter(isChecked);
				updateAllFrags();
			}
		});
		
		ImageButton refreshButt = (ImageButton)findViewById(R.id.notification_app_refresh_butt);
		refreshButt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateAllFrags();
			}
		});
//		GregorianCalendar start = new GregorianCalendar(2012,4,1);
		try {
			new SetupTask().execute(new Void[]{}).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
		//        SesameDataCache cache = SesameDataCache.getInstance();
		//        Log.e(TAG, "cache created");
		//        String placeName = SesameDataCache.EDV1_PLACE.getName();
		//        Log.e(TAG, "placeName: "+placeName);
		//        String sensorName = SesameDataCache.EDV1_PLACE.getSensors().get(0).getId();
		//        Log.e(TAG, "sensorName: "+sensorName);

//		SesameMeasurementPlace lightPlace = SesameDataCache.getInstance().getLightMeasurementPlaces().get(0);
//		Log.e(TAG, lightPlace.toString());
//		//        Log.e(TAG, SemanticRepoHelper.getEnhancedNotificationQuery(placeName, sensorName, start.getTime(), new Date()));
//
//
		
	}
	
	private class SetupTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
//			SesameDataCache.createInstance(NotificationAppActivity.this);
//			GregorianCalendar start = new GregorianCalendar(2012,4,1);
//			ArrayList<SesameMeasurementPlace> places = SesameDataCache.getInstance().getLightMeasurementPlaces();
//			for(SesameMeasurementPlace smp:places)
//			{
//				Log.e(TAG, smp.toString());
//				for(SesameSensor ss:smp.getSensors())
//				{
////					Log.e(TAG, ss.toString());
//					String query = SemanticRepoHelper.getEnhancedNotificationQuery(smp.getName(), ss.getId(), start.getTime(), new Date());
//					Log.e(TAG, "Query: "+query);
//					ArrayList<EnhancedSesameNotification> nots = SemanticQueryResultParser.parseEnhancedNotifications(RepositoryAccess.executeQuery(query));
////					Log.e(TAG, Arrays.toString((EnhancedSesameNotification[]) nots.toArray(new EnhancedSesameNotification[nots.size()])));
//					for(EnhancedSesameNotification esn:nots)
//					{
//						Log.e(TAG, "NOTIFICATION: "+esn.toString());
//					}
//				}
//			}
//			Log.e(TAG, SemanticRepoHelper.getEnhancedNotificationQuery(SesameDataCache.EDV1_PLACE.getName(), SesameDataCache.getInstance().getl, start.getTime(), new Date()));
			NotificationCache.getInstance();
			for(SesameMeasurementPlace smp:NotificationCache.getLightPlaces())
			{
				Log.e(TAG, smp.getName());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			mFrags.clear();
			Bundle b = new Bundle();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			
			SesameNotificationFragment edv1Frag = new SesameNotificationFragment();
			b.putString(SesameNotificationFragment.MEASUREMENT_PLACE_BUNDLE_KEY, NotificationCache.getLightPlaces().get(1).getName());
			edv1Frag.setArguments((Bundle) b.clone());
			mFrags.add(edv1Frag);
			
			SesameNotificationFragment edv3Frag = new SesameNotificationFragment();
			b.putString(SesameNotificationFragment.MEASUREMENT_PLACE_BUNDLE_KEY, NotificationCache.getLightPlaces().get(2).getName());
			edv3Frag.setArguments((Bundle) b.clone());
			mFrags.add(edv3Frag);
			
			SesameNotificationFragment edv6Frag = new SesameNotificationFragment();
			b.putString(SesameNotificationFragment.MEASUREMENT_PLACE_BUNDLE_KEY, NotificationCache.getLightPlaces().get(3).getName());
			edv6Frag.setArguments((Bundle) b.clone());
			mFrags.add(edv6Frag);
			
			SesameNotificationFragment class1Frag = new SesameNotificationFragment();
			b.putString(SesameNotificationFragment.MEASUREMENT_PLACE_BUNDLE_KEY, NotificationCache.getLightPlaces().get(0).getName());
			class1Frag.setArguments((Bundle) b.clone());
			mFrags.add(class1Frag);
			
			SesameNotificationFragment miscFrag = new SesameNotificationFragment();
			b.putString(SesameNotificationFragment.MEASUREMENT_PLACE_BUNDLE_KEY, NotificationCache.getLightPlaces().get(4).getName());
			miscFrag.setArguments((Bundle) b.clone());
			mFrags.add(miscFrag);
			
//			Fragment edv1Frag = new SesameNotificationFragment();
//			b.putString(SesameNotificationFragment.MEASUREMENT_PLACE_BUNDLE_KEY, NotificationCache.getLightPlaces().get(1).getName());
			ft.add(R.id.edv1Frame, edv1Frag);
			ft.add(R.id.edv3Frame, edv3Frag);
			ft.add(R.id.edv6Frame, edv6Frag);
			ft.add(R.id.class1Frame, class1Frag);
			ft.add(R.id.miscFrame, miscFrag);
			
			ft.commit();
		}
		
		
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		SesameDataCache.cleanUp();
	}

	@Override
	public void notifyFilterSet(Date _from, Date _to) 
	{
		NotificationCache.setUserDateFilters(_from, _to);
		updateAllFrags();
	}
	
	private void updateAllFrags()
	{
		for(SesameNotificationFragment snf:mFrags)
		{
			snf.performSingleUpdate();
		}
	}
	
}