/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.vpn;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.vpn.VpnManager;
import android.net.vpn.VpnState;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import at.sesame.fhooe.lib.R;

public class VpnBroadcastReceiver 
extends BroadcastReceiver
{
	private static final String TAG = "VpnBroadcast Receiver";
	private StringBuffer mStatesBuffer = new StringBuffer();
	
	private static ProgressDialog mProgress ;
	private static AlertDialog mAlertDialog;
	
	private int mTimeout = 45000;
	
	private VpnState mLastState = null;
	
	ProgressThread mProgressThread;

	private Context mContext;
	
	public VpnBroadcastReceiver(Context _c)
	{
		mContext = _c;

		IntentFilter filter = new IntentFilter();
        filter.addAction("vpn.connectivity");
        mContext.getApplicationContext().registerReceiver(this, filter);
        createProgressDialog();
        createAlertDialog();
       
	}
	
	private void createProgressDialog()
	{
		 mProgress = new ProgressDialog(mContext);
	     mProgress.setMessage(mContext.getString(R.string.vpnBroadcastReceiver_connectionProgressDialogMessage));
	     mProgress.setTitle(mContext.getString(R.string.vpnBroadcastReceiver_connectionProgressDialogTitle));
	     mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
	
	private void createAlertDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setCancelable(false)
		       .setNeutralButton(R.string.vpnBroadcastReceiver_alertDialogNeutralButtonText, new OnClickListener() 
		       {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
		       }});
		mAlertDialog = builder.create();
	}
	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		android.net.vpn.VpnState state = null;
		try
		{
		state = (android.net.vpn.VpnState) arg1.getExtras().get(VpnManager.BROADCAST_CONNECTION_STATE);
		}
		catch(ClassCastException _cce)
		{
			Log.e(TAG, "couldn't cast state");
			@SuppressWarnings("unused")
			xink.vpn.wrapper.VpnState Xinkstate = (xink.vpn.wrapper.VpnState)arg1.getExtras().get(VpnManager.BROADCAST_CONNECTION_STATE);
			
//			return;
		}

		if(state == VpnState.CONNECTING)
		{	
			mProgressThread = new ProgressThread(handler);
			mProgressThread.start();
			mProgress.show();
		}
		else if(state == VpnState.DISCONNECTING ||state == VpnState.CONNECTED ||state == VpnState.IDLE)
		{
			mProgress.dismiss();
			if(null!=mProgressThread)
			{
				mProgressThread.stopProgressThread();
			}
		}
		
		String msg ="";
		if(state == VpnState.CONNECTED)
		{
			msg = mContext.getString(R.string.vpnBroadcastReceiver_connectedSuccessfullyMsg);
		}
		else if(state ==VpnState.DISCONNECTING)
		{
			if(mLastState == VpnState.CONNECTED)
			{
				msg = mContext.getString(R.string.vpnBroadcastReceiver_disconnectedSuccessfullyMsg);
			}
			else
			{
				msg = mContext.getString(R.string.vpnBroadcastReceiver_connectionFailedMsg);
			}
		}
		if(state==VpnState.CONNECTED ||state == VpnState.DISCONNECTING)
		{
			mAlertDialog.setMessage(msg);
			mAlertDialog.show();
		}
		mStatesBuffer.append(state.toString());


		String profile = arg1.getStringExtra(VpnManager.BROADCAST_PROFILE_NAME);
		Integer errorCode =  (Integer) arg1.getExtras().get(VpnManager.BROADCAST_ERROR_CODE);
		String errorStmt ="VPN_NO_ERROR";
		
		if(null!= errorCode)
		{
			switch(errorCode)
			{
			case VpnManager.VPN_ERROR_AUTH:
				errorStmt = "VPN_ERROR_AUTH";
				break;
			case VpnManager.VPN_ERROR_CHALLENGE:
				errorStmt = "VPN_ERROR_CHALLENGE";
				break;
			case VpnManager.VPN_ERROR_CONNECTION_FAILED:
				errorStmt = "VPN_ERROR_CONNECTION_FAILED";
				break;
			case VpnManager.VPN_ERROR_CONNECTION_LOST:
				errorStmt = "VPN_ERROR_CONNECTION_LOST";
				break;
			case VpnManager.VPN_ERROR_LARGEST:
				errorStmt = "VPN_ERROR_LARGEST";
				break;
			case VpnManager.VPN_ERROR_NO_ERROR:
				errorStmt = "VPN_ERROR_NO_ERROR";
				break;
			case VpnManager.VPN_ERROR_PPP_NEGOTIATION_FAILED:
				errorStmt = "VPN_ERROR_PPP_NEGOTIATION_FAILED";
				break;
			case VpnManager.VPN_ERROR_REMOTE_HUNG_UP:
				errorStmt = "VPN_ERROR_REMOTE_HUNG_UP";
				break;
			case VpnManager.VPN_ERROR_REMOTE_PPP_HUNG_UP:
				errorStmt = "VPN_ERROR_REMOTE_PPP_HUNG_UP";
				break;
			case VpnManager.VPN_ERROR_UNKNOWN_SERVER:
				errorStmt = "VPN_ERROR_UNKNOWN_SERVER";
				break;
			}
		}
		String profileTag = mContext.getString(R.string.vpnBroadcastReceiver_profileToastTag);
		String stateTag = mContext.getString(R.string.vpnBroadcastReceiver_stateToastTag);
		String errorTag = mContext.getString(R.string.vpnBroadcastReceiver_errorToastTag);
		
		String toastMsg = profileTag+profile+", "+stateTag+state.toString()+", "+errorTag+errorStmt;
		Log.e("VpnBroadcastReceiver", msg);
		
		Toast.makeText(arg0, toastMsg, Toast.LENGTH_LONG).show();
		mLastState = state;
	}
	
	 final Handler handler = new Handler() {
	        public void handleMessage(Message msg) {
	            int curRuntime = msg.arg1;
	            String curMsg = mContext.getString(R.string.vpnBroadcastReceiver_connectionText)+" ("+(mTimeout-curRuntime)/1000+")";
	            mProgress.setMessage(curMsg);
	            if (curRuntime >= mTimeout){
	                mProgress.dismiss();
	                mProgressThread.stopProgressThread();
	            }
	        }
	    };

	public void printStates()
	{
		Log.e(TAG, mStatesBuffer.toString());
	}
}
