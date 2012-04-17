/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 11/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.tablet;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.sesame.fhooe.lib2.R;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.pms.ControllableDeviceAdapter;
import at.sesame.fhooe.lib2.pms.ControllableDeviceListEntry;
import at.sesame.fhooe.lib2.pms.IListEntry;
import at.sesame.fhooe.lib2.pms.IPMSUpdateListener;
import at.sesame.fhooe.lib2.pms.PmsHelper;
import at.sesame.fhooe.lib2.pms.PmsUiInfo;
import at.sesame.fhooe.lib2.pms.SeparatorListEntry;
import at.sesame.fhooe.lib2.pms.SeparatorListEntry.ListType;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory.DialogType;
import at.sesame.fhooe.lib2.pms.dialogs.PMSNoNetworkDialogFragment;
import at.sesame.fhooe.lib2.pms.errorhandling.ErrorForwarder;
import at.sesame.fhooe.lib2.pms.errorhandling.IErrorReceiver;
import at.sesame.fhooe.lib2.pms.hosts.HostList;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;




/**
 * this class represents the activity that accesses the PMS and displays all
 * information provided by the PMS
 * @author Peter Riedl
 *
 */
public class PMSClientFragment 
extends DialogFragment 
implements OnClickListener, OnCheckedChangeListener, IErrorReceiver, IPMSUpdateListener
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "PMSClientFragment";

	private static final String ACTIVE_CB_TAG = "active";
	private static final String INACTIVE_CB_TAG = "inactive";

	private static final long UI_UPDATE_PERIOD = 10000;

	private Timer mUiUpdateTimer = new Timer();

	/**
	 * list of all displayed list entries
	 */
	//	private ArrayList<IListEntry> mEntries = new ArrayList<IListEntry>();

	//	/**
	//	 * integer constant for displaying the dialog for actions on active devices
	//	 */
	//	private static final int ACTIVE_DEVICE_ACTION_DIALOG = 0;
	//
	//	/**
	//	 * integer constant for displaying the dialog for actions on inactive devices
	//	 */
	//	private static final int INACTIVE_DEVICE_ACTION_DIALOG = 1;
	//
	//	/**
	//	 * integer constant for displaying the dialog when no internet connection is detected
	//	 */
	//	private static final int NO_NETWORK_DIALOG = 2;
	//
	//	/**
	//	 * integer constant for displaying the dialog when the shutdown of a computer failed
	//	 */
	//	private static final int CANT_SHUTDOWN_DIALOG = 3;
	//
	//	/**
	//	 * integer constant for displaying the dialog when the wake up of a computer failed
	//	 */
	//	private static final int CANT_WAKEUP_DIALOG = 4;
	//
	//	/**
	//	 * integer constant for displaying the dialog when a list of devices is shut down or woken up
	//	 */
	//	private static final int ACTION_IN_PROGRESS_DIALOG = 5;

	//	/**
	//	 * the key under which the hostname information is stored in the bundle for onPrepareDialog
	//	 */
	//	private static final String BUNDLE_HOSTNAME_KEY = "hostname";
	//
	//	/**
	//	 * the key under which the message information is stored in the bundle for onPrepareDialog
	//	 */
	//	private static final String BUNDLE_MESSAGE_KEY = "message";
	//
	//	/**
	//	 * the key under which the selected number of hosts information is stored in the bundle for onPrepareDialog
	//	 */
	//	private static final String BUNDLE_SELECTED_NUMBER_KEY = "numHosts";

	//	private PMSController mPmsController;

	//	/**
	//	 * a list of all controllable devices available
	//	 */
	//	private ArrayList<ControllableDevice> mAllDevices = new ArrayList<ControllableDevice>();





	/**
	 * the adapter used for displaying the list entries
	 */
	//	private ControllableDeviceAdapter mAdapter;





	/**
	 * the listview for all ControllableDevices
	 */
	//	private ListView mDevList;



	private ArrayList<IListEntry> mActiveListEntries = new ArrayList<IListEntry>();

	private ArrayList<IListEntry> mInactiveListEntries = new ArrayList<IListEntry>();

	private ListView mActiveList;
	private ListView mInactiveList;

	private ControllableDeviceAdapter mActiveAdapter;
	private ControllableDeviceAdapter mInactiveAdapter;

	private TextView mActiveDevIndicatorLabel;
	private TextView mInactiveDevIndicatorLabel;

	private CheckBox mSelectAllActiveDevCb;
	private CheckBox mSelectAllInactiveDevCb;

	//	private ControllableDeviceAdapter mActiveAdapter;
	//	private ControllableDeviceAdapter mInactiveAdapter;
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
	 * the ProgressDialog to indicate networking
	 */
	//	private PMSNetworkingInProgressDialogFragment mNetworkingDialog;

	/**
	 * dialog to be shown when a list of devices is shut down or woken up
	 */
	//	private PMSActionInProgressDialogFragment mActionInProgressDialog;

	private PmsHelper mPMSHelper;
//	private FragmentManager mFragMan;
	private Context mCtx;
