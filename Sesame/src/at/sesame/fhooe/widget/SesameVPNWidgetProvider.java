/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import at.sesame.fhooe.R;
import at.sesame.fhooe.lib.exceptions.VpnException;
import at.sesame.fhooe.lib.vpn.VpnAccess;

public class SesameVPNWidgetProvider 
extends AppWidgetProvider 
{
	private static final String TAG = "SesameVPNWidgetProvider";
	private static final String CONNECT_ACTION = "CONNECT_ACTION";
	private static final String DISCONNECT_ACTION = "DISCONNECT_ACTION";
	
	public void onEnabled(Context c)
	{
		Log.e(TAG, "onEnabled() called");
		try {
			VpnAccess.initialize(c);
		} catch (VpnException e) 
		{
			e.printStackTrace();
		}
	}
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	{
		Log.e(TAG, "onUpdate() called");
		Intent connectIntent = new Intent(context, SesameVPNWidgetProvider.class);
		connectIntent.setAction(CONNECT_ACTION);
        PendingIntent connectionIntent = PendingIntent.getBroadcast(context, 0, connectIntent, 0);
        
        Intent disconnectIntent = new Intent(context, SesameVPNWidgetProvider.class);
        disconnectIntent.setAction(DISCONNECT_ACTION);
        PendingIntent disconnectionIntent = PendingIntent.getBroadcast(context, 0, disconnectIntent, 0);

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.vpn_widget);
		views.setOnClickPendingIntent(R.id.widgetConnect, connectionIntent);
		views.setOnClickPendingIntent(R.id.widgetDisconnect, disconnectionIntent);
		
		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}
	
	public void onReceive(Context context, Intent intent) {
		 super.onReceive(context, intent);
		 Log.e(TAG, "onReceive() action:"+intent.getAction());
		 if(intent.getAction().equals(CONNECT_ACTION))
		 {
			 Log.e(TAG, "connect in onReceive");
			 //TODO replace null with meaningful mode
			 if(VpnAccess.connect(null))
			 {
				 Log.e(TAG, "connected successful");
			 }
			 else
			 {
				 Log.e(TAG, "connection failed");
			 }
		 }
		 else if(intent.getAction().equals(DISCONNECT_ACTION))
		 {
			 Log.e(TAG, "disconnect in onReceive");
			 VpnAccess.disconnect();
		 }
	}
}
