/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.wifi;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import at.sesame.fhooe.R;
import at.sesame.fhooe.lib.wifi.IWifiScanReceiver;
import at.sesame.fhooe.lib.wifi.WifiAccess;

/**
 * this class implements the GUI to interact with all
 * provided wifi functionality
 * @author Peter Riedl
 *
 */
public class WifiView 
extends Activity
implements OnClickListener, Runnable, OnCheckedChangeListener, IWifiScanReceiver
{
	/**
	 * EditText to display
	 */
	private EditText mWifiInfoField;
	
	/**
	 * button to manually start a wifi scan
	 */
	private Button mWifiScanButton;
	
	/**
	 * checkbox to enable/disable automatic wifi scanning
	 */
	private CheckBox mAutoUpdateBox;
	
	/**
	 * the WifiAccess to access the WifiManager
	 */
	private WifiAccess mWifiAccess;
	
	/**
	 * flag indicating whether wifiscans are started periodically
	 */
	private boolean mAutoUpdate  = false;
	
	/**
	 * flag indicating whether the auto-update thread is running or not
	 */
	private boolean mThreadRunning = true;
	
	/**
	 * the thread performing automatic wifi scans
	 */
	private Thread mAutoUpdater;
	
	@Override
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.wifi);
//		View v = _inflater.inflate(R.layout.wifi, null);
		
		mWifiInfoField = (EditText) findViewById(R.id.wifiInfoField);
		
		mWifiScanButton = (Button) findViewById(R.id.wifiScanButton);
		mWifiScanButton.setOnClickListener(this);
		
		mAutoUpdateBox = (CheckBox)findViewById(R.id.autoUpdateBox);
		mAutoUpdateBox.setSelected(false);
		mAutoUpdateBox.setOnCheckedChangeListener(this);
		
		mWifiAccess = WifiAccess.getInstance(this, false);
		mWifiAccess.addWifiScanReceiver(this);
		
		mAutoUpdater = new Thread(this);
		mAutoUpdater.start();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		mThreadRunning = false;
	}
	
	@Override
	public void onClick(View arg0) 
	{
		mWifiAccess.startSingleWifiScan();
	}
	
	@Override
	public void setWifiScanResults(ArrayList<ScanResult> _results)
	{
		mWifiInfoField.setText("");
		for(ScanResult sr:_results)
		{
			mWifiInfoField.append(""+sr+"\n\n");
		}
	}

	/**
	 * periodically issues wifi scans
	 */
	@Override
	public void run() 
	{
		while(mThreadRunning)
		{
			if(mAutoUpdate)
			{
				mWifiAccess.startSingleWifiScan();	
			}
			try 
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
	{
		mWifiScanButton.setEnabled(!arg1);
		mAutoUpdate = arg1;
	}
}
