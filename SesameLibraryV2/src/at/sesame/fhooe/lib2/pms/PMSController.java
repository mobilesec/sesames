package at.sesame.fhooe.lib2.pms;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import at.sesame.fhooe.lib2.pms.SeparatorListEntry.ListType;
import at.sesame.fhooe.lib2.pms.dialogs.IPMSDialogActionHandler;
import at.sesame.fhooe.lib2.pms.dialogs.PMSActionDialogFragment;
import at.sesame.fhooe.lib2.pms.dialogs.PMSActionInProgressDialogFragment;
import at.sesame.fhooe.lib2.pms.dialogs.PMSActionDialogFragment.PMSActionDialogType;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory.DialogType;
import at.sesame.fhooe.lib2.pms.dialogs.PMSNetworkingInProgressDialogFragment;
import at.sesame.fhooe.lib2.pms.hosts.HostList;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice.PowerOffState;
import at.sesame.fhooe.lib2.pms.model.ExtendedPMSStatus;

public class PMSController
implements IPMSDialogActionHandler, IPMSUpdateListener
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "PMSController";



	/**
	 * a list of all controllable devices available
	 */
	private final ArrayList<ControllableDevice> mAllDevices = new ArrayList<ControllableDevice>();

	/**
	 * the thread that queries the status of all devies in the background
	 */
	private DeviceStateUpdater mUpdater;

	private String mUser = "peter";
	private String mPass = "thatpeter";

//	private IPmsUi mUi;
	private PmsHelper mUiHelper;
	private FragmentManager mFragMan;

	private boolean mDevicesLoaded = false;

//	
//
//	/**
//	 * list of all displayed list entries
//	 */
//	private ArrayList<IListEntry> mEntries = new ArrayList<IListEntry>();


	/**
	 * the currently selected device
	 */
	private ControllableDevice mSelectedDevice;
	


	private Context mCtx;
//	private Handler mUiHandler;

	public PMSController(Context _ctx, PmsHelper _helper, HostList _hosts2Load, FragmentManager _fragMan)
	{
		mDevicesLoaded = false;
		mCtx = _ctx;
//		mUiHandler = _uiHandler;
//		mUi = _listener;
		mUiHelper = _helper;

		mFragMan = _fragMan;

			new QueryDevsTask().execute(new HostList[]{_hosts2Load});


//		queryControllableDevicesSim();
//		mActiveDeviceControlContainer = _activeDeviceControl;
//		mInactiveDeviceControlContainer = _inactiveDeviceControl;
//		setControlContainerVisibility(View.GONE, View.GONE);
	}

	public void startAutoUpdate()
	{
		stopAutoUpdate();
		if(mDevicesLoaded)
		{
			mUpdater = new DeviceStateUpdater(this, mAllDevices, mUiHelper, mUser, mPass);
			mUpdater.startUpdating();
//			mUpdateThread.start();			
		}
	}

	public void stopAutoUpdate()
	{
		if(null!=mUpdater)
		{
			mUpdater.stopUpdating();
		}

	}

