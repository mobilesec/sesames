package at.sesame.fhooe.viewexperiments;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;


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

//	@Override
//	public void destroyItem(ViewGroup container, int position, Object object) {
//		// TODO Auto-generated method stub
//		((ViewPager)container).removeView(((Fragment)object).getView());
//	}

//	@Override
//	public boolean isViewFromObject(View arg0, Object arg1) {
//		// TODO Auto-generated method stub
//		return ((Fragment)arg1).getView().equals(arg0);
//	}
	
//	
//    /**
//     * Create the page for the given position.  The adapter is responsible
//     * for adding the view to the container given here, although it only
//     * must ensure this is done by the time it returns from
//     * {@link #finishUpdate()}.
//     *
//     * @param container The containing View in which the page will be shown.
//     * @param position The page position to be instantiated.
//     * @return Returns an Object representing the new page.  This does not
//     * need to be a View, but can be some other container of the page.
//     */
//        @Override
//        public Object instantiateItem(View collection, int position) {
//                return mFrags.get(position);
//        }

//    /**
//     * Remove a page for the given position.  The adapter is responsible
//     * for removing the view from its container, although it only must ensure
//     * this is done by the time it returns from {@link #finishUpdate()}.
//     *
//     * @param container The containing View from which the page will be removed.
//     * @param position The page position to be removed.
//     * @param object The same object that was returned by
//     * {@link #instantiateItem(View, int)}.
//     */
//        @Override
//        public void destroyItem(View collection, int position, Object view) {
//                ((ViewPager) collection).removeView(((Fragment) view).getView());
//        }
//
//        
//        
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//                return object instanceof Fragment;
//        }
//
//        
//    /**
//     * Called when the a change in the shown pages has been completed.  At this
//     * point you must ensure that all of the pages have actually been added or
//     * removed from the container as appropriate.
//     * @param container The containing View which is displaying this adapter's
//     * page views.
//     */
//        @Override
//        public void finishUpdate(View arg0) {}
//        
//
//        @Override
//        public void restoreState(Parcelable arg0, ClassLoader arg1) {}
//
//        @Override
//        public Parcelable saveState() {
//                return null;
//        }
//
//        @Override
//        public void startUpdate(View arg0) {}



}
