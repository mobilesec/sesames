package at.sesame.fhooe.phone;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import at.sesame.fhooe.lib2.data.ISesameDataListener;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.data.SesameDataContainer;
import at.sesame.fhooe.lib2.data.SesameMeasurement;
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;

import at.sesame.fhooe.lib2.pms.PMSProvider;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory.DialogType;
import at.sesame.fhooe.lib2.pms.dialogs.PMSNetworkingInProgressDialogFragment;
import at.sesame.fhooe.lib2.ui.EnergyMeterRenderer;
import at.sesame.fhooe.lib2.ui.ILoginListener;
import at.sesame.fhooe.lib2.ui.LoginDialogFragment;
import at.sesame.fhooe.lib2.ui.MeterWheelFragment;
import at.sesame.fhooe.lib2.ui.SesameFragmentPagerAdapter;
import at.sesame.fhooe.lib2.ui.charts.DefaultDatasetProvider;
import at.sesame.fhooe.lib2.ui.charts.IRendererProvider;
import at.sesame.fhooe.lib2.ui.charts.exceptions.DatasetCreationException;
import at.sesame.fhooe.lib2.ui.charts.exceptions.RendererInitializationException;
import at.sesame.fhooe.lib2.util.DateHelper;


public class SesamePhoneAppActivity 
extends FragmentActivity
implements ISesameDataListener, ILoginListener
{
	private static final String TAG = "SesamePhoneAppActivity";
	private static final long UI_UPDATE_PERIOD = 5000;
	private Timer mUiUpdateTimer;
	private ArrayList<Fragment> mFrags = new ArrayList<Fragment>();
	private SesameFragmentPagerAdapter mAdapter;
	private ViewPager mPager;
	private Handler mUiHandler = new Handler();
	
	private SesameDataCache mDataCache;
	
	private MeterWheelFragment mEnergyMeterRoom1Frag;
	private MeterWheelFragment mEnergyMeterRoom3Frag;
	private MeterWheelFragment mEnergyMeterRoom6Frag;
	
	private PhoneChartFragment mEdv1Chart;
	private PhoneChartFragment mEdv3Chart;
	private PhoneChartFragment mEdv6Chart;
	
	private DefaultDatasetProvider mDatasetProvider = new DefaultDatasetProvider();
	private IRendererProvider mRendererProvider;
	
	private LoginDialogFragment mLoginDialog;
	
	private static final int WHEEL_TEXT_SIZE = 40;
	
	private String mUser = "peter";
	private String mPass = "thatpeter";
	private SesameMeasurementPlace mEdv1Place;
	private SesameMeasurementPlace mEdv3Place;
	private SesameMeasurementPlace mEdv6Place;
	static
	{
		Log.e(TAG, "static log");
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.e(TAG, "onCreate");
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		}).start();

    	   
		new CreationTask().execute(new Void[]{});

