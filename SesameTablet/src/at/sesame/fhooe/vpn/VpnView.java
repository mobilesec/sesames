/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.vpn;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import at.sesame.fhooe.R;
import at.sesame.fhooe.lib.SesameFactory;
import at.sesame.fhooe.lib.exceptions.VpnException;
import at.sesame.fhooe.lib.util.DeviceStateInfo;
import at.sesame.fhooe.lib.vpn.IVpnAccess;
import at.sesame.fhooe.lib.vpn.VpnAccess.ConnectionMode;
import at.sesame.fhooe.lib.vpn.VpnSetting;
import at.sesame.fhooe.lib.vpn.xml.VpnSettingsParser;



/**
 * this class provides the GUI for establishing certificate- or PSK based VPN
 * connections
 * @author Peter Riedl
 *
 */
public class VpnView 
extends Activity 
implements OnClickListener
{
	
	public static final int VPN_TYPE_SELECTION_DIALOG_ID = 0;
	
	/**
	 * id for VPN certificate based connection dialog
	 */
	public static final int VPN_CERT_CONNECTION_DIALOG = 1;
	
	/**
	 * id for VPN PSK based connection dialog
	 */
	public static final int VPN_PSK_CONNECTION_DIALOG = 2;
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "VpnView";
	
	private IVpnAccess mVpnAccess;
	
	/**
	 * the VPN settings of this application
	 */
	private VpnSetting mSet;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vpn);
        
        mVpnAccess = SesameFactory.getVpnAccess();
//        View v = _inflater.inflate(R.layout.vpn, null);
        
        Button connectButt = (Button) findViewById(R.id.vpnConnectButton);
        connectButt.setOnClickListener(this);
        
        Button disconnectButton = (Button) findViewById(R.id.vpnDisconnectButton);
        disconnectButton.setOnClickListener(this);
        
        try {
        	mSet = new VpnSettingsParser().parseVpnSettings(getResources().getXml(R.xml.vpn_config));
			mVpnAccess.setVpnSetting(mSet);
			mVpnAccess.initialize(this);
        	Log.e(TAG, mSet.toString());
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VpnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        DeviceStateInfo.setContext(this);
        mVpnAccess.enableNotifications(this);
    }
    
	@Override
	public void onClick(View v) 
	{
		Log.e(TAG, "onClick() called");
		switch(v.getId())
		{
		case R.id.vpnConnectButton:
			try 
			{
				switch (mSet.getType()) {
				case CRT:
					showDialog(VPN_CERT_CONNECTION_DIALOG);
					break;
				case PSK:
					showDialog(VPN_PSK_CONNECTION_DIALOG);
					break;
				case CRT_PSK:
					showDialog(VPN_TYPE_SELECTION_DIALOG_ID);
					break;
				default:
					break;
				}
			} 
			catch (Exception e) 
			{
				Log.e(TAG, "connect", e);
			}
			break;
		case R.id.vpnDisconnectButton:
			try 
			{
				mVpnAccess.disconnect();
			} 
			catch (Exception e) 
			{
				Log.e(TAG, "disconnect", e);
			}
			break;
		}	
	}
	
	/**
	 * initiates the VpnAcces and triggers it to establish the desired connection
	 * @param _cm the type of VPN connection that should be established 
	 * @throws VpnException
	 */
	public void connectToVpn(ConnectionMode _cm) throws VpnException
	{
		Log.e(TAG, "connecting: "+_cm.name());
//		mVpnAccess.setConnectionMode(_cm);
		mVpnAccess.initialize(this);
		mVpnAccess.connect(_cm);
	}
	
	public Dialog onCreateDialog(int _id)
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	switch(_id)
    	{
    	case VPN_TYPE_SELECTION_DIALOG_ID:
    		String[] items = new String[]{	getString(R.string.VpnView_vpnModeSelectionCert),
    										getString(R.string.VpnView_vpnModeSelectionPSK)};
    		
    		builder.setTitle(getString(R.string.VpnView_vpnModeSelectionTitle));
    		builder.setItems(items, new DialogInterface.OnClickListener() 
    		{
    			public void onClick(DialogInterface dialog, int item) 
    			{
    				if(item==0)
    				{
    					showDialog(VPN_CERT_CONNECTION_DIALOG);
    				}
    				else if(item==1)
    				{
    					showDialog(VPN_PSK_CONNECTION_DIALOG);
    				}
    			}
    		});

    		break;
    	case VPN_PSK_CONNECTION_DIALOG:
    		builder.setMessage(R.string.VpnView_pskConnectionDialogMsg);
    		builder.setNeutralButton(R.string.VpnView_pskConnectionDialogButtonText, new DialogInterface.OnClickListener() {

    			@Override
    			public void onClick(DialogInterface dialog, int which) 
    			{
    				try 
    				{
    					connectToVpn(ConnectionMode.PSK);

    				} catch (VpnException e) 
    				{
    					e.printStackTrace();
    				}
    			}
    		});
    		break;
    	case VPN_CERT_CONNECTION_DIALOG:
    		
    		if(DeviceStateInfo.isMobileConnected())
    		{
    			try {
    				connectToVpn(ConnectionMode.CRT);
    				return null;
    			} catch (VpnException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    		
    		builder.setMessage(R.string.VpnView_crtConnectionDialogMsg);
    		builder.setNeutralButton(R.string.VpnView_crtConnectionDialogButtonText, new DialogInterface.OnClickListener() {
    			
    			@Override
    			public void onClick(DialogInterface dialog, int which) 
    			{
    				try 
    				{
    					connectToVpn(ConnectionMode.CRT);
    				} catch (VpnException e) 
    				{
    					e.printStackTrace();
    				}
    			}
    		});
    		break;
    	}
    
    	AlertDialog alert = builder.create();
		alert.show();
    	return null;
    }
//	public Dialog onCreateDialog(int _id)
//	{
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
////		final PhoneStateInfo psi = PhoneStateInfo.getInstance(getApplicationContext());
////		if(!psi.isInternetConnected())
////		{
////			Toast.makeText(getApplicationContext(), R.string.VpnView_noInternetConnectionMsg, Toast.LENGTH_LONG).show();
////			return null;
////		}
//		switch (_id) 
//		{
//		
//		default:
//			break;
//		}
//		AlertDialog alert = builder.create();
//		alert.show();
//		return alert;
//	}

}