package at.sesame.fhooe.viewexperiments;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ColorFragment 
extends Fragment 
{

	private ColorView mView;
	
	public ColorFragment(Context _ctx)
	{
		ArrayList<Integer> colorList = new ArrayList<Integer>();
		colorList.add(Color.RED);
		colorList.add(Color.GREEN);
		colorList.add(Color.BLUE);
		colorList.add(Color.BLACK);
		colorList.add(Color.MAGENTA);
		mView = new ColorView(_ctx, colorList);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		ViewGroup parent = (ViewGroup)mView.getParent();
		if(null!=parent)
		{
			parent.removeView(mView);
		}
		
		return mView;
	}
	
//	@Override
//	public void onDestroy() 
//	{
//		super.onDestroy();
//		mView.cleanUp();
//	}
	
	
	

}
