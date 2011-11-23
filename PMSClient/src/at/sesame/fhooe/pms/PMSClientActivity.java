/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 11/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.pms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.sesame.fhooe.lib.pms.PMSProvider;
import at.sesame.fhooe.lib.pms.errorhandling.ErrorForwarder;
import at.sesame.fhooe.lib.pms.errorhandling.IErrorReceiver;
import at.sesame.fhooe.lib.pms.model.ControllableDevice;
import at.sesame.fhooe.lib.pms.model.ControllableDevice.PowerOffState;
import at.sesame.fhooe.pms.list.commands.CommandAdapter;
import at.sesame.fhooe.pms.list.commands.CommandListEntry;
import at.sesame.fhooe.pms.list.commands.CommandListEntry.CommandType;
import at.sesame.fhooe.pms.list.controllabledevice.ControllableDeviceAdapter;
import at.sesame.fhooe.pms.list.controllabledevice.ControllableDeviceListEntry;
import at.sesame.fhooe.pms.list.controllabledevice.IListEntry;
import at.sesame.fhooe.pms.list.controllabledevice.SeparatorListEntry;
import at.sesame.fhooe.pms.list.controllabledevice.SeparatorListEntry.ListType;

/**
 * this class represents the activity that accesses the PMS and displays all
 * information provided by the PMS
 * @author Peter Riedl
 *
 */
