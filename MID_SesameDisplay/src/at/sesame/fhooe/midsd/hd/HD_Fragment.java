package at.sesame.fhooe.midsd.hd;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.sesame.fhooe.midsd.R;

public class HD_Fragment 
extends Fragment 
{
	private Context mCtx;
	private LayoutInflater mLi;
	
	public HD_Fragment(Context _ctx)
	{
		mCtx = _ctx;
		mLi = LayoutInflater.from(_ctx);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return mLi.inflate(R.layout.hd_layout, null);
	}
//
//	@Override
//	public View getView() {
//		// TODO Auto-generated method stub
//		return mLi.inflate(R.layout.hd_layout, null);
//	}
	

}
