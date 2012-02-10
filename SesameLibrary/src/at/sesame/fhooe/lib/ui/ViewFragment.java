package at.sesame.fhooe.lib.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewFragment 
extends Fragment 
{
	private View mView;
	
	public ViewFragment(View _v)
	{
		mView = _v;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return mView;
	}
	
	

}
