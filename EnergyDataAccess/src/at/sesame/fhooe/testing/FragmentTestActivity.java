package at.sesame.fhooe.testing;


import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TabHost;
import at.sesame.fhooe.EnergyDataActivity;
import at.sesame.fhooe.R;
import at.sesame.fhooe.ui.DialChartFragment;
import at.sesame.fhooe.ui.LineChartViewFragment;
import at.sesame.fhooe.ui.energy.EnergyMeterFragment;
import at.sesame.fhooe.ui.energy.WheelFragment;
import at.sesame.fhooe.ui.ezan.EzanFragment;
import at.sesame.fhooe.ui.ezan.EzanPlaceListFragment;

public class FragmentTestActivity
extends Activity
{
	private static final String TAG = "FragmentTestActivity";
	private TabHost mTabHost;
    private TabManager mTabManager;
    private ActionBar mActionBar;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
//        super.onCreate(savedInstanceState);
//
////        setContentView(R.layout.fragment_tabs);
//        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
//        mTabHost.setup();
//
//        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
//
//        mTabManager.addTab(mTabHost.newTabSpec("simple").setIndicator("Simple"),
//                EnergyDataFragment.class, null);
//        mTabManager.addTab(mTabHost.newTabSpec("contacts").setIndicator("Contacts"),
//        		EnergyDataFragment.class, null);
//        mTabManager.addTab(mTabHost.newTabSpec("custom").setIndicator("Custom"),
//        		EnergyDataFragment.class, null);
//        mTabManager.addTab(mTabHost.newTabSpec("throttle").setIndicator("Throttle"),
//        		EnergyDataFragment.class, null);
//
//        if (savedInstanceState != null) {
//            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
//        }
    	  super.onCreate(savedInstanceState);
//    	  setTheme(android.R.style.Theme_Holo_Light);
    	  setContentView(R.layout.main);
    	    // Notice that setContentView() is not used, because we use the root
    	    // android.R.id.content as the container for each fragment

    	    // setup action bar for tabs
    	    mActionBar = getActionBar();
    	    if(null==mActionBar)
    	    {
    	    	Log.e(TAG, "actionBar was null");
    	    }
    	    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	    mActionBar.setDisplayShowTitleEnabled(true);

    	  addTab("line1",new LineChartViewFragment("line1", new double[]{1,2,3,4,5,6,7,8}));
    	  addTab("line2",new LineChartViewFragment("line2", new double[]{8,7,6,5,4,3,2,1}));
    	  ArrayList<Integer> separators = new ArrayList<Integer>();
    	  separators.add(new Integer(0));
    	  separators.add(new Integer(1));
//    	  separators.add(new Integer(2));
    	  addTab("wheel", new WheelFragment(this,mHandler, 4, separators));
    	  addTab("dial", new DialChartFragment(this, "dial example"));
    	  addTab("energy meter", new EnergyMeterFragment(this));
    	  addTab("load profile", new EnergyDataActivity());
    	  addTab("EZAN", new EzanFragment());
//    	  ArrayList<Fragment> fragList = new ArrayList<Fragment>();
//    	  fragList.add(new LineChartViewFragment("1", new double[]{1,2,3,4,5,6,7,8}));
//    	  fragList.add(new LineChartViewFragment("2", new double[]{1,4,4,4,4,4,4,8}));
//    	  fragList.add(new LineChartViewFragment("3", new double[]{8,7,6,5,4,3,2,1}));
//    	  addTab("third", new MultipleFragmentFragment(fragList, this));

    }
    
//    public void toast(String msg)
//    {
//    	runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//			}
//		})
//    }
    
    private void addTab(String _title, Fragment _frag)
    {
    	Tab tab = mActionBar.newTab()
	            .setText(_title)
	            .setTabListener(new TabListener(_frag));
    	mActionBar.addTab(tab);
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("tab", mTabHost.getCurrentTabTag());
//    }

}
