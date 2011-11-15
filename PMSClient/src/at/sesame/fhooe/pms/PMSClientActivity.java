package at.sesame.fhooe.pms;

import java.lang.Thread.UncaughtExceptionHandler;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.sesame.fhooe.lib.pms.PMSProvider;
import at.sesame.fhooe.lib.pms.model.ControllableDevice;
import at.sesame.fhooe.lib.pms.model.ControllableDevice.PowerOffState;
import at.sesame.fhooe.lib.pms.proxy.ProxyHelper;
import at.sesame.fhooe.pms.list.commands.CommandAdapter;
import at.sesame.fhooe.pms.list.commands.CommandListEntry;
import at.sesame.fhooe.pms.list.commands.CommandListEntry.CommandType;
import at.sesame.fhooe.pms.list.controllabledevice.ControllableDeviceAdapter;
import at.sesame.fhooe.pms.list.controllabledevice.ControllableDeviceListEntry;
import at.sesame.fhooe.pms.list.controllabledevice.IListEntry;
import at.sesame.fhooe.pms.list.controllabledevice.SeparatorListEntry;
import at.sesame.fhooe.pms.list.controllabledevice.SeparatorListEntry.ListType;

public class PMSClientActivity 
extends Activity 
implements OnClickListener, UncaughtExceptionHandler
{
	private static final String TAG = "FancyPMSClientActivity";

	private static final int ACTIVE_DEVICE_ACTION_DIALOG = 0;
	private static final int INACTIVE_DEVICE_ACTION_DIALOG = 1;
	private static final int NO_NETWORK_DIALOG = 2;

	private static final int IDLE_MINUTES_WARNING_THRESHOLD = 30;

	private ArrayList<ControllableDevice> mAllDevices = new ArrayList<ControllableDevice>();
	private HashMap<String, Boolean> mSelection = new HashMap<String, Boolean>();
	private ArrayList<IListEntry> mEntries = new ArrayList<IListEntry>();


	private ControllableDeviceAdapter mAdapter;

	//	private Thread mDeviceStateRefreshThread = new Thread(this);
	private DeviceStateUpdateThread mUpdateThread;
	//	private boolean mUpdating = true;
	//	private int mUpdatePeriod = 5000;

	private ControllableDevice mSelectedDevice;

	private ListView mDevList;
	private ViewGroup mActiveDeviceControlContainer;
	private ViewGroup mInactiveDeviceControlContainer;

	//	private ToggleButton mActiveToggle;
	//	private ToggleButton mInactiveToggle;

	private ImageButton mSleepAllButt;
	private ImageButton mPowerOffAllButt;
	private ImageButton mWakeUpAllButt;

	private boolean mActionPending = false;

	//	private final Object mLock = new Object();

	private enum SelectedType
	{
		active,
		inactive,
		none
	};

	//	Handler hendl = new Handler()
	//	{
	//		public void handleMessage(android.os.Message msg) 
	//		{
	//			Log.e(TAG, "handling");
	//			mUpdateThread.resumeAfterPause();
	//		};
	//	};

	/**
	 * the ProgressDialog to indicate networking
	 */
	private ProgressDialog mNetworkingDialog;

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
		queryControllableDevices();

		refreshListEntries();
		mAdapter = new ControllableDeviceAdapter(this, mEntries);
		mDevList = (ListView)findViewById(R.id.deviceList);
		mDevList.setAdapter(mAdapter);

		mActiveDeviceControlContainer = (ViewGroup)findViewById(R.id.activeDeviceControllContainer);
		mInactiveDeviceControlContainer = (ViewGroup)findViewById(R.id.inactiveDeviceControllContainer);
		setControlContainerVisibility(View.GONE, View.GONE);

		//		mActiveToggle = (ToggleButton)findViewById(R.id.activeDeviceSelection);
		//		mActiveToggle.setOnCheckedChangeListener(this);
		//
		//		mInactiveToggle = (ToggleButton)findViewById(R.id.inactiveDeviceSelection);
		//		mInactiveToggle.setOnCheckedChangeListener(this);

		mSleepAllButt = (ImageButton)findViewById(R.id.sleepButton);
		mSleepAllButt.setOnClickListener(this);

		mPowerOffAllButt = (ImageButton)findViewById(R.id.shutDownButton);
		mPowerOffAllButt.setOnClickListener(this);

		mWakeUpAllButt = (ImageButton)findViewById(R.id.wakeUpButton);
		mWakeUpAllButt.setOnClickListener(this);

		mUpdateThread = new DeviceStateUpdateThread(this, mAllDevices);
	}

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
		//		mUpdating = false;
		mUpdateThread.pause();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		//		mUpdating = false;
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
			//			mUpdateThread.resumeAfterPause();
			//			mDeviceStateRefreshThread.start();

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
		}
		return d;
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
		//		synchronized(mLock)
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
			Collections.sort(activeDevs, new ControllableDeviceComparator());
			Collections.sort(inactiveDevs, new ControllableDeviceComparator());
			mEntries.clear();
			mEntries.add(new SeparatorListEntry(getApplicationContext(), ListType.active, activeDevs.size()));
			for(ControllableDevice cd:activeDevs)
			{
				ControllableDeviceListEntry cdle = new ControllableDeviceListEntry(cd);
				cdle.setSelection(mSelection.get(cd.getHostname()));
				mEntries.add(cdle);
			}
			mEntries.add(new SeparatorListEntry(getApplicationContext(), ListType.inactive, inactiveDevs.size()));
			for(ControllableDevice cd:inactiveDevs)
			{
				ControllableDeviceListEntry cdle = new ControllableDeviceListEntry(cd);
				cdle.setSelection(mSelection.get(cd.getHostname()));
				mEntries.add(cdle);
			}
		}
	}

	private void queryControllableDevices() 
	{
		ArrayList<String> macs = PMSProvider.getDeviceList();
		mNetworkingDialog.setMax(macs.size());
		showNetworkingDialog();
		//		synchronized(mLock)
		{
			mAllDevices = new ArrayList<ControllableDevice>();
			for(int i = 0;i<macs.size();i++)
			{
				ControllableDevice cd = new ControllableDevice(macs.get(i), "admin", "pwd", true);
				mAllDevices.add(cd);
				mSelection.put(cd.getHostname(), false);
				PMSClientActivity.this.mNetworkingDialog.incrementProgressBy(1);
			}
		}
		dismissNetworkingDialog();
	}

	//	@Override
	//	public void run() 
	//	{
	//		while(mUpdating)
	//		{
	//			synchronized (mAllDevices) 
	//			{
	//				Log.e(TAG, "update started");
	//				for(ControllableDevice cd:mAllDevices)
	//				{
	//					cd.updateStatus();
	//					try {
	//						Thread.sleep(10);
	//					} catch (InterruptedException e) {
	//						// TODO Auto-generated catch block
	//						e.printStackTrace();
	//					}
	//					Log.e(TAG, cd.getHostname()+"updated...");
	//				}
	//
	//				refreshListEntries();
	//				Log.e(TAG, "list entries refreshed");
	//				notifyAdapter();
	//				Log.e(TAG, "updated");
	//
	//				try 
	//				{
	//					Thread.sleep(mUpdatePeriod);
	//				} 
	//				catch (InterruptedException e) 
	//				{
	//					e.printStackTrace();
	//				}
	//			}
	//
	//		}
	//	}

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
				res = true;
			}
			break;
		case active:
			if(cdle.getControllableDevice().isAlive())
			{
				selectDevice(cdle,_checked);
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
				res = true;
			}
			break;
		}

		if(getSelectedType()==SelectedType.none)
		{
			setControlContainerVisibility(View.GONE, View.GONE);
		}
		notifyAdapter();
		return res;
	}

	public boolean handleMultipleSelectionAttempt(ListType _type, boolean _isChecked)
	{
		Log.e(TAG, "handling multiple selection attempt...");
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
		//		if(buttonView.equals(mActiveToggle))
		//		{
		//			mInactiveToggle.setOnCheckedChangeListener(null);
		//			mInactiveToggle.setChecked(false);
		//			mInactiveToggle.setOnCheckedChangeListener(this);
		//			for(IListEntry entry:mEntries)
		//			{
		//				if(entry instanceof ControllableDeviceListEntry)
		//				{
		//					ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
		//					if(cdle.getControllableDevice().isAlive())
		//					{
		//						selectDevice(cdle);
		//					}
		//				}
		//			}
		//		}
		//		else if(buttonView.equals(mInactiveToggle))
		//		{
		//			mActiveToggle.setOnCheckedChangeListener(null);
		//			mActiveToggle.setChecked(false);
		//			mActiveToggle.setOnCheckedChangeListener(this);
		//			for(IListEntry entry:mEntries)
		//			{
		//				if(entry instanceof ControllableDeviceListEntry)
		//				{
		//					ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
		//					if(!cdle.getControllableDevice().isAlive())
		//					{
		//						selectDevice(cdle);
		//					}
		//				}
		//			}
		//		}	
	}

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
		notifyAdapter();
		return result;
	}
	

	private boolean selectDevice(ControllableDeviceListEntry _cdle, boolean _select)
	{
		_cdle.setSelection(_select);
		mSelection.put(_cdle.getControllableDevice().getHostname(), _select);
		if(_cdle.getControllableDevice().isAlive())
		{
			setControlContainerVisibility(View.VISIBLE, View.GONE);
		}
		else
		{
			setControlContainerVisibility(View.GONE, View.VISIBLE);
		}
		if(!_select)
		{
			setControlContainerVisibility(View.GONE, View.GONE);
		}
		return _cdle.isSelected();
	}

	private void toastSelectionFail()
	{
		Toast.makeText(this, "only devices from the same category (active/inactive) can be selected at the same time.", Toast.LENGTH_LONG).show();
	}

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

	private ArrayList<ControllableDevice> getSelectedDevices()
	{
		ArrayList<ControllableDevice> res = new ArrayList<ControllableDevice>();
		synchronized (mAllDevices) {
			for(ControllableDevice cd:mAllDevices)
			{
				if(mSelection.get(cd.getHostname()))
				{
					res.add(cd);
				}
			}
		}

		return res;
	}

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

	//	@Override
	//	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	//	{
	//		deselectAll();
	//		if(!isChecked)
	//		{
	//			return;
	//		}
	////		if(buttonView.equals(mActiveToggle))
	////		{
	////			mInactiveToggle.setOnCheckedChangeListener(null);
	////			mInactiveToggle.setChecked(false);
	////			mInactiveToggle.setOnCheckedChangeListener(this);
	////			for(IListEntry entry:mEntries)
	////			{
	////				if(entry instanceof ControllableDeviceListEntry)
	////				{
	////					ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
	////					if(cdle.getControllableDevice().isAlive())
	////					{
	////						selectDevice(cdle);
	////					}
	////				}
	////			}
	////		}
	////		else if(buttonView.equals(mInactiveToggle))
	////		{
	////			mActiveToggle.setOnCheckedChangeListener(null);
	////			mActiveToggle.setChecked(false);
	////			mActiveToggle.setOnCheckedChangeListener(this);
	////			for(IListEntry entry:mEntries)
	////			{
	////				if(entry instanceof ControllableDeviceListEntry)
	////				{
	////					ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
	////					if(!cdle.getControllableDevice().isAlive())
	////					{
	////						selectDevice(cdle);
	////					}
	////				}
	////			}
	////		}	
	//	}

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
			mSelection.put(cd.getHostname(), false);
		}
		notifyAdapter();

		setControlContainerVisibility(View.GONE, View.GONE);
	}

	private void setControlContainerVisibility(int _v1, int _v2)
	{
		mActiveDeviceControlContainer.setVisibility(_v1);
		mInactiveDeviceControlContainer.setVisibility(_v2);
	}

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

		//		mDeviceStateRefreshThread.interrupt();
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
			wakeupSelectedDevices2();
			break;
		}
		//		mDeviceStateRefreshThread.run();
	}

	private void powerOffSelectedDevices(final PowerOffState _state)
	{
		//		synchronized(mLock)
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
	private void wakeupSelectedDevices2()
	{
		//		ProxyHelper.releaseConnections();
		mUpdateThread.pause();
		//		setActionPending(true);
		//		try {
		//			Thread.sleep(100);
		//		} catch (InterruptedException e1) {
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//			Log.e(TAG, "sleep after updatepause interrupted");
		//		}
		Runnable r = new Runnable() 
		{		
			@Override
			public void run() 
			{
				//				try{
				ArrayList<ControllableDevice> selDevs = getSelectedDevices();
				//				ArrayList<ControllableDevice> selDevs = new ArrayList<ControllableDevice>();
				//				selDevs.add(getSelectedDevices().get(0));
				//				selDevs.add(getSelectedDevices().get(1));
				for(ControllableDevice cd:selDevs)
				{
					markDirty(cd);
				}
				for(int i = 0;i<selDevs.size();i++)
				{
					ControllableDevice cd = selDevs.get(i);
					//					cd.wakeUp();
					Log.e(TAG, "woke up:"+cd.getHostname());
					//					Thread.yield();
					//					try {
					//						Thread.sleep(10);
					//					} catch (InterruptedException e) {
					//						// TODO Auto-generated catch block
					//						e.printStackTrace();
					//					}
					Log.e(TAG, "finished device "+(i+1)+" of "+selDevs.size());
				}
				mUpdateThread.resumeAfterPause();
				//				}catch(Exception e)
				//				{
				//					Log.e(TAG, "exception in the difficult thread...");
				//					e.printStackTrace();
				//				}
				//				hendl.sendMessage(new Message());
				//				setActionPending(false);

			}
		};
		Thread wakeupThread = new Thread(r);
		//		wakeupThread.setUncaughtExceptionHandler(this);
		wakeupThread.start();
	}

	private synchronized void setActionPending(boolean _val)
	{
		mActionPending = _val;
	}

	private void wakeupSelectedDevices() 
	{
		ProxyHelper.releaseConnections();
		Log.e(TAG, "waking up all......");
		//		mUpdateThread.pause();
		//		synchronized (mLock) 
		{
			//			new Thread(new Runnable() 
			//			{		
			//				@Override
			//				public void run() 
			//				{
			Log.e(TAG, "waking up selected devices");

			//			new Thread(new Runnable() {
			//
			//				@Override
			//				public void run() {
			Log.e(TAG, "Thread for wakeup created");
			final ArrayList<ControllableDevice> selDevs = getSelectedDevices();
			for(ControllableDevice cd:selDevs)
			{
				markDirty(cd);
			}
			notifyAdapter();

			new Thread(new Runnable() 
			{	
				@Override
				public void run() 
				{
					for(final ControllableDevice cd:selDevs)
					{
						//						new Thread(new Runnable() {
						//							
						//							@Override
						//							public void run() {
						//								// TODO Auto-generated method stub
						//								markDirty(cd);
						//								notifyAdapter();
						//							}
						//						}).start();


						cd.wakeUp();
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}	
				}
			}).start();


		}
	}

	public void notifyDataUpdated()
	{
		//		if(mActionPending)
		//		{
		//			Log.e(TAG, "notify data cancled cause action is pending");
		//			return;
		//		}
		Log.e(TAG, "notified by update thread");
		refreshListEntries();
		notifyAdapter();
	}

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
	public void uncaughtException(Thread arg0, Throwable arg1) {
		Log.e(TAG, "uncaught exception:"+arg1.getMessage());

	}
}
