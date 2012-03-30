package at.sesame.fhooe.tablet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabWidget;
import android.widget.TextView;
import at.sesame.fhooe.lib2.R;

public class TabbedComparisonFragment 
extends Fragment {
	private static final String TAG = "HD_TabFragment";
	public static final String BUNDLE_NAME_KEY = "name";
	public static final String BUNDLE_NUMBER_KEY = "number";

	private Context mCtx;
//	private FragmentManager mFragMan;

	private TabHost mTabHost;
	
	private RealTimeViewProvider mRealTimeViewProvider;
	private ComparisonViewProvider mComparisonViewProvider;
//	private TabManager mTabManager;

	public TabbedComparisonFragment(Context _ctx) {
		mCtx = _ctx;
//		mFragMan = _fm;
		mRealTimeViewProvider = new RealTimeViewProvider(mCtx);
		mComparisonViewProvider = new ComparisonViewProvider(mCtx);
//		mRealTimeViewProvider.initializeView(mCtx);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.comparison_tab_fragment_layout, null);
		mTabHost = (TabHost) v
				.findViewById(R.id.hd_tab_fragment_layout_tabhost);
		mTabHost.setup();
	
//		mTabManager = new TabManager(mCtx, getFragmentManager(), mTabHost,
//				android.R.id.tabcontent);
		createTabs();
//		styleTabs();
		return v;
	}

	private void createTabs() {
		Bundle args = new Bundle();
		args.putString(BUNDLE_NAME_KEY, "asdf");
		args.putInt(BUNDLE_NUMBER_KEY, 1);
//		mTabHost.setup();
		 Intent intent;  // Reusable Intent for each tab

		    // Create an Intent to launch an Activity for the tab (to be reused)
		    intent = new Intent().setClass(mCtx, RealTimeActivity.class);

		    // Initialize a TabSpec for each tab and add it to the TabHost
		    TabHost.TabSpec spec = mTabHost.newTabSpec("realtime").setIndicator("echtzeit")
		                  .setContent(intent);
		    mTabHost.addTab(spec);

//		TabHost.TabSpec realtimeTab = mTabHost.newTabSpec("asdf1");
//
//		realtimeTab.setContent(new TabContentFactory() {
//			
//			@Override
//			public View createTabContent(String tag) {
//				// TODO Auto-generated method stub
//				if(null==mRealTimeViewProvider)
//				{
//					Log.e(TAG, "fragment was null");
//				}
//				else
//				{
//					Log.e(TAG, "fragment was ok");
//				}
//				if(null==mRealTimeViewProvider.getRealtimeView())
//				{
//					Log.e(TAG, "view was null");
//				}
//				else
//				{
//					Log.e(TAG, "view was ok");
//				}
//				return mRealTimeViewProvider.getRealtimeView();
//			}
//		});
//		realtimeTab.setIndicator("Echtzeit", getResources().getDrawable(R.drawable.blank));
		
//		TabHost.TabSpec comparisonTab = mTabHost.newTabSpec("asdf2");
//		comparisonTab.setContent(new TabContentFactory() {
//			
//			@Override
//			public View createTabContent(String tag) {
//				// TODO Auto-generated method stub
//				return mComparisonViewProvider.getComparisonView();
//			}
//		});
//		comparisonTab.setIndicator("vergleich");
////		mTabHost.addTab(realtimeTab);
//		mTabHost.addTab(comparisonTab);
		// args.putSerializable("", mCtx);
//		mTabManager.addTab(
//				mTabHost.newTabSpec("real").setIndicator("Echtzeit"),
//				RealTimeChartFragment.class, args);
//		mTabManager.addTab(mTabHost.newTabSpec("comp")
//				.setIndicator("Vergleich"), ComparisonFragment.class, args);
		// mTabHost.
		// mTabHost.addTab(mTabHost.newTabSpec("").)
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
