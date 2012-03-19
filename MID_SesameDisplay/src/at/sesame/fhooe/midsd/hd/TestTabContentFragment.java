package at.sesame.fhooe.midsd.hd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestTabContentFragment 
extends Fragment 
{
	private static final String TAG = "TestTabContentFragment";
	@Override
	public void onCreate(Bundle _savedInstanceState)
	{
		super.onCreate(_savedInstanceState);
		Bundle args = getArguments();
		String name = args.getString(HD_TabFragment.BUNDLE_NAME_KEY);
		int num = args.getInt(HD_TabFragment.BUNDLE_NUMBER_KEY);
		
		Log.e(TAG, "name="+name+", num="+num);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	

}