//        PMSDialogFactory.showDialog(DialogType.NETWORKING_IN_PROGRESS, getSupportFragmentManager(), null, new Object[]{getApplicationContext()});
        
    }
    
    private class CreationTask extends AsyncTask<Void, Void, Void>
    {
    	
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			PMSDialogFactory.showDialog(DialogType.NETWORKING_IN_PROGRESS, getSupportFragmentManager(), null, new Object[]{SesamePhoneAppActivity.this});
//			new PMSNetworkingInProgressDialogFragment(SesamePhoneAppActivity.this).show(getSupportFragmentManager(), null);
		}

		@Override
		protected Void doInBackground(Void... params) {
			mRendererProvider = new PhoneChartRendererProvider(getApplicationContext(), false);
//	        new LoginDialogFragment().show(getSupportFragmentManager(), this);
	        mDataCache = SesameDataCache.getInstance(SesamePhoneAppActivity.this);
	        ArrayList<SesameMeasurementPlace> places = mDataCache.getEnergyMeasurementPlaces();
			mEdv1Place = places.get(0);
			mEdv3Place = places.get(1);
			mEdv6Place = places.get(2);
	        mEnergyMeterRoom1Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, getString(R.string.global_Room1_name), 50.0f, 0.0f, WHEEL_TEXT_SIZE, 6, false, new EnergyMeterRenderer());
			mEnergyMeterRoom3Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, getString(R.string.global_Room3_name), 50.0f, 0.0f, WHEEL_TEXT_SIZE, 6, false, new EnergyMeterRenderer());
			mEnergyMeterRoom6Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, getString(R.string.global_Room6_name), 50.0f, 0.0f, WHEEL_TEXT_SIZE, 6, false, new EnergyMeterRenderer());
	        
			mEdv1Chart = new PhoneChartFragment(getString(R.string.global_Room1_name), getApplicationContext(), mUiHandler);
			mEdv3Chart = new PhoneChartFragment(getString(R.string.global_Room3_name), getApplicationContext(), mUiHandler);
			mEdv6Chart = new PhoneChartFragment(getString(R.string.global_Room6_name), getApplicationContext(), mUiHandler);
			
			
			mFrags.add(mEnergyMeterRoom1Frag);
			mFrags.add(mEdv1Chart);
	        mFrags.add(mEnergyMeterRoom3Frag);
	        mFrags.add(mEdv3Chart);
	        mFrags.add(mEnergyMeterRoom6Frag);
	        mFrags.add(mEdv6Chart);
	        
//	        mFrags.add(new MD_chartFragment("test"));
			mAdapter = new SesameFragmentPagerAdapter(getSupportFragmentManager(), mFrags);
	        mPager = (ViewPager)findViewById(R.id.sesamePager);
	        
//	       mDataCache.registerEnergyDataListener(this, energyPlaces.get(0));
//	       mDataCache.registerEnergyDataListener(this, energyPlaces.get(1));
//	       mDataCache.registerEnergyDataListener(this, energyPlaces.get(2));
//	        for(SesameMeasurementPlace smp:energyPlaces)
//	        {
//	        	Log.d(TAG, "+++++++++smp="+smp.getName());
//	        	mDataCache.registerEnergyDataListener(this, smp);
//	        }
	       mDataCache.startEnergyDataUpdates();
	       
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			mPager.setAdapter(mAdapter);
			PMSDialogFactory.dismissCurrentDialog();
		}
		
		
    	
    }
    
//    private void showLoginDialog()
//    {
//    	mLoginDialog = new LoginDialogFragment();
//    	mLoginDialog.setLoginListener(this);
//    	mLoginDialog.show(getSupportFragmentManager(), null);
//    }
    
