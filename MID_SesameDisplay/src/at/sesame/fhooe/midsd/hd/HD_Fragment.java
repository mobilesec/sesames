package at.sesame.fhooe.midsd.hd;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.sesame.fhooe.midsd.R;
import at.sesame.fhooe.midsd.data.SesameDataCache;
import at.sesame.fhooe.midsd.hd.pms.PMSFragment;
import at.sesame.fhooe.midsd.ld.INotificationListener;
import at.sesame.fhooe.midsd.ui.MeterWheelFragment;

@SuppressWarnings("unused")
public class HD_Fragment 
extends Fragment
implements INotificationListener
{
	private static final String TAG = "HD_Fragment";
	private Context mCtx;
	private LayoutInflater mLi;
	private FragmentManager mFragMan;
	
	private MeterWheelFragment mEdv1Frag;
	private MeterWheelFragment mEdv3Frag;
	private MeterWheelFragment mEdv6Frag;
	
//	private WheelFragment mEdv1WheelFrag;
//	private WheelFragment mEdv3WheelFrag;
//	private WheelFragment mEdv6WheelFrag;
	
	private PMSFragment mPMSFrag;
	
	private HD_TabFragment mTabFrag;
	
	private Notification mNotification;
	private NotificationManager mNotificationMan;
	private static final String NOTIFICATION_TITLE ="Sesame Notification";
	
	private static final int WHEEL_TEXT_SIZE = 30;
	
	private boolean mShowNotifications = false;
	
	private Handler mUiHandler;
	
	public HD_Fragment(Context _ctx, FragmentManager _fm, Handler _uiHandler)
	{
		mCtx = _ctx;
		mLi = LayoutInflater.from(_ctx);
		mFragMan = _fm;
		mUiHandler = _uiHandler;
		initializeNotification();
		initializeFragments();
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
	
	private void initializeFragments()
	{
		String room1Name = mCtx.getString(R.string.global_Room1_name);
		String room3Name = mCtx.getString(R.string.global_Room3_name);
		String room6Name = mCtx.getString(R.string.global_Room6_name);
		
		mEdv1Frag = new MeterWheelFragment(mCtx, mUiHandler, room1Name, 20.0f, "test1", 14.0f, WHEEL_TEXT_SIZE, 5, 0);
		mEdv3Frag = new MeterWheelFragment(mCtx, mUiHandler, room3Name, 20.0f, "test1", 14.0f, WHEEL_TEXT_SIZE, 5, 0);
		mEdv6Frag = new MeterWheelFragment(mCtx, mUiHandler, room6Name, 20.0f, "test1", 14.0f, WHEEL_TEXT_SIZE, 5, 0);
//		mEdv1WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
//		mEdv3WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
//		mEdv6WheelFrag = new WheelFragment(mCtx, null, 5, null, WHEEL_TEXT_SIZE);
		mUiHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mPMSFrag = new PMSFragment(mCtx);
			}
		});
		
		
		mTabFrag = new HD_TabFragment(mCtx, mFragMan);
		
		
	}
	
	private void addFragments()
	{
		FragmentTransaction ft = mFragMan.beginTransaction();
		ft.add(R.id.hd_layout_edv1Frame, mEdv1Frag);
		ft.add(R.id.hd_layout_edv3Frame, mEdv3Frag);
		ft.add(R.id.hd_layout_edv6Frame, mEdv6Frag);
		
//		ft.add(R.id.hd_layout_edv1Frame, mEdv1WheelFrag);
//		ft.add(R.id.hd_layout_edv3Frame, mEdv3WheelFrag);
//		ft.add(R.id.hd_layout_edv6Frame, mEdv6WheelFrag);
		
		ft.add(R.id.hd_layout_pmsFrame, mPMSFrag);
		
		ft.add(R.id.hd_layout_chartFrame, mTabFrag);
		
		ft.commit();
//		mFragMan.executePendingTransactions();
	}
	
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mShowNotifications = true;
		addFragments();
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
		View v = mLi.inflate(R.layout.hd_layout, null);
		
		return v;
	}
	
	private void showNotification(String _title, String _text)
	{
		Intent notificationIntent = new Intent(mCtx, HD_Fragment.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mCtx, 0, notificationIntent, 0);
		mNotification.tickerText = _text;
		mNotification.setLatestEventInfo(mCtx, _title, _text, contentIntent);
	
		
		
		mNotificationMan.notify(1, mNotification);
	}

	@Override
	public void notifyAboutNotification(String _msg) 
	{
		if(mShowNotifications)
		{
			showNotification(NOTIFICATION_TITLE, _msg);			
		}
		
	}
	

}