//	private HostList mHostList;
	private Handler mUiHandler;
	private String mTitle;
	public PMSClientFragment(Context _ctx, FragmentManager _fragMan, Handler _handler, String _title, HostList _hosts2Load)
	{
		mCtx = _ctx;
//		mFragMan = _fragMan;
//		mHostList = _hosts2Load;
		mUiHandler = _handler;
		mTitle = _title;
		//		mNetworkingDialog = new PMSNetworkingInProgressDialogFragment(_ctx);
//		ErrorForwarder.getInstance().register(this);
		
		//		queryControllableDevicesKDF();
		//		queryControllableDevicesTest(50);
	}

	private void initializeLists()
	{
		
		mActiveAdapter = new ControllableDeviceAdapter(mCtx, mActiveListEntries, mPMSHelper);
		mActiveList.setAdapter(mActiveAdapter);

		mInactiveAdapter = new ControllableDeviceAdapter(mCtx, mInactiveListEntries, mPMSHelper);
		mInactiveList.setAdapter(mInactiveAdapter);
//		startSingleUiUpdate();
		startContinuousUiUpdates();
	}



	//	@Override
	//	public void onActivityCreated(Bundle arg0) 
	//	{
	//		super.onActivityCreated(arg0);
	//		mPmsController = new PMSController(getActivity(), this, getActivity().getSupportFragmentManager());
	//	}




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(mTitle);
		View v = inflater.inflate(R.layout.pms_2_colums, null);

		ViewGroup activeDeviceControlContainer = (ViewGroup)v.findViewById(R.id.activeDeviceControllContainer);
		ViewGroup inactiveDeviceControlContainer = (ViewGroup)v.findViewById(R.id.inactiveDeviceControllContainer);

		mPMSHelper = new PmsHelper(mCtx, getFragmentManager(), this, mTitle, activeDeviceControlContainer, inactiveDeviceControlContainer);

		mActiveList = (ListView)v.findViewById(R.id.activeDeviceList);
		mInactiveList = (ListView)v.findViewById(R.id.inactiveDeviceList);
		initializeLists();

		//		mAdapter = new ControllableDeviceAdapter(mCtx, this, mEntries, mPMSHelper);
		//		mDevList = (ListView)v.findViewById(R.id.deviceList);
		//		mDevList.setAdapter(mAdapter);

		mSleepAllButt = (Button)v.findViewById(R.id.sleepButton);
		mSleepAllButt.setOnClickListener(this);

		mPowerOffAllButt = (Button)v.findViewById(R.id.shutDownButton);
		mPowerOffAllButt.setOnClickListener(this);

		mWakeUpAllButt = (Button)v.findViewById(R.id.wakeUpButton);
		mWakeUpAllButt.setOnClickListener(this);

		View activeIndicator = v.findViewById(R.id.activeDeviceIndicator);
		mActiveDevIndicatorLabel = (TextView)activeIndicator.findViewById(R.id.separatorNameLabel);
		mSelectAllActiveDevCb = (CheckBox)activeIndicator.findViewById(R.id.separatorCheckBox);
		mSelectAllActiveDevCb.setTag(ACTIVE_CB_TAG);
		mSelectAllActiveDevCb.setOnCheckedChangeListener(this);

		View inactiveIndicator = v.findViewById(R.id.inactiveDeviceIndicator);
		mInactiveDevIndicatorLabel = (TextView)inactiveIndicator.findViewById(R.id.separatorNameLabel);
		mSelectAllInactiveDevCb = (CheckBox)inactiveIndicator.findViewById(R.id.separatorCheckBox);
		mSelectAllInactiveDevCb.setTag(INACTIVE_CB_TAG);
		mSelectAllInactiveDevCb.setOnCheckedChangeListener(this);
		return v;
	}
	
	

	//	private void refreshListEntries() 
	//	{
	//
	//
	//		ArrayList<ControllableDevice> activeDevs = new ArrayList<ControllableDevice>();
	//		ArrayList<ControllableDevice> inactiveDevs = new ArrayList<ControllableDevice>();
	////		mUiHelper.createListEntries(mPmsController.getAllDevices());
	////		Log.e(TAG, "size of all devs:"+mPmsController.getAllDevices().size());
	//		for(ControllableDevice cd:mPmsController.getAllDevices())
	//		{
	//			if(cd.isAlive())
	//			{
	//				activeDevs.add(cd);
	//			}
	//			else
	//			{
	//				inactiveDevs.add(cd);
	//			}
	//		}
	//		if(!activeDevs.isEmpty())
	//		{
	//			Collections.sort(activeDevs, new ControllableDeviceComparator());
	//		}
	//
	//		if(!inactiveDevs.isEmpty())
	//		{
	//			Collections.sort(inactiveDevs, new ControllableDeviceComparator());
	//		}
	////		Log.e(TAG, "active:"+activeDevs.size()+", inactive:"+inactiveDevs.size());
	//		mEntries.clear();
	//		mEntries.add(new SeparatorListEntry(mCtx, ListType.active, activeDevs.size()));
	//
	////		HashMap<String, Boolean> selection = mUiHelper.getSelectionMap(mPmsController.getAllDevices());
	////		System.out.println("selection:"+selection.size());
	//		for(ControllableDevice cd:activeDevs)
	//		{
	////			System.out.println("adding entry for:"+cd.toString());
	//			ControllableDeviceListEntry cdle = new ControllableDeviceListEntry(cd);
	//			cdle.setSelection(mPMSHelper.isDeviceSelected(cd));
	//			mEntries.add(cdle);
	//		}
	//		mEntries.add(new SeparatorListEntry(mCtx, ListType.inactive, inactiveDevs.size()));
	//		for(ControllableDevice cd:inactiveDevs)
	//		{
	////			Log.e(TAG, "adding entry for:"+cd.toString());
	//			ControllableDeviceListEntry cdle = new ControllableDeviceListEntry(cd);
	////			if(null!=selection)
	//			{
	////				Boolean rawSelection = selection.get(cd.getMac());
	////				boolean selected = rawSelection==null?false:rawSelection;
	//				cdle.setSelection(mPMSHelper.isDeviceSelected(cd));
	//
	//			}
	//			mEntries.add(cdle);
	//		}	
	//		
	//		notifyAdapter();
	////		PMSDialogFactory.dismissCurrentDialog();
	//	}

	/**
	 * parses the list of all devices, divides them into devices that are alive and those
	 * that are not and sorts the alive devices by idle time
	 */
	//	private void refreshListEntries_old() 
	//	{
	//
	//		{
	//			ArrayList<ControllableDevice> activeDevs = new ArrayList<ControllableDevice>();
	//			ArrayList<ControllableDevice> inactiveDevs = new ArrayList<ControllableDevice>();
	////			mUiHelper.createListEntries(mPmsController.getAllDevices());
	//			for(ControllableDevice cd:mPmsController.getAllDevices())
	//			{
	//				if(cd.isAlive())
	//				{
	//					activeDevs.add(cd);
	//				}
	//				else
	//				{
	//					inactiveDevs.add(cd);
	//				}
	//			}
	//			if(!activeDevs.isEmpty())
	//			{
	//				Collections.sort(activeDevs, new ControllableDeviceComparator());
	//			}
	//
	//			if(!inactiveDevs.isEmpty())
	//			{
	//				Collections.sort(inactiveDevs, new ControllableDeviceComparator());
	//			}
	//			mEntries.clear();
	//			mEntries.add(new SeparatorListEntry(mCtx, ListType.active, activeDevs.size()));
	////			HashMap<String, Boolean> selection = mUiHelper.getSelectionMap(mPmsController.getAllDevices());
	//			
	//			for(ControllableDevice cd:activeDevs)
	//			{
	//				ControllableDeviceListEntry cdle = new ControllableDeviceListEntry(cd);
	//				cdle.setSelection(mPMSHelper.isDeviceSelected(cd));
	//				mEntries.add(cdle);
	//			}
	//			mEntries.add(new SeparatorListEntry(mCtx, ListType.inactive, inactiveDevs.size()));
	//			for(ControllableDevice cd:inactiveDevs)
	//			{
	//				ControllableDeviceListEntry cdle = new ControllableDeviceListEntry(cd);
	////				if(null!=selection)
	//				{
	//					cdle.setSelection(mPMSHelper.isDeviceSelected(cd));
	//
	//				}
	//				mEntries.add(cdle);
	//			}
	//		}
	//	}

	//	@Override
	//	public void onResume()
	//	{
	////		setContentView(R.layout.pms);
	//
	////		setupNetworkingDialog();
	//		setupActionInProgressDialog();
	//
	//		if(!checkConnectivity())
	//		{
	//			showDialog(NO_NETWORK_DIALOG);
	//
	//			return;
	//		}
	//		
	////		queryControllableDevicesTest(65);
	//				
	//		refreshListEntries();
	//		
	//
	//	}


	//
	//	private void testExtendedStatusList()
	//	{
	//
	//		ArrayList<String>hosts = new ArrayList<String>();
	//		//		hosts.
	//		hosts.add("00:25:b3:16:ad:14");
	//		hosts.add("00:25:b3:17:df:1d");
	//		hosts.add("00:25:b3:16:ac:1a");
	//		hosts.add("00:25:b3:17:e2:93");
	//		hosts.add("00:25:b3:16:ac:ad");
	//		hosts.add("00:25:b3:16:ab:f0");
	//		hosts.add("00:25:b3:16:ad:17");
	//		hosts.add("00:25:b3:16:ac:17");
	//		hosts.add("00:25:b3:16:ac:40");
	//		hosts.add("00:25:b3:17:e2:61");
	//		hosts.add("00:25:b3:17:e2:94");
	//		hosts.add("00:25:b3:16:ab:9e");
	//		hosts.add("00:25:b3:17:e2:62");
	//		hosts.add("00:25:b3:17:e1:e8");
	//		hosts.add("00:25:b3:16:ac:65");
	//		hosts.add("00:25:b3:17:df:24");
	//		hosts.add("00:25:b3:17:e2:31");
	//		hosts.add("00:25:b3:17:e2:f6");
	//
	//		hosts.add("00:22:64:16:9d:84");
	//		hosts.add("00:22:64:15:e9:be");
	//		hosts.add("00:22:64:15:2a:46");
	//		hosts.add("00:22:64:17:13:90");
	//		hosts.add("00:22:64:15:2a:38");
	//		hosts.add("00:22:64:16:9d:80");
	//		hosts.add("00:22:64:15:e6:6e");
	//		hosts.add("00:22:64:15:66:14");
	//		hosts.add("00:22:64:15:2a:30");
	//		hosts.add("00:22:64:15:a6:c2");
	//		hosts.add("00:22:64:14:f2:34");
	//		hosts.add("00:22:64:15:a6:b2");
	//		hosts.add("00:22:64:15:29:ca");
	//		hosts.add("00:22:64:16:9d:12");
	//		hosts.add("00:22:64:16:20:ac");
	//		hosts.add("00:22:64:16:9d:7e");
	//		hosts.add("00:22:64:17:15:ca");
	//		hosts.add("00:22:64:15:a6:56");
	//		hosts.add("00:22:64:15:a6:5a");
	//
	//		hosts.add("00:22:64:16:20:9c");
	//		hosts.add("00:22:64:16:9d:fa");
	//		hosts.add("00:22:64:15:a9:04");
	//		hosts.add("00:22:64:17:13:9e");
	//		hosts.add("00:22:64:15:63:76");
	//		hosts.add("00:22:64:15:e9:d0");
	//		hosts.add("00:22:64:15:e8:08");
	//		hosts.add("00:22:64:15:a6:ba");
	//		hosts.add("00:22:64:15:67:86");
	//		hosts.add("00:22:64:14:b3:bf");
	//		hosts.add("00:22:64:17:13:a4");
	//		hosts.add("00:22:64:16:a3:3a");
	//		hosts.add("00:22:64:15:e7:28");
	//		hosts.add("00:22:64:15:e8:d8");
	//		hosts.add("00:22:64:14:b0:ed");
	//		hosts.add("00:22:64:14:f5:96");
	//		hosts.add("00:22:64:14:b3:bd");
	//		hosts.add("00:22:64:16:9d:2c");
	//		hosts.add("00:22:64:15:23:d4");
	//		ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getPMS(mUser, mPass).extendedStatusList(hosts);
	//		for(ExtendedPMSStatus exStat:statuses)
	//		{
	//			Log.e(TAG, "passed mac="+exStat.getMac());
	////			String passedHost = hosts.get(exStat.getMac());
	////			Log.e(TAG, "HOST in LIST="+passedHost);
	////			ControllableDevice cd = new ControllableDevice(getActivity(), exStat, passedHost, "schule\\\\sesame_pms", " my_sesame_pms", true);
	////			//			ControllableDevice cd = new ControllableDevice(getApplicationContext(), host.getValue(), host.getKey(), "schule\\sesame_pms", " my_sesame_pms", true);
	////			if(cd.isValid())
	////			{
	////				mAllDevices.add(cd);
	////				mSelection.put(cd.getMac(), false);
	////			}
	//		}
	//		//		
	//		//		JSONArray arr = new JSONArray(hosts);
	//		////		arr.
	//		//		JSONArray names = new JSONArray();
	//		////		JSONObject
	//		//		StringBuilder sb = new StringBuilder();
	//		//		sb.append("[");
	//		//		for(int i =0 ;i<hosts.size()-1;i++)
	//		//		{
	//		//			JSONObject json = new JSONObject();
	//		//			try {
	//		//				json.put("mac", hosts.get(i));
	//		//				sb.append(json.toString());
	//		//				sb.append(",");
	//		//			} catch (JSONException e) {
	//		//				// TODO Auto-generated catch block
	//		//				e.printStackTrace();
	//		//			}
	//		////			arr.put(json);
	//		//		}
	//		//		JSONObject lastObject = new JSONObject();
	//		//		try {
	//		//			lastObject.put("mac", hosts.get(hosts.size()-1));
	//		//		} catch (JSONException e) {
	//		//			// TODO Auto-generated catch block
	//		//			e.printStackTrace();
	//		//		}
	//		//		sb.append(lastObject.toString());
	//		//		sb.append("]");
	//		//		Log.e(TAG, sb.toString());
	//		////		JSONArray arr2  = new JSONArray();
	//		////		for(int i =0;i<arr.length();i++)
	//		////		{
	//		////			arr2.put("mac");
	//		////		}
	//		////		
	//		//////		arr.toJSONObject(names)
	//		////		try {
	//		////			Log.e(TAG, arr.toJSONObject(arr2).toString());
	//		////		} catch (JSONException e) {
	//		////			// TODO Auto-generated catch block
	//		////			e.printStackTrace();
	//		////		}
	//		//		
	//		////		 [{ "mac" : "1" }, { "mac" : "2" }, { "mac" : "3" } ]
	//
	//	}


	/**
	 * checks if the device currently is connected to the internet
	 * @return true if the device is connected, false otherwise
	 */
	private boolean checkConnectivity() 
	{
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public void onDestroy()
	{
//		mPMSHelper.stopUpdates();
		stopUiUpdates();
		super.onDestroy();
	}

	@Override
	public void onPause()
	{
		stopUiUpdates();
		super.onPause();
	}
	
	public void startSingleUiUpdate()
	{
//		if(null==mUiUpdateTimer)
//		{
//			mUiUpdateTimer = new Timer();
//		}
		new Timer().schedule(new UiUpdateTask(), 0);
	}

	public void stopUiUpdates() {
//		mPMSHelper.stopUpdates();
		if(null!=mUiUpdateTimer)
		{
			mUiUpdateTimer.cancel();
			mUiUpdateTimer.purge();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if(checkConnectivity())
		{
			Log.i(TAG, "starting updates");
//			startContinuousUiUpdates();
		}
		else
		{
			new PMSNoNetworkDialogFragment().show(getFragmentManager(), null);
		}
	}

	private void startContinuousUiUpdates()
	{
//		mPMSHelper.startUpdates();
		stopUiUpdates();
		mUiUpdateTimer = new Timer();
		mUiUpdateTimer.schedule(new UiUpdateTask(), 0, UI_UPDATE_PERIOD);
	}

	//	@Override
	//	public Dialog onCreateDialog(int _id)
	//	{
	//		final Dialog d = new Dialog(this);
	//
	//		d.setContentView(R.layout.custom_action_dialog);
	//		d.setTitle(mSelectedDevice.getHostname());
	//		final ArrayList<CommandListEntry> cles = new ArrayList<CommandListEntry>();
	//		ListView commands = (ListView)d.findViewById(R.id.cusomtActionDialogCommandList);
	//		commands.setAdapter(new CommandAdapter(getApplicationContext(), cles));
	//		TextView message = (TextView)d.findViewById(R.id.messageLabel);
	//
	//		commands.setBackgroundColor(android.R.color.white);
	//		switch(_id)
	//		{
	//		case ACTIVE_DEVICE_ACTION_DIALOG:
	//			if(mSelectedDevice.getIdleSinceMinutes()<IDLE_MINUTES_WARNING_THRESHOLD)
	//			{
	//
	//				message.setText(getString(R.string.PMSClientActivity_activeDeviceActionDialogBaseMessage)+mSelectedDevice.getIdleSinceMinutes()+")");
	//			}
	//			//			strings.add(getString(R.string.PMSClientActivity_activeDeviceDialogShutDownCommand));
	//			//			strings.add(getString(R.string.PMSClientActivity_activeDeviceDialogSleepCommand));
	//			//			strings.add(getString(android.R.string.cancel));
	//			cles.add(new CommandListEntry(getActivity(), CommandType.shutDown));
	//			cles.add(new CommandListEntry(getActivity(), CommandType.sleep));
	//			cles.add(new CommandListEntry(getActivity(), CommandType.cancel));
	//
	//			commands.setOnItemClickListener(new OnItemClickListener() 
	//			{
	//				@Override
	//				public void onItemClick(AdapterView<?> arg0, View arg1,
	//						int arg2, long arg3) {
	//					switch(arg2)
	//					{
	//					case 0:
	//						mSelectedDevice.powerOff(PowerOffState.shutdown);
	//						markDirty(mSelectedDevice);
	//						d.dismiss();
	//						break;
	//					case 1:
	//						mSelectedDevice.powerOff(PowerOffState.sleep);
	//						markDirty(mSelectedDevice);
	//						d.dismiss();
	//						break;
	//					case 2:
	//						d.cancel();
	//						break;
	//					}
	//				}
	//			});	
	//			break;
	//
	//		case INACTIVE_DEVICE_ACTION_DIALOG:
	//			//			strings.add(getString(R.string.PMSClientActivity_inactiveDeviceDialogWakeUpCommand));
	//			//			strings.add(getString(android.R.string.cancel));
	//			cles.add(new CommandListEntry(getActivity(), CommandType.wakeUp));
	//			cles.add(new CommandListEntry(getActivity(), CommandType.cancel));
	//			commands.setOnItemClickListener(new OnItemClickListener() 
	//			{
	//				@Override
	//				public void onItemClick(AdapterView<?> arg0, View arg1,
	//						int arg2, long arg3) {
	//					switch(arg2)
	//					{
	//					case 0:
	//						mSelectedDevice.wakeUp();
	//						markDirty(mSelectedDevice);
	//						d.dismiss();
	//						break;
	//					case 1:
	//						d.cancel();
	//						break;
	//					}
	//				}
	//			});
	//			break;
	//		case NO_NETWORK_DIALOG:
	//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	//			builder.setCancelable(true);
	//			builder.setTitle(R.string.PMSClientActivity_noNetworkDialogTitle);
	//			builder.setMessage(R.string.PMSClientActivity_noNetworkDialogMessage);
	//
	//			builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	//
	//				@Override
	//				public void onClick(DialogInterface dialog, int which) {
	//					dialog.dismiss();
	//					finish();
	//				}
	//			});
	//			return builder.create();
	//		case CANT_SHUTDOWN_DIALOG:
	//			AlertDialog.Builder shutDownBuilder = new AlertDialog.Builder(this);
	//			shutDownBuilder.setCancelable(true);
	//			shutDownBuilder.setTitle("");
	//			shutDownBuilder.setMessage(R.string.PMSClientActivity_cantShutDownDialogMessage);
	//
	//			shutDownBuilder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	//
	//				@Override
	//				public void onClick(DialogInterface dialog, int which) {
	//					dialog.dismiss();
	//					finish();
	//				}
	//			});
	//			return shutDownBuilder.create();
	//		case CANT_WAKEUP_DIALOG:
	//			AlertDialog.Builder wakeUpBuilder = new AlertDialog.Builder(this);
	//			wakeUpBuilder.setCancelable(true);
	//			wakeUpBuilder.setTitle("");
	//			wakeUpBuilder.setMessage(R.string.PMSClientActivity_cantWakeUpDialogMessage);
	//
	//			wakeUpBuilder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	//
	//				@Override
	//				public void onClick(DialogInterface dialog, int which) {
	//					dialog.dismiss();
	//					finish();
	//				}
	//			});
	//			return wakeUpBuilder.create();
	//		}
	//		return d;
	//	}



	//	@Override
	//	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) 
	//	{
	//		// TODO Auto-generated method stub
	//		super.onPrepareDialog(id, dialog, args);
	//		switch(id)
	//		{
	//		case CANT_SHUTDOWN_DIALOG:
	//		case CANT_WAKEUP_DIALOG:
	//			AlertDialog ad = (AlertDialog)dialog;
	//			ad.setTitle(args.getCharSequence(BUNDLE_HOSTNAME_KEY));
	//			break;
	//		case ACTION_IN_PROGRESS_DIALOG:
	//			ProgressDialog pd = (ProgressDialog) dialog;
	//			pd.setMessage(args.getString(BUNDLE_MESSAGE_KEY));
	//			pd.setMax(args.getInt(BUNDLE_SELECTED_NUMBER_KEY));
	//			break;
	//		}
	//	}

	//	/**
	//	 * creates the networking dialog
	//	 */
	//	private void setupNetworkingDialog()
	//	{
	//		mNetworkingDialog = new ProgressDialog(PMSClientFragment.this);
	//		mNetworkingDialog.setMessage(getString(R.string.PMSClientActivity_networkingProgressDialogTitle));
	////		mNetworkingDialog.setCancelable(false);
	//		mNetworkingDialog.setCanceledOnTouchOutside(false);
	//		//		mNetworkingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	//		mNetworkingDialog.setIndeterminate(true);
	//	}

	//	private void setupActionInProgressDialog()
	//	{
	//		mActionInProgressDialog = new ProgressDialog(PMSClientFragment.this);
	//		//		mActionInProgressDialog.setMessage(getString(R.string.PMSClientActivity_networkingProgressDialogTitle));
	//		mActionInProgressDialog.setCancelable(false);
	//		mActionInProgressDialog.setCanceledOnTouchOutside(false);
	//		mActionInProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	//	}

	/**
	 * shows the networking dialog
	 */
	//	private void showNetworkingDialog()
	//	{
	//		new Thread(new Runnable() 
	//		{
	//			@Override
	//			public void run() 
	//			{
	//				Looper.prepare();
	//				mNetworkingDialog.show();
	//				Looper.loop();
	//			}
	//		}).start();
	//	}

	/**
	 * dismisses the networking dialog if it is showing
	 */
	//	private void dismissNetworkingDialog()
	//	{
	////		if(mNetworkingDialog.isShowing())
	//		{
	//			mNetworkingDialog.dismiss();
	//		}
	//	}



	//	/**
	//	 * queries which computers can be controlled via PMS and adds their ControllableDevice representations to the list
	//	 * of all devices
	//	 */
	//	private void queryControllableDevicesKDF() 
	//	{
	//		//		ArrayList<String> macs = PMSProvider.getDeviceList();
	//		HashMap<String,String>hosts = new HashMap<String, String>();
	//		hosts.put("00:25:b3:16:ad:14", "DV101");
	//		hosts.put("00:25:b3:17:df:1d", "DV102");
	//		hosts.put("00:25:b3:16:ac:1a", "DV103");
	//		hosts.put("00:25:b3:17:e2:93", "DV104");
	//		hosts.put("00:25:b3:16:ac:ad", "DV105");
	//		hosts.put("00:25:b3:16:ab:f0", "DV106");
	//		hosts.put("00:25:b3:16:ad:17", "DV107");
	//		hosts.put("00:25:b3:16:ac:17", "DV108");
	//		hosts.put("00:25:b3:16:ac:40", "DV109");
	//		hosts.put("00:25:b3:17:e2:61", "DV110");
	//		hosts.put("00:25:b3:17:e2:94", "DV111");
	//		hosts.put("00:25:b3:16:ab:9e", "DV112");
	//		hosts.put("00:25:b3:17:e2:62", "DV113");
	//		hosts.put("00:25:b3:17:e1:e8", "DV114");
	//		hosts.put("00:25:b3:16:ac:65", "DV115");
	//		hosts.put("00:25:b3:17:df:24", "DV116");
	//		hosts.put("00:25:b3:17:e2:31", "DV118");
	//		hosts.put("00:25:b3:17:e2:f6", "DV119");
	//
	//		hosts.put("00:22:64:16:9d:84", "DV301");
	//		hosts.put("00:22:64:15:e9:be", "DV302");
	//		hosts.put("00:22:64:15:2a:46", "DV303");
	//		hosts.put("00:22:64:17:13:90", "DV304");
	//		hosts.put("00:22:64:15:2a:38", "DV305");
	//		hosts.put("00:22:64:16:9d:80", "DV306");
	//		hosts.put("00:22:64:15:e6:6e", "DV307");
	//		hosts.put("00:22:64:15:66:14", "DV308");
	//		hosts.put("00:22:64:15:2a:30", "DV309");
	//		hosts.put("00:22:64:15:a6:c2", "DV310");
	//		hosts.put("00:22:64:14:f2:34", "DV311");
	//		hosts.put("00:22:64:15:a6:b2", "DV312");
	//		hosts.put("00:22:64:15:29:ca", "DV313");
	//		hosts.put("00:22:64:16:9d:12", "DV314");
	//		hosts.put("00:22:64:16:20:ac", "DV315");
	//		hosts.put("00:22:64:16:9d:7e", "DV316");
	//		hosts.put("00:22:64:17:15:ca", "DV317");
	//		hosts.put("00:22:64:15:a6:56", "DV318");
	//		hosts.put("00:22:64:15:a6:5a", "DV319");
	//
	//		hosts.put("00:22:64:16:20:9c", "DV601");
	//		hosts.put("00:22:64:16:9d:fa", "DV602");
	//		hosts.put("00:22:64:15:a9:04", "DV603");
	//		hosts.put("00:22:64:17:13:9e", "DV604");
	//		hosts.put("00:22:64:15:63:76", "DV605");
	//		hosts.put("00:22:64:15:e9:d0", "DV606");
	//		hosts.put("00:22:64:15:e8:08", "DV607");
	//		hosts.put("00:22:64:15:a6:ba", "DV608");
	//		hosts.put("00:22:64:15:67:86", "DV609");
	//		hosts.put("00:22:64:14:b3:bf", "DV610");
	//		hosts.put("00:22:64:17:13:a4", "DV611");
	//		hosts.put("00:22:64:16:a3:3a", "DV612");
	//		hosts.put("00:22:64:15:e7:28", "DV613");
	//		hosts.put("00:22:64:15:e8:d8", "DV614");
	//		hosts.put("00:22:64:14:b0:ed", "DV615");
	//		hosts.put("00:22:64:14:f5:96", "DV616");
	//		hosts.put("00:22:64:14:b3:bd", "DV617");
	//		hosts.put("00:22:64:16:9d:2c", "DV618");
	//		hosts.put("00:22:64:15:23:d4", "DV619");
	//
	////		String clients = PMSProvider.getPMS().getClients();
	////		Log.e(TAG, clients);
	//
	//		mNetworkingDialog.setMax(hosts.size());
	//		mNetworkingDialog.show(mFragMan, null);
	//		//		String[] macStrings = new String[hosts.size()];
	//		ArrayList<String> macs = new ArrayList<String>(hosts.keySet());
	//		ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getPMS(mUser, mPass).extendedStatusList(macs);
	//		if(null==statuses)
	//		{
	//			Log.e(TAG, "could not query statuses");
	//			mNetworkingDialog.dismiss();
	//			return;
	//		}
	//		mAllDevices = new ArrayList<ControllableDevice>();
	//		for(ExtendedPMSStatus exStat:statuses)
	//		{
	//			Log.e(TAG, "passed mac="+exStat.getMac());
	//			String passedHost = hosts.get(exStat.getMac());
	//			Log.e(TAG, "HOST in LIST="+passedHost);
	//			ControllableDevice cd = new ControllableDevice(getActivity(), exStat, passedHost, "schule\\\\sesame_pms", " my_sesame_pms", true);
	//			//			ControllableDevice cd = new ControllableDevice(getApplicationContext(), host.getValue(), host.getKey(), "schule\\sesame_pms", " my_sesame_pms", true);
	//			if(cd.isValid())
	//			{
	//				mAllDevices.add(cd);
	//				mSelection.put(cd.getMac(), false);
	//			}
	//			//			PMSClientActivity.this.mNetworkingDialog.incrementProgressBy(1);
	//		}
	//
	//		//			for(int i = 0;i<macs.size();i++)
	//		//			{
	//		//				ControllableDevice cd = new ControllableDevice(getApplicationContext(), macs.get(i), "admin1", "pwd", true);
	//		//				if(cd.isValid())
	//		//				{
	//		//					mAllDevices.add(cd);
	//		//					mSelection.put(cd.getMac(), false);
	//		//				}
	//		//				PMSClientActivity.this.mNetworkingDialog.incrementProgressBy(1);
	//		//			}
	//
	//		mNetworkingDialog.dismiss();
	//	}















	//
	//	/**
	//	 * toasts a message when a device could not be selected
	//	 */
	//	private void toastSelectionFail()
	//	{
	//		Toast.makeText(getActivity(), "only devices from the same category (active/inactive) can be selected at the same time.", Toast.LENGTH_LONG).show();
	//	}










	@Override
	public void onClick(View arg0) 
	{
		if (arg0.getId() == R.id.sleepButton) {
			mPMSHelper.handleSleepAll();
		} else if (arg0.getId() == R.id.shutDownButton) {
			Log.i(TAG, "shut down all");
			mPMSHelper.handlePowerOffAll();
		} else if (arg0.getId() == R.id.wakeUpButton) {
			Log.i(TAG, "wake up all");
			mPMSHelper.handleWakeUpAll();
		}
		if(null!=mSelectAllActiveDevCb)
		{
			mSelectAllActiveDevCb.setChecked(false);
		}
		if(null!=mSelectAllInactiveDevCb)
		{
			mSelectAllInactiveDevCb.setChecked(false);
		}
//		mPMSHelper.deselectAll();
//		startSingleUiUpdate();
	}



	//	/**
	//	 * wakes up all selected devices
	//	 */
	//	private void wakeupSelectedDevices()
	//	{
	//		//		mUpdateThread.pause();
	//		//
	//		//		Runnable r = new Runnable() 
	//		//		{		
	//		//			@Override
	//		//			public void run() 
	//		//			{
	//		//				ArrayList<ControllableDevice> selDevs = getSelectedDevices();
	//		//
	//		//				for(ControllableDevice cd:selDevs)
	//		//				{
	//		//					markDirty(cd);
	//		//				}
	//		//				for(int i = 0;i<selDevs.size();i++)
	//		//				{
	//		//					ControllableDevice cd = selDevs.get(i);
	//		////					Looper.prepare();
	//		//					cd.wakeUp();
	//		////					Looper.loop();
	//		//					Log.e(TAG, "woke up:"+cd.getHostname());
	//		//					Log.e(TAG, "finished device "+(i+1)+" of "+selDevs.size());
	//		//				}
	//		//				mUpdateThread.resumeAfterPause();
	//		//
	//		//
	//		//			}
	//		//		};
	//		//		Thread wakeupThread = new Thread(r);
	//		//		wakeupThread.start();
	//
	//		//		final ArrayList<ControllableDevice> selDevs = getSelectedDevices();
	//		//		for(int i = 0 ;i<selDevs.size();i++)
	//		//		{
	//		//			final int idx = i;
	//		//			Runnable wakeUpRunnable = new Runnable() 
	//		//			{	
	//		//				@Override
	//		//				public void run() {
	//		//					markDirty(selDevs.get(idx));
	//		//					boolean res = selDevs.get(idx).wakeUp();
	//		//					Log.e(TAG, "wakeup of "+selDevs.get(idx).getHostname()+":"+res);
	//		//				}
	//		//			};
	//		//			
	//		//			new Thread(wakeUpRunnable).start();
	//		//			try {
	//		//				Thread.sleep(10);
	//		//			} catch (InterruptedException e) {
	//		//				// TODO Auto-generated catch block
	//		//				e.printStackTrace();
	//		//			}
	//		//			
	//		//		}
	//
	//		Runnable wakeupRunnable = new Runnable() {
	//
	//			@Override
	//			public void run() 
	//			{
	//				showActionInProgressDialog("wake up");
	//				
	//				stopAutoUpdate();
	//				ArrayList<ControllableDevice> selDevs = getSelectedDevices();
	//				for(int i = 0;i<selDevs.size();i++)
	//				{
	//					ControllableDevice cd = selDevs.get(i);
	//					markDirty(cd);
	//					cd.wakeUp();
	//					mActionInProgressDialog.incrementProgressBy(1);
	//					try {
	//						Thread.sleep(10);
	//					} catch (InterruptedException e) {
	//						// TODO Auto-generated catch block
	//						e.printStackTrace();
	//					}
	//				}
	//				deselectAll();
	//				startAutoUpdate();
	//				dismissActionInProgressDialog();
	////				runOnUiThread(new Runnable() {
	////					
	////					@Override
	////					public void run() {
	//////						dismissDialog(ACTION_IN_PROGRESS_DIALOG);
	////						mActionInProgressDialog.setProgress(0);
	////						mActionInProgressDialog.dismiss();
	////						
	////					}
	////				});
	//				
	//				//				mUpdateThread.resumeAfterPause();
	//			}
	//		};
	//		new Thread(wakeupRunnable).start();
	//	}

	//	private void showActionInProgressDialog(final String _msg)
	//	{
	////		getActivity().runOnUiThread(new Runnable() {
	////			
	////			@Override
	////			public void run() 
	////			{
	////				setupActionInProgressDialog();
	////				mActionInProgressDialog.setTitle(_msg);
	////				mActionInProgressDialog.setMax(getSelectedDevices().size());
	////				mActionInProgressDialog.show();
	////				
	////			}
	////		});
	//		Bundle args = new Bundle();
	//		args.putString(PMSActionInProgressDialogFragment.TITLE_BUNDLE_KEY, _msg);
	//		args.putInt(PMSActionInProgressDialogFragment.MAX_BUNDLE_KEY, getSelectedDevices().size());
	//		mActionInProgressDialog.setArguments(args);
	////		mActionInProgressDialog.s
	//	}
	//	
	//	private void dismissActionInProgressDialog()
	//	{
	////		getActivity().runOnUiThread(new Runnable() {
	////			
	////			@Override
	////			public void run() {
	////				mActionInProgressDialog.dismiss();
	////				
	////			}
	////		});
	//		mActionInProgressDialog.dismiss();
	//	}


	//	/**
	//	 * callback for the DeviceStateUpdateThread. called when new information on the devices is available
	//	 */
	//	public void notifyDataUpdated()
	//	{
	//		


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
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getActivity(), "410 gone", Toast.LENGTH_LONG).show();

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
//		ControllableDevice cd = mPMSHelper.getDeviceByMac(_mac);
//		Log.e(TAG, "removed "+cd.toString()+", actual remove has to be implemented yet.");
	}

	/**
	 * adds a device to the list of unavailable devices
	 * @param _mac the mac address of the device to add to the list
	 */
	private void addToNotAvailableList(String _mac)
	{
//		ControllableDevice cd = mPMSHelper.getDeviceByMac(_mac);
//		Log.e(TAG, "added "+cd.toString()+" to the not available list, actual add has to be implemented yet.");
	}

	/**
	 * displays a toast message saying that a computer is currently not available
	 * @param _mac the mac address of the device that is not available
	 */
	private void toastComputerNotAvailable(String _mac)
	{
//		ControllableDevice cd = mPMSHelper.getDeviceByMac(_mac);
//		Toast.makeText(getActivity(), cd.getHostname()+" is currently not available", Toast.LENGTH_LONG).show();
	}

	/**
	 * displays a toast message saying that currently there is no information available about a device
	 * @param _mac the mac address of the device on which no information is available
	 */
	private void toastNoInformationAvailable(String _mac)
	{
//		ControllableDevice cd = mPMSHelper.getDeviceByMac(_mac);
//		Toast.makeText(getActivity(), "no information about "+cd.getHostname()+" available", Toast.LENGTH_LONG).show();
	}

	/**
	 * displays the login dialog when credentials for a certain operation are necessary
	 * @param _mac the mac address of the device where credentails are needed
	 */
	private void showLoginDialog(String _mac)
	{
//		ControllableDevice cd = mPMSHelper.getDeviceByMac(_mac);
//		Log.e(TAG, "showing login dialog for "+cd.getHostname());
	}

	/**
	 * displays a dialog saying that a certain computer could not be shut down
	 * @param _mac the mac address of the device that could not be shut down
	 */
	private void showCantShutDownDialog(String _mac)
	{
		//		showDialog(CANT_SHUTDOWN_DIALOG, getBundledHostname(_mac));
		//		new PMSCantShutdownDialogFragment().show(mFragMan, null);+
//		PMSDialogFactory.showDialog(DialogType.CANT_SHUTDONW_DIALOG, getFragmentManager(), mPMSHelper.getController(), new Object[]{_mac});
	}

	/**
	 * displays a dialog saying that a certain computer could not be woke up
	 * @param _mac the mac address of the device that could not be woke up
	 */
	private void showCantWakeUpDialog(String _mac)
	{
		//		showDialog(CANT_WAKEUP_DIALOG, getBundledHostname(_mac));
		//		new PMSCantWakeUpDialogFragment().show(mFragMan, null);
//		PMSDialogFactory.showDialog(DialogType.CANT_WAKEUP_DIALOG, getFragmentManager(), mPMSHelper.getController(), new Object[]{_mac});
	}

	//	/**
	//	 * stores the hostname of a device in a new bundle. the entry is made for the key BUNDLE_HOSTNAME_KEY
	//	 * @param _mac the mac address of the device which's hostname should be bundled 
	//	 * @return a bundle containing the hostname
	//	 */
	//	private Bundle getBundledHostname(String _mac)
	//	{
	//		ControllableDevice cd = mPMSHelper.getDeviceByMac(_mac);
	//		Bundle res = new Bundle();
	//		res.putCharSequence(BUNDLE_HOSTNAME_KEY, cd.getHostname());
	//		return res;
	//	}
	//
	//	private Bundle createMessageAndMaxBundle(String _msg, int _max)
	//	{
	//		Bundle res = new Bundle();
	//		res.putString(BUNDLE_MESSAGE_KEY, _msg);
	//		res.putInt(BUNDLE_SELECTED_NUMBER_KEY, _max);
	//		return res;
	//	}





	//	@Override
	//	public void notifyPMSUpdated()
	//	{
	//		Log.e(TAG, "notified by update thread");
	//
	//
	//		mUiUpdateTimer.schedule(new UiUpdateTask(), 0);
	//
	//		//		notifyAdapter();	
	//	}



	private class UiUpdateTask extends TimerTask
	{

		@Override
		public void run()
		{

//			if(mPMSHelper.areDevicesLoaded())
			{
				final long start = System.currentTimeMillis();

				final ArrayList<ControllableDevice> activeDevs  = SesameDataCache.getInstance(null).getDevices(mTitle, true);
				final ArrayList<ControllableDevice> inactiveDevs = SesameDataCache.getInstance(null).getDevices(mTitle, false);
				
				final ArrayList<ControllableDeviceListEntry> activeEntries = new ArrayList<ControllableDeviceListEntry>();
				final ArrayList<ControllableDeviceListEntry> inactiveEntries = new ArrayList<ControllableDeviceListEntry>();
				
//				for()
				
				for(ControllableDevice cd:activeDevs)
				{
					ControllableDeviceListEntry cdle = new ControllableDeviceListEntry(cd);
					mPMSHelper.setUiInfo(cdle);

					activeEntries.add(cdle);
				}
//
				for(ControllableDevice cd:inactiveDevs)
				{
					ControllableDeviceListEntry cdle = new ControllableDeviceListEntry(cd);
					mPMSHelper.setUiInfo(cdle);
					inactiveEntries.add(cdle);
				}
				if(!isAdded())
				{
					return;
				}

				mUiHandler.post(new Runnable() {
					//				
					@Override
					public void run() 
					{

						mActiveDevIndicatorLabel.setText(getString(R.string.pms_activeDeviceSeparatorText)+activeDevs.size()+")");
						mInactiveDevIndicatorLabel.setText(getString(R.string.pms_inactiveDeviceSeparatorText)+inactiveDevs.size()+")");


//						mActiveAdapter.setNotifyOnChange(false);
						mActiveAdapter.clear();
//						mActiveAdapter.setNotifyOnChange(true);
						mActiveAdapter.addAll(activeEntries);
//						//		mActiveListEntries.add(new SeparatorListEntry(mCtx, ListType.active, activeDevs.size()));
//
						
//						mInactiveAdapter.setNotifyOnChange(false);
						mInactiveAdapter.clear();
//						mInactiveAdapter.setNotifyOnChange(true);
						mInactiveAdapter.addAll(inactiveEntries);
//						//		mInactiveListEntries.add(new SeparatorListEntry(mCtx, ListType.inactive, inactiveDevs.size()));
//
//						//			
						
						// TODO Auto-generated method stub
//						mActiveAdapter.notifyDataSetChanged();
//						mInactiveAdapter.notifyDataSetChanged();
						long duration = System.currentTimeMillis()-start;
						Log.i(TAG, "updating ui done, adapter notified"+duration+"ms");
//						PMSDialogFactory.dismissCurrentDialog();
					}
				});
			}
//			else
//			{
//				Log.e(TAG, "devices not loaded");
//			}

		}
	}

	@Override
	public void onCheckedChanged(CompoundButton _buttonView, boolean _isChecked) 
	{
		if(_buttonView.getTag().equals(ACTIVE_CB_TAG))
		{
			Log.i(TAG, "handling multiple selection of active devs");
			mPMSHelper.handleMultipleSelectionAttempt(ListType.active, _isChecked);
		}
		else if(_buttonView.getTag().equals(INACTIVE_CB_TAG))
		{
			Log.i(TAG, "handling multiple selection of inactive devs");
			mPMSHelper.handleMultipleSelectionAttempt(ListType.inactive, _isChecked);
		}
		else
		{
			Log.e(TAG, "no checkbox recognized");
		}

	}

	@Override
	public void notifyPMSUpdated() {
//		startSingleUiUpdate();
		
	}




	//	@Override
	//	public void notifySelectionFail() {
	//		// TODO Auto-generated method stub
	//		toastSelectionFail();
	//	}
	//
	//	@Override
	//	public int getNumSelectedDevices() {
	//		// TODO Auto-generated method stub
	//		return mUiHelper.getSelectedDevices().size();
	//	}
	//
	//	@Override
	//	public void markDirty(ControllableDevice _cd) {
	//		mUiHelper.markDirty(_cd);
	//		refreshListEntries();
	//		
	//	}
	//
	//	@Override
	//	public void deselectAll() {
	//		mUiHelper.deselectAll();
	//		refreshListEntries();
	//		
	//	}
	//
	//	@Override
	//	public void notifyDevicesLoaded() {
	//		mUiHelper.createListEntries(mPmsController.getAllDevices());
	//		refreshListEntries();
	//		
	//	}




	//	@Override
	//	public void handlePowerClick(ControllableDevice _cd) {
	//		if(_cd.isAlive())
	//		{
	//			PMSDialogFactory.showDialog(DialogType.ACTIVE_DEVICE_ACTION_DIALOG, mFragMan, mPmsController, new Object[]{mCtx, _cd});
	//		}
	//		else
	//		{
	//			PMSDialogFactory.showDialog(DialogType.INACTIVE_DEVICE_ACTION_DIALOG, mFragMan, mPmsController, new Object[]{mCtx, _cd});
	//		}
	//		
	//	}
}
