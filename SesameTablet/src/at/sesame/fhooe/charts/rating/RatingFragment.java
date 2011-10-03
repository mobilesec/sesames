package at.sesame.fhooe.charts.rating;

import java.util.Random;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import at.sesame.fhooe.R;
import at.sesame.fhooe.charts.rating.RatingItemFragment.Ranking;

public class RatingFragment 
extends Fragment 
{
	private FrameLayout mFirstFrame;
	private FrameLayout mSecondFrame;
	private FrameLayout mThirdFrame;
	
	private RatingItemFragment mFirstItem;
	private RatingItemFragment mSecondItem;
	private RatingItemFragment mThirdItem;
	
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstance)
	{
//		FrameLayout fl = (FrameLayout)getActivity().findViewById(R.id.ratingFrame);
//		fl.removeAllViews();
		View v = _inflater.inflate(R.layout.rating, _container, false);
//		v.setBackgroundColor(Color.GREEN);
//		mFirstFrame = (FrameLayout) v.findViewById(R.id.firstFrame);
//		mSecondFrame = (FrameLayout) v.findViewById(R.id.secondFrame);
//		mThirdFrame = (FrameLayout) v.findViewById(R.id.thirdFrame);
		
		return v;
	}
	
	public void onActivityCreated(Bundle _savedInstance)
	{
		super.onActivityCreated(_savedInstance);
		mFirstItem = (RatingItemFragment)getFragmentManager().findFragmentById(R.id.ratingItemFirst);
		mSecondItem = (RatingItemFragment)getFragmentManager().findFragmentById(R.id.ratingItemSecond);
		mThirdItem = (RatingItemFragment)getFragmentManager().findFragmentById(R.id.ratingItemThird);
		refreshFragments();
	}
	
	public void refreshFragments()
	{
		Random r = new Random();
		
//		mFirstItem = new RatingItemFragment();
		mFirstItem.setData("Klasse XY", "", (int)(r.nextDouble()*200)+",00 €", Ranking.FIRST);
		
		
//		mSecondItem = new RatingItemFragment();
		mSecondItem.setData("Klasse XY", "", (int)(r.nextDouble()*200)+",00 €", Ranking.SECOND);
		
		
//		mThirdItem = new RatingItemFragment();
		mThirdItem.setData("Klasse XY", "", (int)(r.nextDouble()*200)+",00 €", Ranking.THIRD);
//		mFirstFrame.removeAllViews();
//		mSecondFrame.removeAllViews();
//		mThirdFrame.removeAllViews();
//		
//		FragmentTransaction ft = getFragmentManager().beginTransaction();
//		
////		ft.add(R.id.firstFrame, mFirstItem, "first");
////		ft.add(R.id.secondFrame, mSecondItem, "second");
////		ft.add(R.id.thirdFrame, mThirdItem, "third");
//		ft.replace(R.id.firstFrame, mFirstItem, "first");
//		ft.replace(R.id.secondFrame, mSecondItem, "second");
//		ft.replace(R.id.thirdFrame, mThirdItem, "third");
//		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//		ft.commit();
	}
}
