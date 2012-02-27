package at.sesame.fhooe.esmart.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import at.sesame.fhooe.esmart.model.EsmartDataRow;
import at.sesame.fhooe.esmart.service.EsmartDataAccess;
import at.sesame.fhooe.midsd.data.provider.EsmartDateHelper;

public class EnergyDataFragment 
extends Activity
implements OnItemClickListener
{
	private static final String TAG = "EnergyDataActivity";
	public static final String MP_ID_KEY = "at.sesame.fhooe.mp.id";
	private ArrayList<EsmartDataRow> mData;
	private EnergyDataListAdapter mAdapter;
	private DatePicker mPicker;
	private ListView mList;
	private int mID;
	@Override
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(at.sesame.fhooe.midsd.R.layout.energy_data_activity_layout);
		
		mID = getIntent().getExtras().getInt(MP_ID_KEY);
		Log.e(TAG, "passed id="+mID);
		mPicker = (DatePicker)findViewById(at.sesame.fhooe.midsd.R.id.datePicker1);
		
		
		mList = (ListView)findViewById(at.sesame.fhooe.midsd.R.id.energyDataList);
		mList.setOnItemClickListener(this);
		
		mData = EsmartDataAccess.getLoadProfile(mID, 
//												DataRow.getUrlTimeString(new GregorianCalendar(	mPicker.getYear(), 
//																								mPicker.getMonth(), 
//																								mPicker.getDayOfMonth())), 
												EsmartDateHelper.getTodayUrlTimeString(),
												EsmartDateHelper.getTodayUrlTimeString());
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
