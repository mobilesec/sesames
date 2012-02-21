package at.sesame.fhooe.midsd.hd;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import at.sesame.fhooe.lib.ui.tabs.TabManager;
import at.sesame.fhooe.midsd.R;

public class HD_TabFragment 
extends Fragment 
{
	private static final String TAG = "HD_TabFragment";
	public static final String BUNDLE_NAME_KEY = "name";
	public static final String BUNDLE_NUMBER_KEY = "number";
	
	private Context mCtx;
	private FragmentManager mFragMan;
	
	private TabHost mTabHost;
	private TabManager mTabManager;
	
	public HD_TabFragment(Context _ctx, FragmentManager _fm)
	{
		mCtx = _ctx;
		mFragMan = _fm;
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.hd_tab_fragment_layout, null);
		mTabHost = (TabHost)v.findViewById(R.id.hd_tab_fragment_layout_tabhost);
		mTabManager = new TabManager(mCtx, getFragmentManager(), mTabHost, android.R.id.tabcontent);
		createTabs();
		return v;
	}
	
	private void createTabs()
	{
		mTabHost.setup();
		Bundle args = new Bundle();
		args.putString(BUNDLE_NAME_KEY, "asdf");
		args.putInt(BUNDLE_NUMBER_KEY, 1);
		mTabManager.addTab(mTabHost.newTabSpec("real").setIndicator("Echtzeit"), RealTimeChartFragment.class, args);
		mTabManager.addTab(mTabHost.newTabSpec("comp").setIndicator("Vergleich"), ComparisonFragment.class, args);
//		mTabHost.
//		mTabHost.addTab(mTabHost.newTabSpec("").)
	}

}
