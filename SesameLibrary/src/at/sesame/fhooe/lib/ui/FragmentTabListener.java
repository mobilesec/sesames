package at.sesame.fhooe.lib.ui;

import android.app.ActionBar.Tab;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.widget.TabHost;

public class FragmentTabListener 
implements ActionBar.TabListener 
{
    private Fragment mFragment;

    /** Constructor used each time a new tab is created.
      * @param activity  The host Activity, used to instantiate the fragment
      * @param tag  The identifier tag for the fragment
      * @param clz  The fragment's Class, used to instantiate the fragment
      */
    public FragmentTabListener(Fragment _frag) 
    {
    	mFragment = _frag;
    }

    /* The following are each of the ActionBar.TabListener callbacks */

    public void onTabSelected(Tab tab, FragmentTransaction ft) 
    {
    	ft.replace(android.R.id.content, mFragment);
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) 
    {
        if (mFragment != null) 
        {
        	ft.remove(mFragment);
        }
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) 
    {
        // User selected the already selected tab. Usually do nothing.
    }
    
//    public static Tab createTab(TabHost _tb, String _title, Fragment _frag) throws Exception
//	{
//	
//		Tab tab = _tb.newTab()
//				.setText(_title)
//				.setTabListener(new FragmentTabListener(_frag));
//		mAb.addTab(tab);
//	}

}