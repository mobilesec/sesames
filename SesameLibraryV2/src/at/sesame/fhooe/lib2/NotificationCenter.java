package at.sesame.fhooe.lib2;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.pms.hosts.*;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;


public class NotificationCenter 
{
	private static final String TAG = "NotificationCenter";
	
	private static Timer mNotificationTimer;
	private static final long NOTIFICATION_PERIOD = 30000;
	
	private static final int NOTIFICATION_ID = 1;
	private static final CharSequence NOTIFICATION_TITLE = "Sesame notification";
	
	private static NotificationManager mNotifyMan;
	
	private static Context mCtx;
	
	private static final HostList EDV1_HOSTS = new EDV1Hosts();
	private static final HostList EDV3_HOSTS = new EDV3Hosts();
	private static final HostList EDV6_HOSTS = new EDV6Hosts();
	
	private static Class mIntentClass;
	
	public static void start(Context _ctx, Class _intentClass)
	{
		mCtx = _ctx;
		mIntentClass = _intentClass;
		mNotifyMan = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMan.cancelAll();
		startTimer();
	}
	
	public static void stop()
	{
		if(null!=mNotificationTimer)
		{
			mNotificationTimer.cancel();
			mNotificationTimer.purge();
		}
	}
	
	private static void startTimer()
	{
		stop();
		mNotificationTimer = new Timer("Notifications");
		mNotificationTimer.schedule(new NotificationTask(), 0, NOTIFICATION_PERIOD);
	}
	
	
	private static class NotificationTask 
	extends TimerTask
	{
		@Override
		public void run() 
		{
			Random r = new Random();
//			showNotification(getNumNotifications(EDV1_HOSTS), getNumNotifications(EDV3_HOSTS), getNumNotifications(EDV6_HOSTS));
			showNotification(r.nextInt(20), r.nextInt(20), r.nextInt(20));
		}
		
	}
	
	private static int getNumNotifications(HostList _hosts)
	{
		int res = 0;
	
		for(String mac:_hosts.getMacList())
		{
			ControllableDevice cd = SesameDataCache.getInstance().getDeviceByMac(mac);
			if(null!=cd)
			{
				if(cd.getIdleSinceMinutes()>=ControllableDevice.IDLE_NOTIFICATION_THRESHOLD)
				{
					res++;
				}
			}
		}
		return res;
	}
	
	@SuppressWarnings("deprecation")
	private static void showNotification(int _numEdv1Notifications, int _numEdv3Notifications, int _numEdv6Notifications)
	{
		final StringBuilder textBuilder = new StringBuilder();
		String prefix = "Warnungen für ";
		if(_numEdv1Notifications == 0 && _numEdv3Notifications == 0 && _numEdv6Notifications == 0)
		{
			return;
		}
		if(_numEdv1Notifications!=0)
		{
			textBuilder.append(prefix);
			textBuilder.append(mCtx.getString(R.string.global_Room1_name));
			textBuilder.append(": ");
			textBuilder.append(_numEdv1Notifications);
		}
		
		if(_numEdv3Notifications!=0)
		{
			if(_numEdv1Notifications!=0)
			{
				textBuilder.append("\n");
			}
			textBuilder.append(prefix);
			textBuilder.append(mCtx.getString(R.string.global_Room3_name));
			textBuilder.append(": ");
			textBuilder.append(_numEdv3Notifications);
		}
		
		if(_numEdv6Notifications!=0)
		{
			if(_numEdv1Notifications!=0 && _numEdv3Notifications !=0)
			{
				textBuilder.append("\n");
			}
			textBuilder.append(prefix);
			textBuilder.append(mCtx.getString(R.string.global_Room6_name));
			textBuilder.append(": ");
			textBuilder.append(_numEdv6Notifications);
		}

//		new Handler(Looper.getMainLooper()).post(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				Toast.makeText(mCtx, "notifying: "+textBuilder.toString(), Toast.LENGTH_LONG).show();
//			}
//		});
		
		Notification not = new Notification(R.drawable.ic_stat_warning, textBuilder.toString(), System.currentTimeMillis());
		not.ledARGB = 0xff00ff00;
		not.ledOffMS = 500;
		not.ledOnMS = 500;
		not.tickerText = textBuilder.toString();
		not.flags |= Notification.FLAG_SHOW_LIGHTS;
		long[] vibrate = {0,100,200,300};
		not.vibrate = vibrate;
		not.defaults |= Notification.DEFAULT_LIGHTS;
		
		Intent notificationIntent = new Intent(mCtx, mIntentClass);
		PendingIntent contentIntent = PendingIntent.getActivity(mCtx, NOTIFICATION_ID, notificationIntent, 0);
		not.setLatestEventInfo(mCtx, NOTIFICATION_TITLE, textBuilder.toString(), contentIntent);
//		builder.setContentTitle(NOTIFICATION_TITLE);
//		builder.setLights(Color.RED, 500, 500);
//		builder.setContentText(textBuilder.toString());

		mNotifyMan.notify(NOTIFICATION_ID, not);

	}

}
