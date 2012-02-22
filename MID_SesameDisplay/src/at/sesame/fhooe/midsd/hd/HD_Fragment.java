package at.sesame.fhooe.midsd.hd;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.sesame.fhooe.midsd.R;
import at.sesame.fhooe.midsd.data.SesameDataCache;
import at.sesame.fhooe.midsd.hd.pms.PMSFragment;
import at.sesame.fhooe.midsd.ld.INotificationListener;
import at.sesame.fhooe.midsd.md.WheelFragment;

public class HD_Fragment 
extends Fragment
implements INotificationListener
{
	private static final String TAG = "HD_Fragment";
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
	
	private HD_TabFragment mTabFrag;
	
	private Notification mNotification;
	private NotificationManager mNotificationMan;
	private static final String NOTIFICATION_TITLE ="Sesame Notification";
	
	private static final int WHEEL_TEXT_SIZE = 30;
	
	private boolean mShowNotifications = false;
	
	public HD_Fragment(Context _ctx, FragmentManager _fm)
	{
		mCtx = _ctx;
		mLi = LayoutInflater.from(_ctx);
		mFragMan = _fm;
		initializeNotification();
		SesameDataCache.getInstance().addNotificationListener(this);
	}
	
	private void initializeNotification()
	{
		int icon = R.drawable.ic_warning;
		CharSequence tickerText = "Hello NOTIFICATION";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);
		mNotificationMan = (NotificationManager)mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	private void initializeFragments(FragmentManager _fm)
	{
//		mEdv1Frag = new MeterWheelFragment(_fm, mCtx, WHEEL_TEXT_SIZE);
//		mEdv3Frag = new MeterWheelFragment(_fm, mCtx, WHEEL_TEXT_SIZE);
//		mEdv6Frag = new MeterWheelFragment(_fm, mCtx, WHEEL_TEXT_SIZE);
//		mEdv1WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
//		mEdv3WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
//		mEdv6WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
		
		mPMSFrag = new PMSFragment(mCtx);
		
		mTabFrag = new HD_TabFragment(mCtx, mFragMan);
		
		FragmentTransaction ft = _fm.beginTransaction();
//		ft.add(R.id.hd_layout_edv1Frame, mEdv1Frag);
//		ft.add(R.id.hd_layout_edv3Frame, mEdv3Frag);
//		ft.add(R.id.hd_layout_edv6Frame, mEdv6Frag);
		
//		ft.add(R.id.hd_layout_edv1Frame, mEdv1WheelFrag);
//		ft.add(R.id.hd_layout_edv3Frame, mEdv3WheelFrag);
//		ft.add(R.id.hd_layout_edv6Frame, mEdv6WheelFrag);
		
		ft.add(R.id.hd_layout_pmsFrame, mPMSFrag);
		
		ft.add(R.id.hd_layout_chartFrame, mTabFrag);
		
		ft.commit();
	}
	
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		initializeFragments(mFragMan);
		mShowNotifications = true;
//		testNotifications();
	}
	
	

	@Override
	public void onDetach() {
		mShowNotifications = false;
		mNotificationMan.cancelAll();
		super.onDetach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return mLi.inflate(R.layout.hd_layout, null);
	}
	
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		testNotifications();
	}





	private void testNotifications()
	{
//		int icon = R.drawable.ic_warning;
//		CharSequence tickerText = "Hello NOTIFICATION";
//		long when = System.currentTimeMillis();
//		Notification notification = new Notification(icon, tickerText, when);
//		CharSequence contentTitle = "My notification";
//		CharSequence contentText = "Hello World!";
		
	}
	
	private void showNotification(String _title, String _text)
	{
		Intent notificationIntent = new Intent(mCtx, HD_Fragment.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mCtx, 0, notificationIntent, 0);
		mNotification.tickerText = _text;
		mNotification.setLatestEventInfo(mCtx, _title, _text, contentIntent);
	
		
		
		mNotificationMan.notify(1, mNotification);
	}
//
//	@Override
//	public View getView() {
//		// TODO Auto-generated method stub
//		return mLi.inflate(R.layout.hd_layout, null);
//	}

	@Override
	public void notifyAboutNotification(String _msg) 
	{
		if(mShowNotifications)
		{
			showNotification(NOTIFICATION_TITLE, _msg);			
		}
		
	}
	

}
