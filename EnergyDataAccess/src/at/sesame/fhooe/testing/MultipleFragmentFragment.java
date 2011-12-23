package at.sesame.fhooe.testing;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import at.sesame.fhooe.R;
public class MultipleFragmentFragment 
extends Fragment 
{
	private ArrayList<Fragment> mFrags;
	private ArrayList<FrameLayout> mContainer = new ArrayList<FrameLayout>();
	private LinearLayout mLinLayout;
	
	public MultipleFragmentFragment(ArrayList<Fragment> _frags, Context _c)
	{
		mFrags = _frags;
		for(int i = 0;i<mFrags.size();i++)
		{
			mContainer.add(new FrameLayout(_c));
		}
	}
	
//	public void onCreate(Bundle _savedInstanceState)
//	{
//		super.onCreate(_savedInstanceState);
//		
//	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.triple_fragment_layout, null);
		if(null==mLinLayout)
		{
			mLinLayout = (LinearLayout)v.findViewById(R.id.LinearLayout1);
		}
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		mLinLayout.removeAllViews();
		for(int i  = 0;i<mFrags.size();i++)
		{
			FrameLayout fl = mContainer.get(i);
			mLinLayout.addView(fl);
			ft.add(fl.getId(), mFrags.get(i));
		}
//		ft.add(R.id.frameLayout1, new LineChartViewFragment("1", new double[]{1,2,3,4,5,6,7,8}));
//		ft.add(R.id.frameLayout2, new LineChartViewFragment("2", new double[]{1,4,4,4,4,4,4,8}));
//		ft.add(R.id.frameLayout3, new LineChartViewFragment("3", new double[]{8,7,6,5,4,3,2,1}));
		ft.commit();
		return v;
	}
//	public void onCreate(Bundle _savedInstance)
//	{
//		super.onCreate(_savedInstance);
//		setCon
//	}
	
	
}
