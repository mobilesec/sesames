package at.sesame.fhooe.phone;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Intent;
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
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;
import at.sesame.fhooe.lib2.data.SesameDataCache.DataSource;
import at.sesame.fhooe.lib2.pms.PMSProvider;
import at.sesame.fhooe.lib2.ui.ILoginListener;
import at.sesame.fhooe.lib2.ui.LoginDialogFragment;
import at.sesame.fhooe.lib2.ui.MeterWheelFragment;
import at.sesame.fhooe.lib2.ui.PMSClientActivity;
import at.sesame.fhooe.lib2.ui.SesameFragmentPagerAdapter;
import at.sesame.fhooe.lib2.ui.charts.DefaultDatasetProvider;
import at.sesame.fhooe.lib2.ui.charts.IRendererProvider;
import at.sesame.fhooe.lib2.util.DateHelper;
import at.sesame.fhooe.lib2.ui.charts.exceptions.DatasetCreationException;
import at.sesame.fhooe.lib2.ui.charts.exceptions.RendererInitializationException;


public class SesamePhoneAppActivity 
extends FragmentActivity
implements ISesameDataListener, ILoginListener
{
	private static final String TAG = "SesameHandyAppActivity";
	private ArrayList<Fragment> mFrags = new ArrayList<Fragment>();
	private SesameFragmentPagerAdapter mAdapter;
	private ViewPager mPager;
	private Handler mUiHandler = new Handler();
	
	private SesameDataCache mDataCache;
	
	private MeterWheelFragment mEnergyMeterRoom1Frag;
	private MeterWheelFragment mEnergyMeterRoom3Frag;
	private MeterWheelFragment mEnergyMeterRoom6Frag;
	
	private MD_chartFragment mEdv1Chart;
	private MD_chartFragment mEdv3Chart;
	private MD_chartFragment mEdv6Chart;
	
	private DefaultDatasetProvider mDatasetProvider = new DefaultDatasetProvider();
	private IRendererProvider mRendererProvider;
	
	private LoginDialogFragment mLoginDialog;
	
	private static final int WHEEL_TEXT_SIZE = 40;
	
	private String mUser;
	private String mPass;
	
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
        mRendererProvider = new MD_chart_RendererProvider(getApplicationContext(), true);
        new LoginDialogFragment().show(getSupportFragmentManager(), this);
        mDataCache = SesameDataCache.getInstance(DataSource.semantic_repo);
        
        mEnergyMeterRoom1Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, getString(R.string.global_Room1_name), 50.0f, 0.0f, WHEEL_TEXT_SIZE, 6, 200, true);
		mEnergyMeterRoom3Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, getString(R.string.global_Room3_name), 50.0f, 0.0f, WHEEL_TEXT_SIZE, 6, 200, true);
		mEnergyMeterRoom6Frag = new MeterWheelFragment(getApplicationContext(), mUiHandler, getString(R.string.global_Room6_name), 50.0f, 0.0f, WHEEL_TEXT_SIZE, 6, 200, true);
        
		mEdv1Chart = new MD_chartFragment(getString(R.string.global_Room1_name));
		mEdv3Chart = new MD_chartFragment(getString(R.string.global_Room3_name));
		mEdv6Chart = new MD_chartFragment(getString(R.string.global_Room6_name));
		
		
		mFrags.add(mEnergyMeterRoom1Frag);
		mFrags.add(mEdv1Chart);
        mFrags.add(mEnergyMeterRoom3Frag);
        mFrags.add(mEdv3Chart);
        mFrags.add(mEnergyMeterRoom6Frag);
        mFrags.add(mEdv6Chart);
        
//        mFrags.add(new MD_chartFragment("test"));
		mAdapter = new SesameFragmentPagerAdapter(getSupportFragmentManager(), mFrags);
        mPager = (ViewPager)findViewById(R.id.sesamePager);
        mPager.setAdapter(mAdapter);
        ArrayList<SesameMeasurementPlace> energyPlaces = mDataCache.getEnergyMeasurementPlaces();
