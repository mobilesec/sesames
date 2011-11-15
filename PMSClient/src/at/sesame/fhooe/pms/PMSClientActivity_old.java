/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.pms;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import at.sesame.fhooe.lib.pms.PMSProvider;
import at.sesame.fhooe.lib.pms.errorhandling.ErrorForwarder;
import at.sesame.fhooe.lib.pms.errorhandling.IErrorReceiver;
import at.sesame.fhooe.lib.pms.model.ControllableDevice;
import at.sesame.fhooe.lib.pms.model.ExtendedPMSStatus;
import at.sesame.fhooe.lib.pms.model.PMSStatus;
import at.sesame.fhooe.lib.pms.model.ControllableDevice.PowerOffState;


/**
 * this activity provides the GUI for interaction with the Sesame-S
 * Power Management Service
 *
 */
public class PMSClientActivity_old 
extends Activity
implements OnItemSelectedListener, OnClickListener, IErrorReceiver
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "PMSClientActivity";
	
	/**
	 * the drop-down menu for device selection
	 */
	private Spinner mDeviceSelection;
	
	/**
	 * the array adapter presenting the model for device selection
	 */
	private ArrayAdapter<String> mSpinnerAdapter;

	/**
	 * button to shut down the selected device
	 */
	private Button mShutDownButt;
	
	/**
	 * button to wake the selected device up
	 */
	private Button mWakeupButt;
	
	/**
	 * button to put the selected device to sleep
	 */
	private Button mSleepButt;
	
	/**
	 * button to toast the status of the selected device
	 */
	private Button mStatusButt;
	
	/**
	 * button to toast the extended status of the selected device
	 */
	private Button mExtendedStatusButt;

	/**
	 * the currently selected ControllableDevice
	 */
	private ControllableDevice mCurrentDevice;
	
	/**
	 * a list of all selectable devices
	 */
	private ArrayList<ControllableDevice> mDevices = new ArrayList<ControllableDevice>();
	
	/**
	 * the ProgressDialog to indicate networking
	 */
	ProgressDialog mNetworkingDialog;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_old);
		ErrorForwarder.getInstance().register(this);
		
		setupNetworkingDialog();
		
		loadDevices();

		mSleepButt = (Button)findViewById(R.id.main_xml_sleepButt);
		mSleepButt.setOnClickListener(this);

		mShutDownButt = (Button)findViewById(R.id.main_xml_shutDownButt);
		mShutDownButt.setOnClickListener(this);

		mStatusButt = (Button)findViewById(R.id.main_xml_statusButt);
		mStatusButt.setOnClickListener(this);

		mExtendedStatusButt = (Button)findViewById(R.id.main_xml_extendedStatusButt);
		mExtendedStatusButt.setOnClickListener(this);

		mWakeupButt = (Button)findViewById(R.id.main_xml_wakeupButt);
		mWakeupButt.setOnClickListener(this);
	}
	
	/**
	 * creates the networking dialog
	 */
	private void setupNetworkingDialog()
	{
		mNetworkingDialog = new ProgressDialog(PMSClientActivity_old.this);
		mNetworkingDialog.setMessage("Networking in progress, please wait...");
		mNetworkingDialog.setCancelable(false);
		mNetworkingDialog.setCanceledOnTouchOutside(false);
		mNetworkingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
	
	/**
	 * queries the mac addresses of all devices from the PMS, creates ControllableDeviecs from it
	 * and triggers initialization of the drop-down menu
	 */
	private void loadDevices() 
	{
		showNetworkingDialog();
		//only done in a own thread in order for the networking dialog to show
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				Looper.prepare(); //needed to fix exceptions with AsyncTasks started in this method
				ArrayList<String> macs = new ArrayList<String>();
				macs = PMSProvider.getDeviceList();
				for(int i = 0;i<macs.size();i++)
				{
					ControllableDevice dev = new ControllableDevice(getApplicationContext(), macs.get(i), "admin", "pwd", true);
					mDevices.add(dev);
					Log.e(TAG, dev.toString());
				}
				dismissNetworkingDialog();
				initSpinner();
				Looper.loop(); //needed to fix exceptions with AsyncTasks started in this method
			}
		}).start();
		
	}
	
	/**
	 * shows the networking dialog
	 */
	private void showNetworkingDialog()
	{
		mNetworkingDialog.show();
	}
	
	/**
	 * dismisses the networking dialog if it is showing
	 */
	private void dismissNetworkingDialog()
	{
		if(mNetworkingDialog.isShowing())
		{
			mNetworkingDialog.dismiss();
		}
	}
	
	/**
	 * fills the drop-down menu with hostnames of all ControllableDevices
	 */
	private void initSpinner()
	{
		runOnUiThread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				mSpinnerAdapter= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, getControllableHostNames());
				mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				mDeviceSelection = (Spinner)findViewById(R.id.main_xml_device_spinner);
				mDeviceSelection.setAdapter(mSpinnerAdapter);
				mDeviceSelection.setOnItemSelectedListener(PMSClientActivity_old.this);
				
				setCurrentDevice(mDeviceSelection.getSelectedItem());		
			}
		});
	}
	
	/**
	 * extracts the hostnames of all ControllableDevices and returns them in a list
	 * @return a list of all available hostnames
	 */
	private ArrayList<String> getControllableHostNames()
	{
		ArrayList<String> hostnames = new ArrayList<String>();
		for(ControllableDevice cd:mDevices)
		{
			hostnames.add(cd.getHostname());
		}
		return hostnames;
	}

	/**
	 * sets the currently controlled device and enables/disables buttons based
	 * on the alive-status of the current device
	 * @param _item item that is currently selected in the drop-down menu
	 */
	private void setCurrentDevice(Object _item)
	{
		ControllableDevice curDev = getControllableDeviceFromSelectedItem(_item);
		if(null!=curDev)
		{
			mCurrentDevice = curDev;
			if(mCurrentDevice.isAlive())
			{
				mWakeupButt.setEnabled(false);
				mSleepButt.setEnabled(true);
				mShutDownButt.setEnabled(true);
			}
			else
			{
				mSleepButt.setEnabled(false);
				mShutDownButt.setEnabled(false);
				mWakeupButt.setEnabled(true);
			}
		}
		else
		{
			Log.e(TAG,"selected device was not found");
		}
	}

	/**
	 * searches the list of ControllableDevices for a hostname specified by the _item parameter
	 * @param _item contains a hostname to look up in the list of devices
	 * @return the ControllableDevice specified by the _item parameter, null if no device matches the
	 * specified hostname
	 */
	private ControllableDevice getControllableDeviceFromSelectedItem(Object _item)
	{
		for(ControllableDevice cd:mDevices)
		{
			if(cd.getHostname().equals(_item))
			{
				return cd;
			}
		}
		return null;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		setCurrentDevice(mDeviceSelection.getSelectedItem());
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) 
	{
	}

	@Override
	public void onClick(View v) 
	{
//		final ProgressDialog progressDialog = ProgressDialog.show(PMSClientActivity.this, "", "Networking in progress");
		showNetworkingDialog();
		final int id = v.getId();
		
		new Thread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				switch(id)
				{
				case R.id.main_xml_sleepButt:
					if(mCurrentDevice.powerOff(PowerOffState.sleep))
					{
						toastOnUiThread("sleep of "+mCurrentDevice.getHostname()+" successful");
					}
					break;
				case R.id.main_xml_shutDownButt:
					if(mCurrentDevice.powerOff(PowerOffState.shutdown))
					{
						toastOnUiThread("shutdown of "+mCurrentDevice.getHostname()+" successful");
					}
					else
					{
						toastOnUiThread("shutdown of "+mCurrentDevice.getHostname()+" failed");
					}

					break;
				case R.id.main_xml_statusButt:
					PMSStatus status = mCurrentDevice.getStatus();

					toastOnUiThread(status.toString());
					break;
				case R.id.main_xml_extendedStatusButt:
					ExtendedPMSStatus extStatus = mCurrentDevice.getExtendedStatus();
					if(null==extStatus)
					{
						break;
					}
					toastOnUiThread(extStatus.toString());
					break;
				case R.id.main_xml_wakeupButt:
					if(mCurrentDevice.wakeUp())
					{
						toastOnUiThread("wakeup of "+mCurrentDevice.getHostname()+" successful");
					}
					else
					{
						toastOnUiThread("wakeup of "+mCurrentDevice.getHostname()+" failed");
					}

					break;

				}
				dismissNetworkingDialog();
			}
		}).start();
		
	}
	
	/**
	 * whenever a thread other than the UI thread has to toast a message
	 * this method has to be used
	 * @param _msg the message to be toasted
	 */
	private void toastOnUiThread(final String _msg)
	{
		runOnUiThread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				Toast.makeText(getApplicationContext(), _msg, Toast.LENGTH_LONG).show();
				
			}
		});
	}

	@Override
	public void notifyError(RequestType _type, String _mac, int _code, final String _msg) 
	{
		toastOnUiThread(_msg);
	}
}