//	/**
//	 * returns a list of all selected devices
//	 * @return a list of all selected devices
//	 */
//	private ArrayList<ControllableDevice> getSelectedDevices()
//	{
//		ArrayList<ControllableDevice> res = new ArrayList<ControllableDevice>();
//		//		synchronized (mAllDevices) 
//		{
//			for(ControllableDevice cd:mAllDevices)
//			{
//				if(mSelection.get(cd.getMac()))
//				{
//					res.add(cd);
//				}
//			}
//		}
//
//		return res;
//	}
//	
//	public HashMap<String, Boolean> getSelectionMap()
//	{
//		return mSelection;
//	}

	

	



	public void wakeupDevices(final ArrayList<ControllableDevice> _selectedDevs)
	{
			new WakeupTask(_selectedDevs.size()).execute(_selectedDevs);
	}
	
	private class WakeupTask extends AsyncTask<ArrayList<ControllableDevice>, Void, Void>
	{
		private PMSActionInProgressDialogFragment mDialog;
		private int mMax;
		
		public WakeupTask(int _max)
		{
			mMax = _max;
		}
		@Override
		protected Void doInBackground(ArrayList<ControllableDevice>... params) {
			ArrayList<ControllableDevice> selDevs = params[0];
			
			for(ControllableDevice cd:selDevs)
			{
//				ControllableDevice cd = selDevs.get(i);
				mUiHelper.markDirty(cd, true);
				try {
				cd.wakeUp();
//				aipdf.incrementProgressBy(1);
					Thread.sleep(100);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				publishProgress(new Void[]{});
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			mUiHelper.deselectAll();
			startAutoUpdate();
			PMSDialogFactory.dismissCurrentDialog();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = (PMSActionInProgressDialogFragment) PMSDialogFactory.showDialog(DialogType.ACTION_IN_PROGRESS_DIALOG, mFragMan, PMSController.this, new Object[]{mCtx, "wakeup", mMax});
			stopAutoUpdate();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			if(null!=mDialog)
			{
				mDialog.incrementProgressBy(1);				
			}
			else
			{
				Log.e(TAG, "could not update dialog (null)");
			}
		}
		
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

		//		mNetworkingDialog.setMax(hosts.size());
//		mUiHandler.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				PMSDialogFactory.showDialog(DialogType.NETWORKING_IN_PROGRESS, mFragMan, PMSController.this, new Object[]{mCtx});
//				
//			}
//		});
		//		String[] macStrings = new String[hosts.size()];
		ArrayList<String> macs = new ArrayList<String>(hosts.keySet());
		ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getPMS(mUser, mPass).extendedStatusList(macs);
		if(null==statuses)
		{
			Log.e(TAG, "could not query statuses");
			//			mNetworkingDialog.dismiss();
			PMSDialogFactory.dismissCurrentDialog();
			return;
		}
//		mAllDevices = new ArrayList<ControllableDevice>();
		for(ExtendedPMSStatus exStat:statuses)
		{
			Log.e(TAG, "passed mac="+exStat.getMac());
			String passedHost = hosts.get(exStat.getMac());
			Log.e(TAG, "HOST in LIST="+passedHost);
			ControllableDevice cd = new ControllableDevice(mCtx, exStat, passedHost, "schule\\\\sesame_pms", " my_sesame_pms", true);
			//			ControllableDevice cd = new ControllableDevice(getApplicationContext(), host.getValue(), host.getKey(), "schule\\sesame_pms", " my_sesame_pms", true);
			if(cd.isValid())
			{
				mAllDevices.add(cd);
//				mSelection.put(cd.getMac(), false);
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

//		PMSDialogFactory.dismissCurrentDialog();
	}
	
	private void loadDevices(HostList _hl)
	{
//		ArrayList<String> macs = new ArrayList<String>(hosts.keySet());
		ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getPMS(mUser, mPass).extendedStatusList(_hl.getMacList());
		if(null==statuses)
		{
			Log.e(TAG, "could not query statuses");
			//			mNetworkingDialog.dismiss();
//			PMSDialogFactory.dismissCurrentDialog();
			return;
		}
//		mAllDevices = new ArrayList<ControllableDevice>();
		for(ExtendedPMSStatus exStat:statuses)
		{
			Log.e(TAG, "passed mac="+exStat.getMac());
			String passedHost = _hl.getHostNameForMac(exStat.getMac());
			Log.e(TAG, "HOST in LIST="+passedHost);
			ControllableDevice cd = new ControllableDevice(mCtx, exStat, passedHost, "schule\\\\sesame_pms", " my_sesame_pms", true);
			//			ControllableDevice cd = new ControllableDevice(getApplicationContext(), host.getValue(), host.getKey(), "schule\\sesame_pms", " my_sesame_pms", true);
			if(cd.isValid())
			{
				mAllDevices.add(cd);
//				mSelection.put(cd.getMac(), false);
			}
			//			PMSClientActivity.this.mNetworkingDialog.incrementProgressBy(1);
		}
	}



	private void queryControllableDevicesTest(int numDummyEntries)
	{
//		mNetworkingDialog.show(mFragMan, null);
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
			mAllDevices.add(new ControllableDevice(mCtx, status, status.getHostname(), "schule\\\\sesame_pms", " my_sesame_pms", true));
//			mPmsController.getSelectionMap().put(macs.get(i), false);
		}
//		mNetworkingDialog.dismiss();
	}
	



	
	
	

//	/**
//	 * this method is called whenever the power icon of a list entry was pressed
//	 * @param _cd the ControllableDevice associated with the list entry
//	 */
//	public void handlePowerClick(ControllableDevice _cd)
//	{
////		ControllableDeviceListEntry cdle = getListEntryFromDevice(_cd);
////		if(null==cdle)
////		{
////			return;
////		}
////		mSelectedDevice = cdle.getControllableDevice();
//		mSelectedDevice = _cd;
//
//		if(mSelectedDevice.isAlive())
//		{
//
//			//			showDialog(ACTIVE_DEVICE_ACTION_DIALOG);
//			new PMSActionDialogFragment(this, mSelectedDevice, PMSActionDialogType.ActiveDeviceActionDialog)
//			.show(mFragMan, null);
//		}
//		else
//		{
//			//			showDialog(INACTIVE_DEVICE_ACTION_DIALOG);
//			new PMSActionDialogFragment(this, mSelectedDevice, PMSActionDialogType.InactiveDeviceActionDialog)
//			.show(mFragMan, null);
//		}
//	}



	@Override
	public void handleDialogPowerOff(ControllableDevice _cd) 
	{
		mUiHelper.markDirty(_cd, true);
		_cd.powerOff(PowerOffState.shutdown);

	}

	@Override
	public void handleDialogSleep(ControllableDevice _cd) 
	{
		mUiHelper.markDirty(_cd, true);
		_cd.powerOff(PowerOffState.sleep);

	}

	@Override
	public void handleDialogWakeUp(ControllableDevice _cd) 
	{
		mUiHelper.markDirty(_cd, true);
		_cd.wakeUp();
	}
	
	/**
	 * powers off or puts to sleep all currently selected devices based on the passed PowerOffState
	 * @param _state determines whether to shut down or put to sleep all selected devices 
	 */
	public void powerOffDevices(final ArrayList<ControllableDevice> _devices, final PowerOffState _state)
	{
		new PowerOffTask(_state.name(), _devices.size()).execute(new Object[]{_devices,_state});
	}
	
	
	private class PowerOffTask extends AsyncTask<Object, Void, Void>
	{

		private String mTitle;
		private int mMax;
		private PMSActionInProgressDialogFragment mDialog;
		
		public PowerOffTask(String _title, int _max)
		{
			mTitle = _title;
			mMax  =_max;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			PMSDialogFactory.dismissCurrentDialog();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			mDialog = (PMSActionInProgressDialogFragment) PMSDialogFactory.showDialog(DialogType.ACTION_IN_PROGRESS_DIALOG, mFragMan, PMSController.this, new Object[]{mCtx, mTitle, mMax});
		}
		
		

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			mDialog.incrementProgressBy(1);
		}

		@Override
		protected Void doInBackground(Object... params) {
//			mUpdateThread.pause();
			//					stopAutoUpdate();
			//					try {
			//						Thread.sleep(16000);
			//					} catch (InterruptedException e1) {
			//						// TODO Auto-generated catch block
			//						e1.printStackTrace();
			//					}
//			ArrayList<ControllableDevice> selDevs = getSelectedDevices();
			ArrayList<ControllableDevice> devices = (ArrayList<ControllableDevice>)params[0];
			PowerOffState state = (PowerOffState)params[1];
			for(ControllableDevice cd:devices)
			{
				mUiHelper.markDirty(cd,true);
				
				try 
				{
					cd.powerOff(state);
					Thread.sleep(50);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.publishProgress(new Void[]{});
			}
			mUiHelper.deselectAll();
			//					startAutoUpdate();
			//					mUpdateThread.resumeAfterPause();
			//					for(ControllableDevice cd:selDevs)
			//					{
			//						
			//					}

			return null;
		}
		
	}
	
	
	public class QueryDevsTask extends AsyncTask<HostList, Void, Void>
	{
		
		@Override
		protected void onPreExecute() {
			PMSDialogFactory.showDialog(DialogType.NETWORKING_IN_PROGRESS, mFragMan, null, new Object[]{mCtx});
		}

		@Override
		protected void onPostExecute(Void result) {
//			mUi.notifyPMSUpdated();
			mDevicesLoaded = true;
			if(null!=mUiHelper)
			{
				mUiHelper.notifyDevicesLoaded();				
			}
			Log.e(TAG, Arrays.toString((ControllableDevice[]) mAllDevices.toArray(new ControllableDevice[mAllDevices.size()])));
			startAutoUpdate();
			PMSDialogFactory.dismissCurrentDialog();
		}

		@Override
		protected Void doInBackground(HostList... params) {
//			queryControllableDevicesSim();
//			queryControllableDevicesKDF();
			for(HostList hl:params)
			{
				if(null!=hl)
				{
					loadDevices(hl);					
				}
			}
			return null;
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

//		mNetworkingDialog.setMax(hosts.size());
//		showNetworkingDialog();
		
//		new PMSNetworkingInProgressDialogFragment(mCtx).show(mFragMan, null);
		//		String[] macStrings = new String[hosts.size()];
		ArrayList<String> macs = new ArrayList<String>(hosts.keySet());
		
		ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getPMS(mUser, mPass).extendedStatusList(macs);
		if(null==statuses)
		{
			Log.e(TAG, "could not query statuses");
//			dismissNetworkingDialog();
			PMSDialogFactory.dismissCurrentDialog();
			return;
		}
//		mAllDevices = new ArrayList<ControllableDevice>();
		for(ExtendedPMSStatus exStat:statuses)
		{
			Log.e(TAG, "passed mac="+exStat.getMac());
			String passedHost = hosts.get(exStat.getMac());
			Log.e(TAG, "HOST in LIST="+passedHost);
			ControllableDevice cd = new ControllableDevice(mCtx, exStat, passedHost, "schule\\\\sesame_pms", " my_sesame_pms", true);
			//			ControllableDevice cd = new ControllableDevice(getApplicationContext(), host.getValue(), host.getKey(), "schule\\sesame_pms", " my_sesame_pms", true);
			if(cd.isValid())
			{
				mAllDevices.add(cd);
//				mSelection.put(cd.getMac(), false);
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

//		PMSDialogFactory.dismissCurrentDialog();
	}
	
	/**
	 * searches the list of ControllableDevices for the first occurrence of the passed mac address 
	 * @param _mac the mac address of the device that should be retrieved
	 * @return the ControllableDevice associated with the passed mac address
	 */
	public ControllableDevice getDeviceFromMac(String _mac)
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

	public ArrayList<ControllableDevice> getAllDevices()
	{
		return mAllDevices;
	}

	public void handlePowerClick(ControllableDevice cd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPMSUpdated() 
	{
		Log.e(TAG, "%%%%%%%%%%%%%%%notified about pms update");
		if(null!=mUiHelper)
		{
			mUiHelper.notifyPMSUpdated();			
		}
//		mUpdateListener.notifyPMSUpdated();
		
	}
}