//    @Override
//	protected void onPostCreate(Bundle savedInstanceState) {
////    	for(int i = 0;i<3;i++)
////        {
////        	mFrags.add(new DummyFragment("dummy "+i));
////        }
////     
//		super.onPostCreate(savedInstanceState);
//	}



	



	@Override
	public void notifyAboutData(ArrayList<SesameDataContainer> _data) {
		Log.e(TAG, "notified about data");
		
	}
	
	private void updateMeterWheelFragment(MeterWheelFragment _frag, double _lastReading, double _overallReading)
	{
		_frag.setMeterValue(_lastReading);
		_frag.setWheelValue(_overallReading);
	}
	
	private void updateChartFragment(PhoneChartFragment _frag, SesameMeasurementPlace _smp)
	{
//		if(null==_data)
//		{
//			return;
//		}
		
//		Log.d(TAG, "passed data ("+_data.getMeasurements().size()+"):");
//		for(SesameMeasurement sm:_data.getMeasurements())
//		{
//			Log.d(TAG, sm.toString());
//		}
//		for(int i = 0;i<_data.getTimeStamps().size();i++)
//		{
//			Date d = _data.getTimeStamps().get(i);
//			double val = _data.getValues().get(i);
//			Log.d(TAG, d.toGMTString()+": "+val);
//		}

//		String[] titles = new String[]{" aktuell", "vor 1 Woche"};
		String[] titles = new String[]{" aktuell"};
		ArrayList<String> titleList = new ArrayList<String>();
		
		for(String s:titles)
		{
			titleList.add(s);
		}

		ArrayList<Date[]>dates = new ArrayList<Date[]>(2);
		Date today = DateHelper.getFirstDateToday();
		Date yesterday = DateHelper.getFirstDateXDaysAgo(1);
		Date lastWeek = DateHelper.getFirstDateXDaysAgo(7);
		Date sixDaysAgo = DateHelper.getFirstDateXDaysAgo(6);
		Date fiveDaysAgo = DateHelper.getFirstDateXDaysAgo(5);
		
//		Log.e(TAG, "################today Start = "+today.toGMTString());
//		ArrayList<SesameMeasurement> todayMeasurements = _data.filterByDate(DateHelper.getFirstDateXDaysAgo(38), DateHelper.getFirstDateXDaysAgo(37));
		ArrayList<SesameMeasurement> todayMeasurements = mDataCache.getEnergyReadings(_smp, DateHelper.getFirstDateXDaysAgo(2), new Date(), false).getMeasurements();
		double[] todayData = SesameDataContainer.getValueArray(todayMeasurements);
//		double[] todayData = _data.getValuesBetweenDates(lastWeek, sixDaysAgo);
		
//		Log.e(TAG, "today:"+Arrays.toString(todayData));
//		ArrayList<SesameMeasurement> lastWeekMeasurements = _data.filterByDate(DateHelper.getFirstDateXDaysAgo(45), DateHelper.getFirstDateXDaysAgo(44));
//		ArrayList<SesameMeasurement> lastWeekMeasurements = mDataCache.getEnergyReadings(_smp, DateHelper.getFirstDateXDaysAgo(45), DateHelper.getFirstDateXDaysAgo(44)).getMeasurements();
//		double[]lastWeekData = SesameDataContainer.getValueArray(lastWeekMeasurements);
		//		double[] lastWeekData = _data.getValuesBetweenDates(lastWeek, sixDaysAgo);
//		Log.e(TAG, "todayLength:"+todayData.length+", week data length:"+lastWeekData.length);
		
//		double[] lastWeekDataCropped = new double[todayData.length];
//		for(int i =0;i<todayData.length;i++)
//		{
//			lastWeekDataCropped[i]=lastWeekData[i];
//		}
		
		
//		double[] lastWeekData = new double[todayData.length];
//		for(int i = 0;i<todayData.length;i++)
//		{
//			lastWeekData[i]=i;
//		}
//		Log.e(TAG, "lastWeek:"+Arrays.toString(lastWeekData));
		
		Date[] datesArr = SesameDataContainer.getTimeStampArray(todayMeasurements);
//		Date[] datesArr = _data.getDatesBetweenDates(today, new Date());
//		dates.add((Date[]) _data.getTimeStamps().toArray(new Date[_data.getTimeStamps().size()]));
		

		ArrayList<double[]>values = new ArrayList<double[]>(2);
//		double[] temp = new double[_data.getValues().size()];
//		for(int i = 0;i<_data.getValues().size();i++)
//		{
//			temp[i]=_data.getValues().get(i);
//		}
//		values.add(temp);
		
		
//		values.add(lastWeekDataCropped);
		values.add(todayData);
		
		dates.add(datesArr);
//		dates.add(datesArr);

		try 
		{
			XYMultipleSeriesDataset dataset = mDatasetProvider.buildDateDataset(titles, dates, values);
//			mDatasetProvider.createDataset(titleList, values);
//			XYMultipleSeriesDataset dataset = mDatasetProvider.getDataset();
			
			mRendererProvider.createMultipleSeriesRenderer(dataset);

			XYMultipleSeriesRenderer renderer = mRendererProvider.getRenderer();
			_frag.setChart(dataset, renderer);

		} 
		catch (DatasetCreationException e) 
		{
			e.printStackTrace();
		} 
		catch (RendererInitializationException e) 
		{
			e.printStackTrace();
		}
	}



	@Override
	protected void onPause() {
		stopUiUpdates();
		super.onPause();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		startUiUpdates();
	}

	@Override
	protected void onDestroy() {
		mDataCache.cleanUp();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.energy_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent i = new Intent(this, PMSClientActivity.class);
//		Bundle extras = new Bundle();
		i.putExtra(PMSClientActivity.BUNDLE_USER_KEY, mUser);
		i.putExtra(PMSClientActivity.BUNDLE_PASS_KEY, mPass);
		
		startActivity(i);
		return true;
	}

	@Override
	public boolean checkLogin(String _user, String _pass) {
		boolean res = PMSProvider.checkCredentials(_user, _pass);
		if(res)
		{
			mUser = _user;
			mPass = _pass;
		}
		return res;
	}
	
	private void startUiUpdates()
	{
		stopUiUpdates();
		mUiUpdateTimer = new Timer();
		mUiUpdateTimer.scheduleAtFixedRate(new UiUpdateTask(), 0, UI_UPDATE_PERIOD);
	}
	
	private void stopUiUpdates()
	{
		if(null!=mUiUpdateTimer)
		{
			mUiUpdateTimer.cancel();
			mUiUpdateTimer.purge();
		}
	}
	
	private class UiUpdateTask extends TimerTask
	{
		@Override
		public void run() {
//			SesameDataContainer data = _data.get(0)	;
//			SesameMeasurementPlace smp = data.getMeasurementPlace();
//			double lastReading;
//			try {
//				lastReading = mDataCache.getLastEnergyReading(smp).getVal();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return;
//			}
//			double overallReading = mDataCache.getOverallEnergyConsumtion(smp);
//			if(smp.equals(mDataCache.getEnergyMeasurementPlaces().get(0)))
//			{
//				Log.d(TAG, "updating edv1 from "+smp.getName());
//				updateChartFragment(mEdv1Chart, data);
//				updateMeterWheelFragment(mEnergyMeterRoom1Frag, lastReading, overallReading);
//			}
//			else if(smp.equals(mDataCache.getEnergyMeasurementPlaces().get(3)))
//			{
//				Log.d(TAG, "updating edv3 from "+smp.getName());
//				updateChartFragment(mEdv3Chart, data);
//				updateMeterWheelFragment(mEnergyMeterRoom3Frag, lastReading, overallReading);
//			}
//			else if(smp.equals(mDataCache.getEnergyMeasurementPlaces().get(2)))
//			{
//				Log.d(TAG, "updating edv6 from "+smp.getName());
//				updateChartFragment(mEdv6Chart, data);
//				updateMeterWheelFragment(mEnergyMeterRoom6Frag, lastReading, overallReading);
//			}
//			Log.d(TAG, "updating edv1 from "+smp.getName());
			try {
				updateChartFragment(mEdv1Chart, mEdv1Place);
				updateMeterWheelFragment(mEnergyMeterRoom1Frag, mDataCache.getLastEnergyReading(mEdv1Place).getVal(), mDataCache.getOverallEnergyConsumtion(mEdv1Place));
				
				updateChartFragment(mEdv3Chart, mEdv3Place);
				updateMeterWheelFragment(mEnergyMeterRoom3Frag, mDataCache.getLastEnergyReading(mEdv3Place).getVal(), mDataCache.getOverallEnergyConsumtion(mEdv3Place));
				
				updateChartFragment(mEdv6Chart,mEdv6Place);
				updateMeterWheelFragment(mEnergyMeterRoom6Frag, mDataCache.getLastEnergyReading(mEdv6Place).getVal(), mDataCache.getOverallEnergyConsumtion(mEdv6Place));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}	
	}
}