package at.sesame.fhooe.charts;

import java.util.Random;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;
import at.sesame.fhooe.R;
import at.sesame.fhooe.charts.rating.RatingFragment;
import at.sesame.fhooe.charts.rating.RatingItemFragment;
import at.sesame.fhooe.charts.rating.RatingItemFragment.Ranking;

public class ChartNavigationFragment 
extends Fragment 
implements Runnable
{
	private static final int SHOW_DAY = 0;
	private static final int SHOW_MONTH = 1;
	private static final int SHOW_YEAR = 2;
	
	private Button mDayButton;
	private Button mMonthButton;
	private Button mYearButton;
	private ToggleButton mToggle;
	private boolean mWindowKnown = false;
	private double mWinSize = 0;
//	private Button mRankingButton;
	
	private LineChartViewFragment mLcvfDay;
	private LineChartViewFragment mLcvfMonth;
	private LineChartViewFragment mLcvfYear;
	
	private BarChartViewFragment mBchvf;
	
	private LineChartViewFragment mCurrentFrag;
	private RatingFragment mRatFrag;
	
	private boolean mRunning = false;
	
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setHasOptionsMenu(true);
		mLcvfDay = new LineChartViewFragment("DAY");
		mLcvfMonth = new LineChartViewFragment("MONTH");
		mLcvfYear = new LineChartViewFragment("YEAR");
		
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		mRunning = false;
	}
	
	
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstance)
	{
		super.onCreateView(_inflater, _container, _savedInstance);
		View v = _inflater.inflate(R.layout.chartnavigation, _container, false);
		if(null==v)
		{
			Log.e("ChartNavigationFragment", "view was null");
		}
		Log.e("ChartNavigationFragment", "onCreateView called");
		
		return v;
	}
	
	public void onActivityCreated(Bundle _savedInstance)
	{
		super.onActivityCreated(_savedInstance);
		
		mDayButton = (Button)getActivity().findViewById(R.id.chartNavigationDayButton);
		if(null==mDayButton)
		{
			Log.e("ChartNavigationFragment", "day button was null");
		}
		mDayButton.setOnClickListener(new View.OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				setData(SHOW_DAY);
			}
		});
		
		mMonthButton = (Button)getActivity().findViewById(R.id.chartNavigationMonthButton);
		mMonthButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				setData(SHOW_MONTH);
			}
		});
		mYearButton = (Button)getActivity().findViewById(R.id.chartNavigationYearButton);
		mYearButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				setData(SHOW_YEAR);
			}
		});
		
		mToggle = (ToggleButton)getActivity().findViewById(R.id.toggleButton1);
		mToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if(!isChecked)
				{
					mRunning = false;
				}
				else
				{
					mRunning = true;
					new Thread(ChartNavigationFragment.this).start();
				}
				
			}
		});
		
//		mRankingButton = (Button)getActivity().findViewById(R.id.setupRating);
//		mRankingButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				setupRating();
//				
//			}
//		});
//		setData(SHOW_DAY);
		
		FragmentManager fragMan = getFragmentManager();
		mBchvf = (BarChartViewFragment) fragMan.findFragmentByTag("barChart");
		mRatFrag = (RatingFragment) fragMan.findFragmentByTag("rating");
		mCurrentFrag = (LineChartViewFragment)fragMan.findFragmentByTag("lineChart");
//
//
//		FragmentTransaction ft = fragMan.beginTransaction();
//	
//			ft.replace(R.id.frameLayout2, mBchvf);
//			ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
//			ft.commit();
	}
	
//	public void onCreateOptionsMenu(Menu _menu, MenuInflater _inflater)
//	{
////		super.onCreateOptionsMenu(_menu);
//		Log.e("ChartView","onCreateOptionsMenu");
//		_inflater.inflate(R.menu.menu, _menu);
////		getMenuInflater().inflate(R.menu.menu, _menu);
////		return true;
//	}
//	public boolean onOptionsItemSelected(MenuItem _item)
//	{
//		Toast.makeText(getActivity(), "Settings", Toast.LENGTH_LONG).show();
//		return true;
//	}
	
	private void setData(int _data)
	{
		Log.e("ChartNavigationFragment", "setData called");
		FragmentManager fragMan = getFragmentManager();
		LineChartViewFragment newCvf = null;
		switch(_data)
		{
		case SHOW_DAY:
			newCvf = mLcvfDay;
			break;
		case SHOW_MONTH:
			newCvf = mLcvfMonth;
			break;
		case SHOW_YEAR:
			newCvf = mLcvfYear;
			break;
		}
		mCurrentFrag = newCvf;
		FragmentTransaction ft = fragMan.beginTransaction();
		try
		{
//			ft.remove(fragMan.fin)
			ft.replace(R.id.lineChartFrame, newCvf,"lineChart");
//			ft.replace(R.id.barChartFrame, mBchvf);
//			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}
		catch(IllegalStateException ise)
		{
			//IllegalStateException is thrown when the new fragment to
			//replace the old fragment is the same as the old fragment
			//therefore do nothing
		}
		
	}
	
	public void onPrepareOptionsMenu(Menu _menu)
	{
		Log.e("ChartNavigationFragment", "onPrepareOptionsMenu");
		_menu.add("test");
	}


	@Override
	public void run() 
	{
		double x = 0;
		Random r = new Random();
		while(mRunning)
		{
			if(null!=mCurrentFrag)
			{
				x+=10;
				mCurrentFrag.updateDataSet(x, r.nextDouble()*50);
				mBchvf.updateData();
				mRatFrag.refreshFragments();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
//	private void setupRating()
//	{
//		RatingItemFragment firstFrag = (RatingItemFragment) getFragmentManager().findFragmentById(R.id.ratingItemFirst);
//		RatingItemFragment secondFrag = (RatingItemFragment) getFragmentManager().findFragmentById(R.id.ratingItemSecond);
//		RatingItemFragment thirdFrag = (RatingItemFragment) getFragmentManager().findFragmentById(R.id.ratingItemThird);
//		
//		firstFrag.setData("Klasse XY", "", "123,00 €", Ranking.FIRST);
//		secondFrag.setData("Klasse XY", "", "140,00 €", Ranking.SECOND);
//		thirdFrag.setData("Klasse XY", "", "150,00 €", Ranking.THIRD);
//	}
}
