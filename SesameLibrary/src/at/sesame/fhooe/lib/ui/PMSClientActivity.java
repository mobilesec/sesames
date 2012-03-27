/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 11/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.sesame.fhooe.lib.R;
import at.sesame.fhooe.lib.pms.ControllableDeviceAdapter;
import at.sesame.fhooe.lib.pms.ControllableDeviceComparator;
import at.sesame.fhooe.lib.pms.ControllableDeviceListEntry;
import at.sesame.fhooe.lib.pms.DeviceStateUpdateThread;
import at.sesame.fhooe.lib.pms.IListEntry;
import at.sesame.fhooe.lib.pms.IPMSUpdateListener;
import at.sesame.fhooe.lib.pms.PMSProvider;
import at.sesame.fhooe.lib.pms.SeparatorListEntry;
import at.sesame.fhooe.lib.pms.SeparatorListEntry.ListType;
import at.sesame.fhooe.lib.pms.errorhandling.ErrorForwarder;
import at.sesame.fhooe.lib.pms.errorhandling.IErrorReceiver;
import at.sesame.fhooe.lib.pms.list.commands.CommandAdapter;
import at.sesame.fhooe.lib.pms.list.commands.CommandListEntry;
import at.sesame.fhooe.lib.pms.list.commands.CommandListEntry.CommandType;
import at.sesame.fhooe.lib.pms.model.ControllableDevice;
import at.sesame.fhooe.lib.pms.model.ControllableDevice.PowerOffState;
import at.sesame.fhooe.lib.pms.model.ExtendedPMSStatus;




/**
 * this class represents the activity that accesses the PMS and displays all
 * information provided by the PMS
 * @author Peter Riedl
 *
 */
public class PMSClientActivity 
extends Activity 
implements OnClickListener, IErrorReceiver, IPMSUpdateListener
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "PMSClientActivity";

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
	 * integer constant for displaying the dialog when a list of devices is shut down or woken up
	 */
	private static final int ACTION_IN_PROGRESS_DIALOG = 5;

	/**
	 * the key under which the hostname information is stored in the bundle for onPrepareDialog
	 */
	private static final String BUNDLE_HOSTNAME_KEY = "hostname";

	/**
	 * the key under which the message information is stored in the bundle for onPrepareDialog
	 */
	private static final String BUNDLE_MESSAGE_KEY = "message";

	/**
	 * the key under which the selected number of hosts information is stored in the bundle for onPrepareDialog
	 */
	private static final String BUNDLE_SELECTED_NUMBER_KEY = "numHosts";

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
	
	public static final String BUNDLE_USER_KEY = "user";
	public static final String BUNDLE_PASS_KEY = "pass";
	
	
	private String mUser;
	private String  mPass;


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

	/**
	 * dialog to be shown when a list of devices is shut down or woken up
	 */
	private ProgressDialog mActionInProgressDialog;

	@Override
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.pms);

		setupNetworkingDialog();
		setupActionInProgressDialog();

		if(!checkConnectivity())
		{
			showDialog(NO_NETWORK_DIALOG);

			return;
		}
		
		mUser = getIntent().getExtras().getString(BUNDLE_USER_KEY);
		mPass = getIntent().getExtras().getString(BUNDLE_PASS_KEY);
		
		ErrorForwarder.getInstance().register(this);
		queryControllableDevicesSim();
