package at.sesame.fhooe.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import at.sesame.fhooe.R;
import at.sesame.fhooe.R.id;
import at.sesame.fhooe.R.layout;
import at.sesame.fhooe.model.DataRow;
import at.sesame.fhooe.service.EnergyDataAccess;

public class EnergyDataFragment 
extends Activity
implements OnItemClickListener
{
	private static final String TAG = "EnergyDataActivity";
	public static final String MP_ID_KEY = "at.sesame.fhooe.mp.id";
	private ArrayList<DataRow> mData;
	private EnergyDataListAdapter mAdapter;
	private DatePicker mPicker;
	private ListView mList;
	private int mID;
	@Override
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.energy_data_activity_layout);
		
		mID = getIntent().getExtras().getInt(MP_ID_KEY);
		Log.e(TAG, "passed id="+mID);
		mPicker = (DatePicker)findViewById(R.id.datePicker1);
		
		
		mList = (ListView)findViewById(R.id.energyDataList);
		mList.setOnItemClickListener(this);
		
		mData = EnergyDataAccess.getLoadProfile(mID, 
//												DataRow.getUrlTimeString(new GregorianCalendar(	mPicker.getYear(), 
//																								mPicker.getMonth(), 
//																								mPicker.getDayOfMonth())), 
												DataRow.getUrlTimeString(2011, 11, 25),
												DataRow.getUrlTimeString());
		mAdapter = new EnergyDataListAdapter(this, mData);
		if(null==mList)Log.e(TAG, "list was null");
		else Log.e(TAG, "list was not null");
		
		if(null==mAdapter)Log.e(TAG, "adapter was null");
		else Log.e(TAG, "adapter was not null");
		
		mList.setAdapter(mAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.e(TAG, mData.get(arg2).toString());
		
	}
}
