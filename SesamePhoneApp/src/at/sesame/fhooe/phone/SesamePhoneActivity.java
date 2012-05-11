package at.sesame.fhooe.phone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import at.sesame.fhooe.lib2.pms.ComputerRoomInformation;

public class SesamePhoneActivity 
extends FragmentActivity 
{
	public static final String COMPUTER_ROOM_INFO_KEY = "at.sesame.fhooe.mp";
	
	private ComputerRoomInformation mComputerRoomInfo;
	
	private Intent mPmsIntent;
	private Intent mTodayIntent;
	private Intent mComparisonIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Bundle data = getIntent().getExtras();
		if(null!=data)
		{
			mComputerRoomInfo = (ComputerRoomInformation) data.getSerializable(COMPUTER_ROOM_INFO_KEY);
		}
		createIntents();
	}
	
	private void createIntents()
	{
		mPmsIntent = new Intent(this, PMSClientActivity.class);
		mPmsIntent.putExtras(getIntent());
		
		mTodayIntent = new Intent(this, TodayChartActivity.class);
		mTodayIntent.putExtras(getIntent());
		
		mComparisonIntent = new Intent(this, ComparisonActivity.class);
		mComparisonIntent.putExtras(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.sesame_phone_menu, menu);
	    return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putSerializable(COMPUTER_ROOM_INFO_KEY, mComputerRoomInfo);
		super.onSaveInstanceState(outState);
	}

	protected ComputerRoomInformation getComputerRoomInformation()
	{
		return mComputerRoomInfo;
	}
	
	protected Intent getPmsIntent()
	{
		return mPmsIntent;
	}
	
	protected Intent getTodayIntent() 
	{
		return mTodayIntent;
	}
	
	protected Intent getComparisonIntent()
	{
		return mComparisonIntent;
	}
}
