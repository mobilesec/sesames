package at.sesame.fhooe.midsd.md;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> mFrags;
	
	public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> _frags) {
		super(fm);
		mFrags = _frags;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFrags.get(arg0);
	}

	@Override
	public int getCount() 
	{
		return mFrags.size();
	}
}
