package at.sesame.fhooe.midsd.hd;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.sesame.fhooe.lib.ui.FragmentTabListener;
import at.sesame.fhooe.midsd.R;
import at.sesame.fhooe.midsd.hd.pms.PMSFragment;
import at.sesame.fhooe.midsd.md.WheelFragment;
import at.sesame.fhooe.midsd.ui.MeterWheelFragment;

public class HD_Fragment 
extends Fragment 
{
	private Context mCtx;
	private LayoutInflater mLi;
	private FragmentManager mFragMan;
	
//	private MeterWheelFragment mEdv1Frag;
//	private MeterWheelFragment mEdv3Frag;
//	private MeterWheelFragment mEdv6Frag;
	
	private WheelFragment mEdv1WheelFrag;
	private WheelFragment mEdv3WheelFrag;
	private WheelFragment mEdv6WheelFrag;
	
	private PMSFragment mPMSFrag;
	
	private static final int WHEEL_TEXT_SIZE = 20;
	
	public HD_Fragment(Context _ctx, FragmentManager _fm)
	{
		mCtx = _ctx;
		mLi = LayoutInflater.from(_ctx);
		mFragMan = _fm;
	}
	
	private void initializeFragments(FragmentManager _fm)
	{
//		mEdv1Frag = new MeterWheelFragment(_fm, mCtx, WHEEL_TEXT_SIZE);
//		mEdv3Frag = new MeterWheelFragment(_fm, mCtx, WHEEL_TEXT_SIZE);
//		mEdv6Frag = new MeterWheelFragment(_fm, mCtx, WHEEL_TEXT_SIZE);
		mEdv1WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
		mEdv3WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
		mEdv6WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
		
		mPMSFrag = new PMSFragment(mCtx);
		
		FragmentTransaction ft = _fm.beginTransaction();
//		ft.add(R.id.hd_layout_edv1Frame, mEdv1Frag);
//		ft.add(R.id.hd_layout_edv3Frame, mEdv3Frag);
//		ft.add(R.id.hd_layout_edv6Frame, mEdv6Frag);
		
		ft.add(R.id.hd_layout_edv1Frame, mEdv1WheelFrag);
		ft.add(R.id.hd_layout_edv3Frame, mEdv3WheelFrag);
		ft.add(R.id.hd_layout_edv6Frame, mEdv6WheelFrag);
		
		ft.add(R.id.hd_layout_pmsFrame, mPMSFrag);
		
		ft.commit();
	}
	
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		initializeFragments(mFragMan);
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