//       mDataCache.registerEnergyDataListener(this, energyPlaces.get(0));
//       mDataCache.registerEnergyDataListener(this, energyPlaces.get(1));
//       mDataCache.registerEnergyDataListener(this, energyPlaces.get(2));
        for(SesameMeasurementPlace smp:energyPlaces)
        {
        	mDataCache.registerEnergyDataListener(this, smp);
        }
       mDataCache.startEnergyDataUpdates();
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
		SesameDataContainer data = _data.get(0)	;
		SesameMeasurementPlace smp = data.getMeasurementPlace();
		double lastReading = mDataCache.getLastEnergyReading(smp);
		double overallReading = mDataCache.getOverallEnergyConsumtion(smp);
		if(smp.equals(mDataCache.getEnergyMeasurementPlaces().get(0)))
		{
			updateChartFragment(mEdv1Chart, data);
			updateMeterWheelFragment(mEnergyMeterRoom1Frag, lastReading, overallReading);
		}
		else if(smp.equals(mDataCache.getEnergyMeasurementPlaces().get(1)))
		{
			updateChartFragment(mEdv3Chart, data);
			updateMeterWheelFragment(mEnergyMeterRoom3Frag, lastReading, overallReading);
		}
		else if(smp.equals(mDataCache.getEnergyMeasurementPlaces().get(2)))
		{
			updateChartFragment(mEdv6Chart, data);
			updateMeterWheelFragment(mEnergyMeterRoom6Frag, lastReading, overallReading);
		}
	}
	
	private void updateMeterWheelFragment(MeterWheelFragment _frag, double _lastReading, double _overallReading)
	{
		_frag.setWheelValue(_overallReading);
		_frag.setMeterValue(_lastReading);
	}
	
	private void updateChartFragment(MD_chartFragment _frag, SesameDataContainer _data)
	{
		if(null==_data)
		{
			return;
		}

		String[] titles = new String[]{" aktuell", "vor 1 Woche"};
		ArrayList<String> titleList = new ArrayList<String>();
		
		for(String s:titles)
		{
			titleList.add(s);
		}

		ArrayList<Date[]>dates = new ArrayList<Date[]>(2);
		Date today = DateHelper.getFirstDateToday();
		Date lastWeek = DateHelper.getFirstDateXDaysAgo(7);
		Date sixDaysAgo = DateHelper.getFirstDateXDaysAgo(6);
		Date fiveDaysAgo = DateHelper.getFirstDateXDaysAgo(5);
		
//		Log.e(TAG, "################today Start = "+today.toGMTString());
		double[] todayData = _data.getValuesBetweenDates(sixDaysAgo, fiveDaysAgo);
		
//		Log.e(TAG, "today:"+Arrays.toString(todayData));
		double[] lastWeekData = _data.getValuesBetweenDates(lastWeek, sixDaysAgo);
		
		double[] lastWeekDataCropped = new double[todayData.length];
		for(int i =0;i<todayData.length;i++)
		{
			lastWeekDataCropped[i]=lastWeekData[i];
		}
		
		Log.e(TAG, "todayLength:"+todayData.length+", week data length:"+lastWeekDataCropped.length);
		
//		double[] lastWeekData = new double[todayData.length];
//		for(int i = 0;i<todayData.length;i++)
//		{
//			lastWeekData[i]=i;
//		}
//		Log.e(TAG, "lastWeek:"+Arrays.toString(lastWeekData));
		
		
		Date[] datesArr = _data.getDatesBetweenDates(today, new Date());
//		dates.add((Date[]) _data.getTimeStamps().toArray(new Date[_data.getTimeStamps().size()]));
		

		ArrayList<double[]>values = new ArrayList<double[]>(2);
//		double[] temp = new double[_data.getValues().size()];
//		for(int i = 0;i<_data.getValues().size();i++)
//		{
//			temp[i]=_data.getValues().get(i);
//		}
//		values.add(temp);
		
		
		values.add(lastWeekDataCropped);
		values.add(todayData);
		
		dates.add(datesArr);
		dates.add(datesArr);

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
}