public class PMSClientActivity 
extends Activity 
implements OnClickListener, IErrorReceiver
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "FancyPMSClientActivity";

	/**
	 * integer constant for displaying the dialog for actions on active devices
	 */
	private static final int ACTIVE_DEVICE_ACTION_DIALOG = 0;
	
	/**
	 * integer constant for displaying the dialog for actions on inactive devices
	 */
	private static final int INACTIVE_DEVICE_ACTION_DIALOG = 1;
	
	/**
	 * integer constant for displaying the dialog when no internet connection is detected
	 */
	private static final int NO_NETWORK_DIALOG = 2;
	
	/**
	 * integer constant for displaying the dialog when the shutdown of a computer failed
	 */
	private static final int CANT_SHUTDOWN_DIALOG = 3;
	
	/**
	 * integer constant for displaying the dialog when the wake up of a computer failed
	 */
	private static final int CANT_WAKEUP_DIALOG = 4;

	/**
	 * the key under which the hostname information is stored in the bundle for onPrepareDialog
	 */
	private static final String BUNDLE_HOSTNAME_KEY = "hostname";

	/**
	 * threshold after which an idle time warning is displayed
	 */
	private static final int IDLE_MINUTES_WARNING_THRESHOLD = 30;

	/**
	 * a list of all controllable devices available
	 */
	private ArrayList<ControllableDevice> mAllDevices = new ArrayList<ControllableDevice>();
	
	/**
	 * mapping of hostname and selection status
	 */
	private HashMap<String, Boolean> mSelection = new HashMap<String, Boolean>();
	
	/**
	 * list of all displayed list entries
	 */
	private ArrayList<IListEntry> mEntries = new ArrayList<IListEntry>();

	/**
	 * the adapter used for displaying the list entries
	 */
	private ControllableDeviceAdapter mAdapter;

	/**
	 * the thread that queries the status of all devies in the background
	 */
	private DeviceStateUpdateThread mUpdateThread;

	/**
	 * the currently selected device
	 */
	private ControllableDevice mSelectedDevice;

	/**
	 * the listview for all ControllableDevices
	 */
	private ListView mDevList;
	
	/**
	 * container for the buttons that are associated with multiple selected
	 * active devices
	 */
	private ViewGroup mActiveDeviceControlContainer;
	
	/**
	 * container for the buttons that are associated with multiple selected
	 * inactive devices
	 */
	private ViewGroup mInactiveDeviceControlContainer;

	/**
	 * the button that puts all selected devices to sleep
	 */
	private Button mSleepAllButt;
	
	/**
	 * the button that shuts all selected devices down
	 */
	private Button mPowerOffAllButt;
	
	/**
	 * the button that wakes all selected devices up
	 */
	private Button mWakeUpAllButt;


	/**
	 * this enumeration is used to determine which kind of device was selected
	 * @author admin
	 *
	 */
	private enum SelectedType
	{
		active,
		inactive,
		none
	};

	/**
	 * the ProgressDialog to indicate networking
	 */
	private ProgressDialog mNetworkingDialog;

	@Override
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.main);

		setupNetworkingDialog();
		if(!checkConnectivity())
		{
			showDialog(NO_NETWORK_DIALOG);

			return;
		}
		ErrorForwarder.getInstance().register(this);
		queryControllableDevices();

		refreshListEntries();
		mAdapter = new ControllableDeviceAdapter(this, mEntries);
		mDevList = (ListView)findViewById(R.id.deviceList);
		mDevList.setAdapter(mAdapter);

		mActiveDeviceControlContainer = (ViewGroup)findViewById(R.id.activeDeviceControllContainer);
		mInactiveDeviceControlContainer = (ViewGroup)findViewById(R.id.inactiveDeviceControllContainer);
		setControlContainerVisibility(View.GONE, View.GONE);

		mSleepAllButt = (Button)findViewById(R.id.sleepButton);
		mSleepAllButt.setOnClickListener(this);

		mPowerOffAllButt = (Button)findViewById(R.id.shutDownButton);
		mPowerOffAllButt.setOnClickListener(this);

		mWakeUpAllButt = (Button)findViewById(R.id.wakeUpButton);
		mWakeUpAllButt.setOnClickListener(this);

		mUpdateThread = new DeviceStateUpdateThread(this, mAllDevices);
	}

	/**
	 * checks if the device currently is connected to the internet
	 * @return true if the device is connected, false otherwise
	 */
	private boolean checkConnectivity() 
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mUpdateThread.pause();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mUpdateThread.pause();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.e(TAG, "onResume");
		if(checkConnectivity())
		{
			mUpdateThread.start();
		}
	}

	@Override
	public Dialog onCreateDialog(int _id)
	{
		final Dialog d = new Dialog(this);

		d.setContentView(R.layout.custom_action_dialog);
		d.setTitle(mSelectedDevice.getHostname());
		final ArrayList<CommandListEntry> cles = new ArrayList<CommandListEntry>();
		ListView commands = (ListView)d.findViewById(R.id.cusomtActionDialogCommandList);
		commands.setAdapter(new CommandAdapter(getApplicationContext(), cles));
		TextView message = (TextView)d.findViewById(R.id.messageLabel);

		commands.setBackgroundColor(android.R.color.white);
		switch(_id)
		{
		case ACTIVE_DEVICE_ACTION_DIALOG:
			if(mSelectedDevice.getIdleSinceMinutes()<IDLE_MINUTES_WARNING_THRESHOLD)
			{

				message.setText(getString(R.string.PMSClientActivity_activeDeviceActionDialogBaseMessage)+mSelectedDevice.getIdleSinceMinutes()+")");
			}
			//			strings.add(getString(R.string.PMSClientActivity_activeDeviceDialogShutDownCommand));
			//			strings.add(getString(R.string.PMSClientActivity_activeDeviceDialogSleepCommand));
			//			strings.add(getString(android.R.string.cancel));
			cles.add(new CommandListEntry(getApplicationContext(), CommandType.shutDown));
			cles.add(new CommandListEntry(getApplicationContext(), CommandType.sleep));
			cles.add(new CommandListEntry(getApplicationContext(), CommandType.cancel));

			commands.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch(arg2)
					{
					case 0:
						mSelectedDevice.powerOff(PowerOffState.shutdown);
						markDirty(mSelectedDevice);
						d.dismiss();
						break;
					case 1:
						mSelectedDevice.powerOff(PowerOffState.sleep);
						markDirty(mSelectedDevice);
						d.dismiss();
						break;
					case 2:
						d.cancel();
						break;
					}
				}
			});	
			break;

		case INACTIVE_DEVICE_ACTION_DIALOG:
			//			strings.add(getString(R.string.PMSClientActivity_inactiveDeviceDialogWakeUpCommand));
			//			strings.add(getString(android.R.string.cancel));
			cles.add(new CommandListEntry(getApplicationContext(), CommandType.wakeUp));
			cles.add(new CommandListEntry(getApplicationContext(), CommandType.cancel));
			commands.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch(arg2)
					{
					case 0:
						mSelectedDevice.wakeUp();
						markDirty(mSelectedDevice);
						d.dismiss();
						break;
					case 1:
						d.cancel();
						break;
					}
				}
			});
			break;
		case NO_NETWORK_DIALOG:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(true);
			builder.setTitle(R.string.PMSClientActivity_noNetworkDialogTitle);
			builder.setMessage(R.string.PMSClientActivity_noNetworkDialogMessage);

			builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});
			return builder.create();
		case CANT_SHUTDOWN_DIALOG:
			AlertDialog.Builder shutDownBuilder = new AlertDialog.Builder(this);
			shutDownBuilder.setCancelable(true);
			shutDownBuilder.setTitle("");
			shutDownBuilder.setMessage(R.string.PMSClientActivity_cantShutDownDialogMessage);

			shutDownBuilder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});
			return shutDownBuilder.create();
		case CANT_WAKEUP_DIALOG:
			AlertDialog.Builder wakeUpBuilder = new AlertDialog.Builder(this);
			wakeUpBuilder.setCancelable(true);
			wakeUpBuilder.setTitle("");
			wakeUpBuilder.setMessage(R.string.PMSClientActivity_cantWakeUpDialogMessage);

			wakeUpBuilder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});
			return wakeUpBuilder.create();
		}
		return d;
	}



	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) 
	{
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog, args);
		switch(id)
		{
		case CANT_SHUTDOWN_DIALOG:
		case CANT_WAKEUP_DIALOG:
			AlertDialog ad = (AlertDialog)dialog;
			ad.setTitle(args.getCharSequence(BUNDLE_HOSTNAME_KEY));
			break;
		}
	}

	/**
	 * creates the networking dialog
	 */
	private void setupNetworkingDialog()
	{
		mNetworkingDialog = new ProgressDialog(PMSClientActivity.this);
		mNetworkingDialog.setMessage(getString(R.string.PMSClientActivity_networkingProgressDialogTitle));
		mNetworkingDialog.setCancelable(false);
		mNetworkingDialog.setCanceledOnTouchOutside(false);
		mNetworkingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}

	/**
	 * shows the networking dialog
	 */
	private void showNetworkingDialog()
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				Looper.prepare();
				mNetworkingDialog.show();
				Looper.loop();
			}
		}).start();
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
	 * parses the list of all devices, divides them into devices that are alive and those
	 * that are not and sorts the alive devices by idle time
	 */
	private void refreshListEntries() 
	{

		{
			ArrayList<ControllableDevice> activeDevs = new ArrayList<ControllableDevice>();
			ArrayList<ControllableDevice> inactiveDevs = new ArrayList<ControllableDevice>();

			for(ControllableDevice cd:mAllDevices)
			{
				if(cd.isAlive())
				{
					activeDevs.add(cd);
				}
				else
				{
					inactiveDevs.add(cd);
				}
			}
			if(!activeDevs.isEmpty())
			{
				Collections.sort(activeDevs, new ControllableDeviceComparator());
			}

			if(!inactiveDevs.isEmpty())
			{
				Collections.sort(inactiveDevs, new ControllableDeviceComparator());
			}
			mEntries.clear();
			mEntries.add(new SeparatorListEntry(getApplicationContext(), ListType.active, activeDevs.size()));
			for(ControllableDevice cd:activeDevs)
			{
				ControllableDeviceListEntry cdle = new ControllableDeviceListEntry(cd);
				cdle.setSelection(mSelection.get(cd.getMac()));
				mEntries.add(cdle);
			}
			mEntries.add(new SeparatorListEntry(getApplicationContext(), ListType.inactive, inactiveDevs.size()));
			for(ControllableDevice cd:inactiveDevs)
			{
				ControllableDeviceListEntry cdle = new ControllableDeviceListEntry(cd);
				cdle.setSelection(mSelection.get(cd.getMac()));
				mEntries.add(cdle);
			}
		}
	}

	/**
	 * queries which computers can be controlled via PMS and adds their ControllableDevice representations to the list
	 * of all devices
	 */
	private void queryControllableDevices() 
	{
		ArrayList<String> macs = PMSProvider.getDeviceList();
		mNetworkingDialog.setMax(macs.size());
		showNetworkingDialog();
		{
			mAllDevices = new ArrayList<ControllableDevice>();
			for(int i = 0;i<macs.size();i++)
			{
				ControllableDevice cd = new ControllableDevice(getApplicationContext(), macs.get(i), "admin1", "pwd", true);
				if(cd.isValid())
				{
					mAllDevices.add(cd);
					mSelection.put(cd.getMac(), false);
				}
				PMSClientActivity.this.mNetworkingDialog.incrementProgressBy(1);
			}
		}
		dismissNetworkingDialog();
	}

	/**
	 * this method is called whenever the power icon of a list entry was pressed
	 * @param _cd the ControllableDevice associated with the list entry
	 */
	public void handlePowerClick(ControllableDevice _cd)
	{
		ControllableDeviceListEntry cdle = getListEntryFromDevice(_cd);
		if(null==cdle)
		{
			return;
		}
		mSelectedDevice = cdle.getControllableDevice();

		if(mSelectedDevice.isAlive())
		{

			showDialog(ACTIVE_DEVICE_ACTION_DIALOG);
		}
		else
		{
			showDialog(INACTIVE_DEVICE_ACTION_DIALOG);
		}
	}

	/**
	 * handles an attempt to select a device. if the list of selected devices already contains a device 
	 * of different type (active/inactive) the device is not selected. 
	 * @param _cd the device to select
	 * @return true if the device was selected, false otherwise
	 */
	public boolean handleSingleSelectionAttempt(ControllableDevice _cd, boolean _checked)
	{
		boolean res = false;
		ControllableDeviceListEntry cdle = getListEntryFromDevice(_cd);
		if(null==cdle)
		{
			return false;
		}
		switch (getSelectedType()) {
		case none:
			if(_checked)
			{
				selectDevice(cdle, true);
				updateMultipleSelectionWidgets();
				res = true;
			}
			break;
		case active:
			if(cdle.getControllableDevice().isAlive())
			{
				selectDevice(cdle,_checked);
				updateMultipleSelectionWidgets();
				res = true;
			}
			else
			{
				toastSelectionFail();
				res = false;
			}
			break;
		case inactive:
			if(cdle.getControllableDevice().isAlive())
			{
				toastSelectionFail();
				res = false;
			}
			else
			{
				selectDevice(cdle,_checked);
				updateMultipleSelectionWidgets();
				res = true;
			}
			break;
		}

		if(getSelectedType()==SelectedType.none)
		{
			Log.e(TAG, "handling single selection attempt with no devices selected");
			setControlContainerVisibility(View.GONE, View.GONE);
		}
		notifyAdapter();
		return res;
	}

	/**
	 * this method is called when multiple selection of a category is requested (checkbox in separator)
	 * @param _type specifies which kind of ControllableDevice was selected
	 * @param _isChecked determines whether the device was selected or deselected
	 * @return true if the device could be selected, false otherwise
	 */
	public boolean handleMultipleSelectionAttempt(ListType _type, boolean _isChecked)
	{
		switch(_type)
		{
		case active:
			switch(getSelectedType())
			{
			case active:
			case none:

				return selectDevices(SelectedType.active, _isChecked);
				//				return true;
			case inactive:
				toastSelectionFail();
				return false;
			}
			break;
		case inactive:
			switch(getSelectedType())
			{
			case inactive:
			case none:
				return selectDevices(SelectedType.inactive, _isChecked);
				//				return true;
			case active:
				toastSelectionFail();
				return false;
			}
			break;
		}
		return false;

	}

	/**
	 * sets the selection state of all devices of a certain category
	 * @param _type the type of ControllableDevices to be selected
	 * @param _select determines whether the devices should selected or deselected
	 * @return true if the group of devices was selected, false otherwise
	 */
	private boolean selectDevices(SelectedType _type, boolean _select)
	{
		boolean selectionFlag;
		boolean result = false;
		switch (_type) 
		{
		case active:
			selectionFlag = true;
			break;
		case inactive:
			selectionFlag = false;
			break;
		case none:
			deselectAll();
			return false;
		default:
			return false;
		}

		for(IListEntry entry:mEntries)
		{
			if(entry instanceof ControllableDeviceListEntry)
			{
				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
				if(cdle.getControllableDevice().isAlive()==selectionFlag)
				{
					result = selectDevice(cdle, _select);
				}
			}
		}
		updateMultipleSelectionWidgets();
		notifyAdapter();
		return result;
	}


	/**
	 * changes the selection state of one device
	 * @param _cdle the list entry associated with the device
	 * @param _select determines whether the device should selected or deselected
	 * @return true if the device was selected, false otherwise
	 */
	private boolean selectDevice(ControllableDeviceListEntry _cdle, boolean _select)
	{
		_cdle.setSelection(_select);
		mSelection.put(_cdle.getControllableDevice().getMac(), _select);
		return _cdle.isSelected();
	}
	
	/**
	 * hides or shows the containers for actions on multiple selected ControllableDevices
	 */
	private void updateMultipleSelectionWidgets()
	{
		switch(getSelectedType())
		{
		case none:
			
			setControlContainerVisibility(View.GONE, View.GONE);
			break;
		case active:
			setControlContainerVisibility(View.VISIBLE, View.GONE);
			break;
		case inactive:
			setControlContainerVisibility(View.GONE, View.VISIBLE);
			break;
		}
	}

	/**
	 * toasts a message when a device could not be selected
	 */
	private void toastSelectionFail()
	{
		Toast.makeText(this, "only devices from the same category (active/inactive) can be selected at the same time.", Toast.LENGTH_LONG).show();
	}

	/**
	 * finds the listentry associated with the passed ControllableDevice
	 * @param _cd the ControllableDevice to get the list entry for
	 * @return the listentry associated with the passed ControllableDevice
	 */
	private ControllableDeviceListEntry getListEntryFromDevice(ControllableDevice _cd)
	{
		for(IListEntry entry:mEntries)
		{
			if(entry instanceof ControllableDeviceListEntry)
			{
				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
				if(cdle.getControllableDevice().equals(_cd))
				{
					return cdle;
				}
			}
		}
		return null;
	}

	/**
	 * returns a list of all selected devices
	 * @return a list of all selected devices
	 */
	private ArrayList<ControllableDevice> getSelectedDevices()
	{
		ArrayList<ControllableDevice> res = new ArrayList<ControllableDevice>();
//		synchronized (mAllDevices) 
		{
			for(ControllableDevice cd:mAllDevices)
			{
				if(mSelection.get(cd.getMac()))
				{
					res.add(cd);
				}
			}
		}

		return res;
	}

	/**
	 * determines which type of ControllableDevice is currently selected
	 * @return the type of selected devices
	 */
	private SelectedType getSelectedType()
	{
		ArrayList<ControllableDevice>selectedDevs = getSelectedDevices();
		if(null==selectedDevs||selectedDevs.size()==0)
		{
			return SelectedType.none;
		}
		ControllableDevice cd = selectedDevs.get(0);
		if(cd.isAlive())
		{
			return SelectedType.active;
		}
		else
		{
			return SelectedType.inactive;
		}
	}


	/**
	 * deselects all devices
	 */
	private void deselectAll()
	{
		for(IListEntry entry:mEntries)
		{
			if(entry instanceof ControllableDeviceListEntry)
			{
				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
				cdle.setSelection(false);
			}
		}

		for(ControllableDevice cd:mAllDevices)
		{
			mSelection.put(cd.getMac(), false);
		}
		notifyAdapter();

		setControlContainerVisibility(View.GONE, View.GONE);
	}

	/**
	 * shows/hides the containers for actions on multiple selected ControllableDevices
	 */
	private void setControlContainerVisibility(int _v1, int _v2)
	{
		mActiveDeviceControlContainer.setVisibility(_v1);
		
		mInactiveDeviceControlContainer.setVisibility(_v2);
	}

	/**
	 * marks the passed device as dirty (information currently not consistent ==> progress bar is shown)
	 * @param _cd the ControllableDevice to mark as dirty
	 */
	private void markDirty(final ControllableDevice _cd)
	{
		ControllableDeviceListEntry entry = getListEntryFromDevice(_cd);
		entry.setDirty(true);
		notifyAdapter();
		Log.e(TAG, "marked dirty:"+_cd.getHostname());
	}

	@Override
	public void onClick(View arg0) 
	{
		switch(arg0.getId())
		{
		case R.id.sleepButton:
			powerOffSelectedDevices(PowerOffState.sleep);
			break;
		case R.id.shutDownButton:
			Log.e(TAG, "shut down all");
			powerOffSelectedDevices(PowerOffState.shutdown);
			break;
		case R.id.wakeUpButton:
			Log.e(TAG, "wake up all");
			wakeupSelectedDevices();
			break;
		}
	}

	/**
	 * powers off or puts to sleep all currently selected devices based on the passed PowerOffState
	 * @param _state determines whether to shut down or put to sleep all selected devices 
	 */
	private void powerOffSelectedDevices(final PowerOffState _state)
	{
		{
			new Thread(new Runnable() 
			{		
				@Override
				public void run() 
				{
					ArrayList<ControllableDevice> selDevs = getSelectedDevices();
					for(ControllableDevice cd:selDevs)
					{
						markDirty(cd);
					}
					for(ControllableDevice cd:selDevs)
					{
						cd.powerOff(_state);
					}

				}
			}).start();
		}
	}
	
	/**
	 * wakes up all selected devices
	 */
	private void wakeupSelectedDevices()
	{
		mUpdateThread.pause();

		Runnable r = new Runnable() 
		{		
			@Override
			public void run() 
			{
				ArrayList<ControllableDevice> selDevs = getSelectedDevices();

				for(ControllableDevice cd:selDevs)
				{
					markDirty(cd);
				}
				for(int i = 0;i<selDevs.size();i++)
				{
					ControllableDevice cd = selDevs.get(i);
					Log.e(TAG, "woke up:"+cd.getHostname());
					Log.e(TAG, "finished device "+(i+1)+" of "+selDevs.size());
				}
				mUpdateThread.resumeAfterPause();


			}
		};
		Thread wakeupThread = new Thread(r);
		wakeupThread.start();
	}


	/**
	 * callback for the DeviceStateUpdateThread. called when new information on the devices is available
	 */
	public void notifyDataUpdated()
	{
		Log.e(TAG, "notified by update thread");
		refreshListEntries();
		notifyAdapter();
	}

	/**
	 * notifies the listadapter that the dataset has changed
	 */
	private void notifyAdapter()
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				mAdapter.notifyDataSetChanged();

			}
		});

	}

	@Override
	public void notifyError(RequestType _type, String _mac, int _code, String _msg) {
		switch(_type)
		{
		case extendedStatus:
			handleExtendedStatusError(_mac, _code, _msg);
			break;
		case getStatus:
			handleStatusError(_mac, _code, _msg);
			break;
		case poweroff:
			handlePowerOffError(_mac, _code, _msg);
			break;
		case wakeup:
			handleWakeUpError(_mac, _code, _msg);
			break;
		case unknown:

			break;
		}

	}

	/**
	 * handles errors originating from a status call
	 * @param _mac the MAC address in the request that failed
	 * @param _code the return code of the response
	 * @param _msg the message associated with the return code
	 */
	private void handleStatusError(String _mac, int _code, String _msg)
	{
		Log.e(TAG, "notified about status error. code = "+_code+", mac="+_mac);
		switch(_code)
		{
		case 404:
			removeFromList(_mac);
			toastComputerNotAvailable(_mac);
			break;
		case 500:
			addToNotAvailableList(_mac);
			toastNoInformationAvailable(_mac);
			break;
		}
	}

	/**
	 * handles errors originating from an extended status call
	 * @param _mac the MAC address in the request that failed
	 * @param _code the return code of the response
	 * @param _msg the message associated with the return code
	 */
	private void handleExtendedStatusError(String _mac, int _code, String _msg)
	{
		Log.e(TAG, "notified about extended status error. code = "+_code+", mac="+_mac);
		switch (_code) 
		{
		case 401:
			showLoginDialog(_mac);
			break;
		case 404:
			removeFromList(_mac);
			toastComputerNotAvailable(_mac);
			break;
		case 500:
			addToNotAvailableList(_mac);
			toastNoInformationAvailable(_mac);
			break;
		default:
			break;
		}

	}

	/**
	 * handles errors originating from an powerOff call
	 * @param _mac the MAC address in the request that failed
	 * @param _code the return code of the response
	 * @param _msg the message associated with the return code
	 */
	private void handlePowerOffError(String _mac, int _code, String _msg)
	{
		Log.e(TAG, "notified about power off error. code = "+_code+", mac="+_mac);
		switch (_code) {
		case 401:
			showLoginDialog(_mac);
			break;
		case 404:
			removeFromList(_mac);
			toastComputerNotAvailable(_mac);
			break;
		case 410:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "410 gone", Toast.LENGTH_LONG).show();

				}
			});
			break;

		case 400:
		case 406:
		case 500:
		case 501:
			showCantShutDownDialog(_mac);
			break;
		default:
			break;
		}
	}

	/**
	 * handles errors originating from an wakeUp call
	 * @param _mac the MAC address in the request that failed
	 * @param _code the return code of the response
	 * @param _msg the message associated with the return code
	 */
	private void handleWakeUpError(String _mac, int _code, String _msg)
	{
		Log.e(TAG, "notified about wake up error. code = "+_code+", mac="+_mac);
		switch(_code)
		{
		case 500:
			showCantWakeUpDialog(_mac);
			break;
		}
	}

	/**
	 * removes a device from the list
	 * @param _mac the mac address of the device to remove
	 */
	private void removeFromList(String _mac)
	{
		ControllableDevice cd = getDeviceFromMac(_mac);
		Log.e(TAG, "removed "+cd.toString()+", actual remove has to be implemented yet.");
	}

	/**
	 * adds a device to the list of unavailable devices
	 * @param _mac the mac address of the device to add to the list
	 */
	private void addToNotAvailableList(String _mac)
	{
		ControllableDevice cd = getDeviceFromMac(_mac);
		Log.e(TAG, "added "+cd.toString()+" to the not available list, actual add has to be implemented yet.");
	}

	/**
	 * displays a toast message saying that a computer is currently not available
	 * @param _mac the mac address of the device that is not available
	 */
	private void toastComputerNotAvailable(String _mac)
	{
		ControllableDevice cd = getDeviceFromMac(_mac);
		Toast.makeText(getApplicationContext(), cd.getHostname()+" is currently not available", Toast.LENGTH_LONG).show();
	}

	/**
	 * displays a toast message saying that currently there is no information available about a device
	 * @param _mac the mac address of the device on which no information is available
	 */
	private void toastNoInformationAvailable(String _mac)
	{
		ControllableDevice cd = getDeviceFromMac(_mac);
		Toast.makeText(getApplicationContext(), "no information about "+cd.getHostname()+" available", Toast.LENGTH_LONG).show();
	}

	/**
	 * displays the login dialog when credentials for a certain operation are necessary
	 * @param _mac the mac address of the device where credentails are needed
	 */
	private void showLoginDialog(String _mac)
	{
		ControllableDevice cd = getDeviceFromMac(_mac);
		Log.e(TAG, "showing login dialog for "+cd.getHostname());
	}

	/**
	 * displays a dialog saying that a certain computer could not be shut down
	 * @param _mac the mac address of the device that could not be shut down
	 */
	private void showCantShutDownDialog(String _mac)
	{
		showDialog(CANT_SHUTDOWN_DIALOG, getBundledHostname(_mac));
	}

	/**
	 * displays a dialog saying that a certain computer could not be woke up
	 * @param _mac the mac address of the device that could not be woke up
	 */
	private void showCantWakeUpDialog(String _mac)
	{
		showDialog(CANT_WAKEUP_DIALOG, getBundledHostname(_mac));
	}

	/**
	 * stores the hostname of a device in a new bundle. the entry is made for the key BUNDLE_HOSTNAME_KEY
	 * @param _mac the mac address of the device which's hostname should be bundled 
	 * @return a bundle containing the hostname
	 */
	private Bundle getBundledHostname(String _mac)
	{
		ControllableDevice cd = getDeviceFromMac(_mac);
		Bundle res = new Bundle();
		res.putCharSequence(BUNDLE_HOSTNAME_KEY, cd.getHostname());
		return res;
	}

	/**
	 * searches the list of ControllableDevices for the first occurrence of the passed mac address 
	 * @param _mac the mac address of the device that should be retrieved
	 * @return the ControllableDevice associated with the passed mac address
	 */
	private ControllableDevice getDeviceFromMac(String _mac)
	{
		for(ControllableDevice cd:mAllDevices)
		{
			if(cd.getMac().equals(_mac))
			{
				return cd;
			}
		}
		return null;
	}
}