//		queryControllableDevicesKDF();
//		queryControllableDevicesTest(65);
				
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

	}

	private void startAutoUpdate()
	{
		mUpdateThread = new DeviceStateUpdateThread(this, mAllDevices, mUser, mPass);
		mUpdateThread.start();
	}

	private void stopAutoUpdate()
	{
		if(null!=mUpdateThread)
		{
			mUpdateThread.stopUpdating();
		}

	}

	private void testExtendedStatusList()
	{

		ArrayList<String>hosts = new ArrayList<String>();
		//		hosts.
		hosts.add("00:25:b3:16:ad:14");
		hosts.add("00:25:b3:17:df:1d");
		hosts.add("00:25:b3:16:ac:1a");
		hosts.add("00:25:b3:17:e2:93");
		hosts.add("00:25:b3:16:ac:ad");
		hosts.add("00:25:b3:16:ab:f0");
		hosts.add("00:25:b3:16:ad:17");
		hosts.add("00:25:b3:16:ac:17");
		hosts.add("00:25:b3:16:ac:40");
		hosts.add("00:25:b3:17:e2:61");
		hosts.add("00:25:b3:17:e2:94");
		hosts.add("00:25:b3:16:ab:9e");
		hosts.add("00:25:b3:17:e2:62");
		hosts.add("00:25:b3:17:e1:e8");
		hosts.add("00:25:b3:16:ac:65");
		hosts.add("00:25:b3:17:df:24");
		hosts.add("00:25:b3:17:e2:31");
		hosts.add("00:25:b3:17:e2:f6");

		hosts.add("00:22:64:16:9d:84");
		hosts.add("00:22:64:15:e9:be");
		hosts.add("00:22:64:15:2a:46");
		hosts.add("00:22:64:17:13:90");
		hosts.add("00:22:64:15:2a:38");
		hosts.add("00:22:64:16:9d:80");
		hosts.add("00:22:64:15:e6:6e");
		hosts.add("00:22:64:15:66:14");
		hosts.add("00:22:64:15:2a:30");
		hosts.add("00:22:64:15:a6:c2");
		hosts.add("00:22:64:14:f2:34");
		hosts.add("00:22:64:15:a6:b2");
		hosts.add("00:22:64:15:29:ca");
		hosts.add("00:22:64:16:9d:12");
		hosts.add("00:22:64:16:20:ac");
		hosts.add("00:22:64:16:9d:7e");
		hosts.add("00:22:64:17:15:ca");
		hosts.add("00:22:64:15:a6:56");
		hosts.add("00:22:64:15:a6:5a");

		hosts.add("00:22:64:16:20:9c");
		hosts.add("00:22:64:16:9d:fa");
		hosts.add("00:22:64:15:a9:04");
		hosts.add("00:22:64:17:13:9e");
		hosts.add("00:22:64:15:63:76");
		hosts.add("00:22:64:15:e9:d0");
		hosts.add("00:22:64:15:e8:08");
		hosts.add("00:22:64:15:a6:ba");
		hosts.add("00:22:64:15:67:86");
		hosts.add("00:22:64:14:b3:bf");
		hosts.add("00:22:64:17:13:a4");
		hosts.add("00:22:64:16:a3:3a");
		hosts.add("00:22:64:15:e7:28");
		hosts.add("00:22:64:15:e8:d8");
		hosts.add("00:22:64:14:b0:ed");
		hosts.add("00:22:64:14:f5:96");
		hosts.add("00:22:64:14:b3:bd");
		hosts.add("00:22:64:16:9d:2c");
		hosts.add("00:22:64:15:23:d4");
		ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getPMS(mUser, mPass).extendedStatusList(hosts);
		for(ExtendedPMSStatus epmss:statuses)
		{
			Log.e(TAG, epmss.toString());
		}
		//		
		//		JSONArray arr = new JSONArray(hosts);
		////		arr.
		//		JSONArray names = new JSONArray();
		////		JSONObject
		//		StringBuilder sb = new StringBuilder();
		//		sb.append("[");
		//		for(int i =0 ;i<hosts.size()-1;i++)
		//		{
		//			JSONObject json = new JSONObject();
		//			try {
		//				json.put("mac", hosts.get(i));
		//				sb.append(json.toString());
		//				sb.append(",");
		//			} catch (JSONException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		////			arr.put(json);
		//		}
		//		JSONObject lastObject = new JSONObject();
		//		try {
		//			lastObject.put("mac", hosts.get(hosts.size()-1));
		//		} catch (JSONException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		sb.append(lastObject.toString());
		//		sb.append("]");
		//		Log.e(TAG, sb.toString());
		////		JSONArray arr2  = new JSONArray();
		////		for(int i =0;i<arr.length();i++)
		////		{
		////			arr2.put("mac");
		////		}
		////		
		//////		arr.toJSONObject(names)
		////		try {
		////			Log.e(TAG, arr.toJSONObject(arr2).toString());
		////		} catch (JSONException e) {
		////			// TODO Auto-generated catch block
		////			e.printStackTrace();
		////		}
		//		
		////		 [{ "mac" : "1" }, { "mac" : "2" }, { "mac" : "3" } ]

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
		stopAutoUpdate();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		stopAutoUpdate();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.e(TAG, "onResume");
		if(checkConnectivity())
		{
			startAutoUpdate();
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
		case ACTION_IN_PROGRESS_DIALOG:
			ProgressDialog pd = (ProgressDialog) dialog;
			pd.setMessage(args.getString(BUNDLE_MESSAGE_KEY));
			pd.setMax(args.getInt(BUNDLE_SELECTED_NUMBER_KEY));
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
//		mNetworkingDialog.setCancelable(false);
		mNetworkingDialog.setCanceledOnTouchOutside(false);
		//		mNetworkingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mNetworkingDialog.setIndeterminate(true);
	}

	private void setupActionInProgressDialog()
	{
		mActionInProgressDialog = new ProgressDialog(PMSClientActivity.this);
		//		mActionInProgressDialog.setMessage(getString(R.string.PMSClientActivity_networkingProgressDialogTitle));
		mActionInProgressDialog.setCancelable(false);
		mActionInProgressDialog.setCanceledOnTouchOutside(false);
		mActionInProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
//		if(mNetworkingDialog.isShowing())
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
				if(null!=mSelection)
				{
					cdle.setSelection(mSelection.get(cd.getMac()));
					
				}
				mEntries.add(cdle);
			}
		}
	}
	
	private void queryControllableDevicesSim() 
	{
		Log.i(TAG, "SIMULATION");
		//		ArrayList<String> macs = PMSProvider.getDeviceList();
		HashMap<String,String>hosts = new HashMap<String, String>();
		hosts.put("00:24:81:1C:3D:90", "Topf");
		hosts.put("00:21:5A:17:40:CE", "dangl");
		

//		String clients = PMSProvider.getPMS().getClients();
//		Log.e(TAG, clients);

		mNetworkingDialog.setMax(hosts.size());
		showNetworkingDialog();
		//		String[] macStrings = new String[hosts.size()];
		ArrayList<String> macs = new ArrayList<String>(hosts.keySet());
		ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getPMS(mUser, mPass).extendedStatusList(macs);
		if(null==statuses)
		{
			Log.e(TAG, "could not query statuses");
			dismissNetworkingDialog();
			return;
		}
		mAllDevices = new ArrayList<ControllableDevice>();
		for(ExtendedPMSStatus exStat:statuses)
		{
			Log.e(TAG, "passed mac="+exStat.getMac());
			String passedHost = hosts.get(exStat.getMac());
			Log.e(TAG, "HOST in LIST="+passedHost);
			ControllableDevice cd = new ControllableDevice(getApplicationContext(), exStat, passedHost, "schule\\\\sesame_pms", " my_sesame_pms", true);
			//			ControllableDevice cd = new ControllableDevice(getApplicationContext(), host.getValue(), host.getKey(), "schule\\sesame_pms", " my_sesame_pms", true);
			if(cd.isValid())
			{
				mAllDevices.add(cd);
				mSelection.put(cd.getMac(), false);
			}
			//			PMSClientActivity.this.mNetworkingDialog.incrementProgressBy(1);
		}

		//			for(int i = 0;i<macs.size();i++)
		//			{
		//				ControllableDevice cd = new ControllableDevice(getApplicationContext(), macs.get(i), "admin1", "pwd", true);
		//				if(cd.isValid())
		//				{
		//					mAllDevices.add(cd);
		//					mSelection.put(cd.getMac(), false);
		//				}
		//				PMSClientActivity.this.mNetworkingDialog.incrementProgressBy(1);
		//			}

		dismissNetworkingDialog();
	}

	/**
	 * queries which computers can be controlled via PMS and adds their ControllableDevice representations to the list
	 * of all devices
	 */
	private void queryControllableDevicesKDF() 
	{
		//		ArrayList<String> macs = PMSProvider.getDeviceList();
		HashMap<String,String>hosts = new HashMap<String, String>();
		hosts.put("00:25:b3:16:ad:14", "DV101");
		hosts.put("00:25:b3:17:df:1d", "DV102");
		hosts.put("00:25:b3:16:ac:1a", "DV103");
		hosts.put("00:25:b3:17:e2:93", "DV104");
		hosts.put("00:25:b3:16:ac:ad", "DV105");
		hosts.put("00:25:b3:16:ab:f0", "DV106");
		hosts.put("00:25:b3:16:ad:17", "DV107");
		hosts.put("00:25:b3:16:ac:17", "DV108");
		hosts.put("00:25:b3:16:ac:40", "DV109");
		hosts.put("00:25:b3:17:e2:61", "DV110");
		hosts.put("00:25:b3:17:e2:94", "DV111");
		hosts.put("00:25:b3:16:ab:9e", "DV112");
		hosts.put("00:25:b3:17:e2:62", "DV113");
		hosts.put("00:25:b3:17:e1:e8", "DV114");
		hosts.put("00:25:b3:16:ac:65", "DV115");
		hosts.put("00:25:b3:17:df:24", "DV116");
		hosts.put("00:25:b3:17:e2:31", "DV118");
		hosts.put("00:25:b3:17:e2:f6", "DV119");

		hosts.put("00:22:64:16:9d:84", "DV301");
		hosts.put("00:22:64:15:e9:be", "DV302");
		hosts.put("00:22:64:15:2a:46", "DV303");
		hosts.put("00:22:64:17:13:90", "DV304");
		hosts.put("00:22:64:15:2a:38", "DV305");
		hosts.put("00:22:64:16:9d:80", "DV306");
		hosts.put("00:22:64:15:e6:6e", "DV307");
		hosts.put("00:22:64:15:66:14", "DV308");
		hosts.put("00:22:64:15:2a:30", "DV309");
		hosts.put("00:22:64:15:a6:c2", "DV310");
		hosts.put("00:22:64:14:f2:34", "DV311");
		hosts.put("00:22:64:15:a6:b2", "DV312");
		hosts.put("00:22:64:15:29:ca", "DV313");
		hosts.put("00:22:64:16:9d:12", "DV314");
		hosts.put("00:22:64:16:20:ac", "DV315");
		hosts.put("00:22:64:16:9d:7e", "DV316");
		hosts.put("00:22:64:17:15:ca", "DV317");
		hosts.put("00:22:64:15:a6:56", "DV318");
		hosts.put("00:22:64:15:a6:5a", "DV319");

		hosts.put("00:22:64:16:20:9c", "DV601");
		hosts.put("00:22:64:16:9d:fa", "DV602");
		hosts.put("00:22:64:15:a9:04", "DV603");
		hosts.put("00:22:64:17:13:9e", "DV604");
		hosts.put("00:22:64:15:63:76", "DV605");
		hosts.put("00:22:64:15:e9:d0", "DV606");
		hosts.put("00:22:64:15:e8:08", "DV607");
		hosts.put("00:22:64:15:a6:ba", "DV608");
		hosts.put("00:22:64:15:67:86", "DV609");
		hosts.put("00:22:64:14:b3:bf", "DV610");
		hosts.put("00:22:64:17:13:a4", "DV611");
		hosts.put("00:22:64:16:a3:3a", "DV612");
		hosts.put("00:22:64:15:e7:28", "DV613");
		hosts.put("00:22:64:15:e8:d8", "DV614");
		hosts.put("00:22:64:14:b0:ed", "DV615");
		hosts.put("00:22:64:14:f5:96", "DV616");
		hosts.put("00:22:64:14:b3:bd", "DV617");
		hosts.put("00:22:64:16:9d:2c", "DV618");
		hosts.put("00:22:64:15:23:d4", "DV619");

//		String clients = PMSProvider.getPMS().getClients();
//		Log.e(TAG, clients);

		mNetworkingDialog.setMax(hosts.size());
		showNetworkingDialog();
		//		String[] macStrings = new String[hosts.size()];
		ArrayList<String> macs = new ArrayList<String>(hosts.keySet());
		ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getPMS(mUser, mPass).extendedStatusList(macs);
		if(null==statuses)
		{
			Log.e(TAG, "could not query statuses");
			dismissNetworkingDialog();
			return;
		}
		mAllDevices = new ArrayList<ControllableDevice>();
		for(ExtendedPMSStatus exStat:statuses)
		{
			Log.e(TAG, "passed mac="+exStat.getMac());
			String passedHost = hosts.get(exStat.getMac());
			Log.e(TAG, "HOST in LIST="+passedHost);
			ControllableDevice cd = new ControllableDevice(getApplicationContext(), exStat, passedHost, "schule\\\\sesame_pms", " my_sesame_pms", true);
			//			ControllableDevice cd = new ControllableDevice(getApplicationContext(), host.getValue(), host.getKey(), "schule\\sesame_pms", " my_sesame_pms", true);
			if(cd.isValid())
			{
				mAllDevices.add(cd);
				mSelection.put(cd.getMac(), false);
			}
			//			PMSClientActivity.this.mNetworkingDialog.incrementProgressBy(1);
		}

		//			for(int i = 0;i<macs.size();i++)
		//			{
		//				ControllableDevice cd = new ControllableDevice(getApplicationContext(), macs.get(i), "admin1", "pwd", true);
		//				if(cd.isValid())
		//				{
		//					mAllDevices.add(cd);
		//					mSelection.put(cd.getMac(), false);
		//				}
		//				PMSClientActivity.this.mNetworkingDialog.incrementProgressBy(1);
		//			}

		dismissNetworkingDialog();
	}

	private void queryControllableDevicesTest(int numDummyEntries)
	{
		showNetworkingDialog();
//		ArrayList<String> macs = PMSProvider.getDeviceList();
		ArrayList<String>macs = new ArrayList<String>(numDummyEntries);
		for(int i = 0;i<numDummyEntries;i++)
		{
			macs.add("invalid_mac_"+i);
		}
//		
//		JSONObject buffer = new JSONObject();
//		for(int i = 0;i<macs.size();i++)
//		{
//			try {
//				buffer.put("mac", macs.get(i));
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			ControllableDevice cd = new ControllableDevice(getApplication(), macs.get(i), null/*"host "+i*/, "schule\\\\sesame_pms", " my_sesame_pms", true);
//			//			if(cd.isValid())
//			{
//				mAllDevices.add(cd);
//				Log.e(TAG, "controllable device added..."+mAllDevices.size());
//				mSelection.put(cd.getMac(), false);
//			}
//			//			PMSClientActivity.this.mNetworkingDialog.incrementProgressBy(1);
//		}
//		ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getPMS().extendedStatusList(macs);
//		if(null==statuses)
//		{
//			Log.e(TAG, "retrieving device information failed...");
//			return;
//		}
//		for(int i = 0;i<statuses.size();i++)
//		{
//			ControllableDevice cd = new ControllableDevice(getApplicationContext(), statuses.get(i), null, "schule\\\\sesame_pms", " my_sesame_pms", true);
//			mAllDevices.add(cd);
//			mSelection.put(cd.getMac(), false);
//		}
		
		for(int i =0;i<macs.size();i++)
		{
			ExtendedPMSStatus status = new ExtendedPMSStatus();
			status.setMac(macs.get(i));
			status.setHostname("pc"+i);
			status.setIp("127.0.0.1");
			status.setOs(ControllableDevice.OS.unknown.name());
			status.setAlive("0");
			mAllDevices.add(new ControllableDevice(getApplicationContext(), status, status.getHostname(), "schule\\\\sesame_pms", " my_sesame_pms", true));
			mSelection.put(macs.get(i), false);
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
		runOnUiThread(new Runnable() {

			@Override
			public void run() 
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
		});

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
		if (arg0.getId() == R.id.sleepButton) {
			powerOffSelectedDevices(PowerOffState.sleep);
		} else if (arg0.getId() == R.id.shutDownButton) {
			Log.e(TAG, "shut down all");
			powerOffSelectedDevices(PowerOffState.shutdown);
		} else if (arg0.getId() == R.id.wakeUpButton) {
			Log.e(TAG, "wake up all");
			wakeupSelectedDevices();
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
					//					mUpdateThread.pause();
					//					stopAutoUpdate();
					//					try {
					//						Thread.sleep(16000);
					//					} catch (InterruptedException e1) {
					//						// TODO Auto-generated catch block
					//						e1.printStackTrace();
					//					}
					ArrayList<ControllableDevice> selDevs = getSelectedDevices();
					for(ControllableDevice cd:selDevs)
					{
						markDirty(cd);
						cd.powerOff(_state);
						Log.e(TAG, "should not come too often ");
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					deselectAll();
					//					startAutoUpdate();
					//					mUpdateThread.resumeAfterPause();
					//					for(ControllableDevice cd:selDevs)
					//					{
					//						
					//					}

				}
			}).start();
		}
	}

	/**
	 * wakes up all selected devices
	 */
	private void wakeupSelectedDevices()
	{
		//		mUpdateThread.pause();
		//
		//		Runnable r = new Runnable() 
		//		{		
		//			@Override
		//			public void run() 
		//			{
		//				ArrayList<ControllableDevice> selDevs = getSelectedDevices();
		//
		//				for(ControllableDevice cd:selDevs)
		//				{
		//					markDirty(cd);
		//				}
		//				for(int i = 0;i<selDevs.size();i++)
		//				{
		//					ControllableDevice cd = selDevs.get(i);
		////					Looper.prepare();
		//					cd.wakeUp();
		////					Looper.loop();
		//					Log.e(TAG, "woke up:"+cd.getHostname());
		//					Log.e(TAG, "finished device "+(i+1)+" of "+selDevs.size());
		//				}
		//				mUpdateThread.resumeAfterPause();
		//
		//
		//			}
		//		};
		//		Thread wakeupThread = new Thread(r);
		//		wakeupThread.start();

		//		final ArrayList<ControllableDevice> selDevs = getSelectedDevices();
		//		for(int i = 0 ;i<selDevs.size();i++)
		//		{
		//			final int idx = i;
		//			Runnable wakeUpRunnable = new Runnable() 
		//			{	
		//				@Override
		//				public void run() {
		//					markDirty(selDevs.get(idx));
		//					boolean res = selDevs.get(idx).wakeUp();
		//					Log.e(TAG, "wakeup of "+selDevs.get(idx).getHostname()+":"+res);
		//				}
		//			};
		//			
		//			new Thread(wakeUpRunnable).start();
		//			try {
		//				Thread.sleep(10);
		//			} catch (InterruptedException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//			
		//		}

		Runnable wakeupRunnable = new Runnable() {

			@Override
			public void run() 
			{
				showActionInProgressDialog("wake up");
				
				stopAutoUpdate();
				ArrayList<ControllableDevice> selDevs = getSelectedDevices();
				for(int i = 0;i<selDevs.size();i++)
				{
					ControllableDevice cd = selDevs.get(i);
					markDirty(cd);
					cd.wakeUp();
					mActionInProgressDialog.incrementProgressBy(1);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				deselectAll();
				startAutoUpdate();
				dismissActionInProgressDialog();
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
////						dismissDialog(ACTION_IN_PROGRESS_DIALOG);
//						mActionInProgressDialog.setProgress(0);
//						mActionInProgressDialog.dismiss();
//						
//					}
//				});
				
				//				mUpdateThread.resumeAfterPause();
			}
		};
		new Thread(wakeupRunnable).start();
	}
	
	private void showActionInProgressDialog(final String _msg)
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() 
			{
				setupActionInProgressDialog();
				mActionInProgressDialog.setTitle(_msg);
				mActionInProgressDialog.setMax(getSelectedDevices().size());
				mActionInProgressDialog.show();
				
			}
		});
	}
	
	private void dismissActionInProgressDialog()
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mActionInProgressDialog.dismiss();
				
			}
		});
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

	private Bundle createMessageAndMaxBundle(String _msg, int _max)
	{
		Bundle res = new Bundle();
		res.putString(BUNDLE_MESSAGE_KEY, _msg);
		res.putInt(BUNDLE_SELECTED_NUMBER_KEY, _max);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.pms_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		finish();
//		startActivity(new Intent(this, SesamePhoneAppActivity.class));
		return true;
	}

	@Override
	public void notifyPMSUpdated() {
		Log.e(TAG, "notified by update thread");
		refreshListEntries();
		notifyAdapter();
		
	}

	
	
	
}
