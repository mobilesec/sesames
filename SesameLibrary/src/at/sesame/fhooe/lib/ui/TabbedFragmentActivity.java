package at.sesame.fhooe.lib.ui;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import at.sesame.fhooe.lib.R;

public class TabbedFragmentActivity 
extends Activity 
{
	private ActionBar mAb;

	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.main);
		mAb = getActionBar();
		if(null!=mAb)
		{
			mAb.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}
	}
	public void addTab(String _title, Fragment _frag) throws Exception
	{
		if(null==mAb)
		{
			throw new Exception("actionbar was null");
		}
		Tab tab = mAb.newTab()
				.setText(_title)
				.setTabListener(new FragmentTabListener(_frag));
		mAb.addTab(tab);
	}

}
