package at.fhooe.mc.graphical;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import at.fhooe.mc.R;
import at.fhooe.mc.extern.fingerprintInformation.MeasurementPoint;

public class GraphicalMpSelector
extends Activity
implements ISelectionListener
{
	private static final String TAG = "GraphicalMpSelector";
	private ArrayList<Bitmap> mPlans = new ArrayList<Bitmap>();
	private Spinner mPlanSelector;
	private MpSelectionView mMpSelectionView;
	private ArrayList<MeasurementPoint> mMps;
	private MeasurementPoint mSelectedMp = null;
	private TextView mMpNameLabel;
	private TextView mMpNumFpLabel;
	public static final String SELECTED_MP_KEY = "at.fhooe.mc.selectedMP";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if(null!=extras)
		{
			mMps = (ArrayList<MeasurementPoint>)extras.getSerializable("asdf");
			Log.e(TAG, "!!!!!!!!!!!!!mps loaded:"+mMps.size());
			for(MeasurementPoint mp:mMps)
			{
				Log.e(TAG, mp.getName()+": "+mp.getFingerPrints().size());
			}
		}
		else
		{
			Log.e(TAG, "%%%%%%%%%EXTRAS WERE NULL");
		}
		setContentView(R.layout.mp_selection_layout);
		
		mPlanSelector  =(Spinner) findViewById(R.id.mp_selection_floor_spinner);
		
		mMpSelectionView = (MpSelectionView)findViewById(R.id.mpSelectionView1);
		mMpSelectionView.setSelectionListener(this);
		
		mMpNameLabel = (TextView)findViewById(R.id.mp_info_name);
		mMpNumFpLabel = (TextView)findViewById(R.id.mp_info_num_fp);
		
		File images = Environment.getExternalStorageDirectory();
		File[] imagelist = images.listFiles(new FilenameFilter(){
		@Override
		public boolean accept(File dir, String name)
		{
			return ((name.endsWith(".jpg"))||(name.endsWith(".png")));
		}
		});
//		mFiles = new String[imagelist.length];
//		ArrayList<URL> urls = new ArrayList<URL>();
		ArrayList<String> names = new ArrayList<String>();
		Log.e(TAG, "number of pics found:"+imagelist.length);
		for(File f:imagelist)
		{
			Log.e(TAG, "FILE:::"+f.getAbsolutePath().toString());
			mPlans.add(BitmapFactory.decodeFile(f.toString()));
			names.add(f.toString());

//			try {
//				Uri uri = Uri.parse(imagelist[i].getAbsolutePath());
//				urls.add(new U);
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
//
//		
//		for(URL url:urls)
//		{
//			Log.e(TAG, url.toString());
//			InputStream is;
//			try
//			{
//				is = (InputStream) url.getContent();
//				mPlans.add(Drawable.createFromStream(is, "src name"));
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//		}
		
		ArrayAdapter<String> planAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, (String[]) names.toArray(new String[names.size()]));
		mPlanSelector.setAdapter(planAdapter);
		
//		mPlanSelector.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				Log.e(TAG, "LISTENER: position:"+arg2);
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		Log.e(TAG, ""+planAdapter.getCount());
//		Log.e(TAG, planAdapter.getItem(0));
		if(!mPlans.isEmpty())
		{
			mMpSelectionView.setPlan(mPlans.get(0), mMps);			
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.mp_selection_menu_reset_pos:
			mMpSelectionView.resetOffsets();
			break;
		case R.id.mp_selection_menu_select_mp:
			Intent i = new Intent();
			i.putExtra(SELECTED_MP_KEY, mSelectedMp.getName());
			setResult(RESULT_OK, i);
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mp_selection_menu, menu);
	    return true;
	}

	@Override
	public void notifyMpSelected(MeasurementPoint _mp)
	{
		mSelectedMp = _mp;
		mMpNameLabel.setText(mSelectedMp.getName());
		mMpNumFpLabel.setText(""+mSelectedMp.getFingerPrints().size());
	}
}
