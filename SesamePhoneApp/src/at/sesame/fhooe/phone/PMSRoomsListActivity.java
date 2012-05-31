package at.sesame.fhooe.phone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import at.sesame.fhooe.lib2.NotificationCenter;
import at.sesame.fhooe.lib2.R;
import at.sesame.fhooe.lib2.data.ISesameUpdateListener;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.data.SesameNotification;
import at.sesame.fhooe.lib2.logging.SesameLogger;
import at.sesame.fhooe.lib2.logging.SesameLogger.EntryType;
import at.sesame.fhooe.lib2.logging.export.SesameFileLogExporter;
import at.sesame.fhooe.lib2.logging.export.SesameFileLogExporter.ExportLocation;
import at.sesame.fhooe.lib2.mail.SesameMail;
import at.sesame.fhooe.lib2.mail.SesameMail.NotificationType;
import at.sesame.fhooe.lib2.pms.ComputerRoomInformation;
import at.sesame.fhooe.lib2.pms.PMSRoomListAdapter;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory.DialogType;
import at.sesame.fhooe.lib2.pms.hosts.EDV1Hosts;
import at.sesame.fhooe.lib2.pms.hosts.EDV3Hosts;
import at.sesame.fhooe.lib2.pms.hosts.EDV6Hosts;
import at.sesame.fhooe.lib2.pms.hosts.HostList;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;



public class PMSRoomsListActivity 
extends FragmentActivity 
implements OnItemClickListener, ISesameUpdateListener
{
	private static final String TAG = "PMSRoomListActivity";
	private static final SimpleDateFormat	LOG_FILENAME_DATE_FORMAT		= new SimpleDateFormat("dd_MM_yy_HH_mm");
	private static final int UPDATE_PERIOD = 5000;
	
	private boolean mRepoAvailable = false;
	private boolean mPmsAvailable = false;
	private boolean mRepoDataNew = false;
//	private PMSClientFragment mPMSClientFrag;
	//	private ArrayList<ComputerRoomInformation> mInfos;
	private PMSRoomListAdapter mAdapter;
	private ArrayList<ComputerRoomInformation> mInfos = new ArrayList<ComputerRoomInformation>();

	private boolean mShowNotification = false;
	
	private Date mLastEnergyUpdate;
//	private Date mLastEnergyUpdateTimeStamp;
	private Date mLastPmsUpdate;
	
	private int mNumHoursBeforeRepoFailNotification = 3;
//	private int mNumMinutesBeforePmsFailNotification = 5;
	
	private int mPmsUpdateFailCount = 0;
	private int mMaxPmsUpdateFailCount = 5;
	
	private int mEnergyUpdateFailCount = 0;
	private int mMaxEnergyUpdateFailCount = 5;
	
	private Timer mHeartBeatTimer;
	private static final long HEARTBEAT_PERIOD = 3600000;

	private ListView mList;

//	private PmsHelper mPmsHelper;
//	private ArrayList<ControllableDevice> mAllDevs;

	private HostList mEdv1Hosts = new EDV1Hosts();
	private HostList mEdv3Hosts = new EDV3Hosts();
	private HostList mEdv6Hosts = new EDV6Hosts();
	
	private Timer mUpdateTimer;
	private Timer mShutdownTimer;
	private int mShutdownHour = 19;
	
//	private SesameDataCache mCache;
	private SesameFileLogExporter mExporter;
	private static final long LOG_EXPORT_PERIOD = 10000;

	public void onCreate(Bundle _savedInstanceState)
	{
		super.onCreate(_savedInstanceState);
		String fileName = "sesameLog" + LOG_FILENAME_DATE_FORMAT.format(new Date()) + ".csv";
		mExporter = new SesameFileLogExporter(PMSRoomsListActivity.this, ExportLocation.EXT_PUB_DIR,
				fileName);
		SesameLogger.setExporter(mExporter);

		SesameLogger.startContinuousExport(LOG_EXPORT_PERIOD);
		if(!checkConnectivity())
		{
			Toast.makeText(this, "Netzwerk nicht verbunden.\n Anwendung wird beendet.\n Bitte stellen sie eine Internetverbindung her und starten sie die Anwendung erneut", Toast.LENGTH_LONG).show();
			new Timer().schedule(new ShutdownTask(), 5000);
		}
		else
		{	
			
			new CreationTask().execute();			
		}
	}
	private void stopShutdownTask()
	{
		if(null!=mShutdownTimer)
		{
			mShutdownTimer.cancel();
			mShutdownTimer.purge();
		}
	}
	private void startShutdownTask()
	{
		stopShutdownTask();
		GregorianCalendar shutdownCal = new GregorianCalendar();
		shutdownCal.set(Calendar.HOUR_OF_DAY, mShutdownHour);
		shutdownCal.set(Calendar.MINUTE, 30);
		shutdownCal.set(Calendar.SECOND, 0);
		SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "automatic shutdown scheduled for: "+shutdownCal.getTime().toString());
		mShutdownTimer = new Timer("shutdown timer");
		mShutdownTimer.schedule(new ShutdownTask(), shutdownCal.getTime());
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
	
	private class ShutdownTask extends TimerTask
	{

		@Override
		public void run() 
		{
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "automatic shutdown");
			finish();
		}
		
	}
	
	
	
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
//		SesameDataCache.cleanUp();
	}

	private class CreationTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) 
		{

			if(null==SesameDataCache.getInstance())
			{
				SesameDataCache.createInstance(PMSRoomsListActivity.this);				
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			mInfos.add(new ComputerRoomInformation(getString(R.string.global_Room1_name), SesameDataCache.EDV1_PLACE, 0, 0, false));
			mInfos.add(new ComputerRoomInformation(getString(R.string.global_Room3_name), SesameDataCache.EDV3_PLACE, 0, 0, false));
			mInfos.add(new ComputerRoomInformation(getString(R.string.global_Room6_name), SesameDataCache.EDV6_PLACE, 0, 0, false));
			
			setContentView(R.layout.pms_list_layout);
			mAdapter = new PMSRoomListAdapter(PMSRoomsListActivity.this, 1, mInfos);
			
			
			mList = (ListView)findViewById(R.id.pms_list_layout_list);
			mList.setAdapter(mAdapter);
			mList.setOnItemClickListener(PMSRoomsListActivity.this);

			// set styling
			mList.setDivider(new ColorDrawable(Color.WHITE));
			mList.setDividerHeight(1);
			mList.setBackgroundResource(R.drawable.app_background);
			
			SesameDataCache.getInstance().registerSesameUpdateListener(PMSRoomsListActivity.this);
			PMSDialogFactory.dismissCurrentDialog();
			updateComputerRoomInfos();
			NotificationCenter.start(PMSRoomsListActivity.this, PMSRoomsListActivity.class);
			startHeartBeat();
			startShutdownTask();
		}

		@Override
		protected void onPreExecute() {
			PMSDialogFactory.showDialog(DialogType.NETWORKING_IN_PROGRESS, getSupportFragmentManager(), null,
					new Object[] { PMSRoomsListActivity.this });
		}

	}
	
	private void startHeartBeat()
	{
		stopHeartBeat();
		mHeartBeatTimer = new Timer("heartbeat");
		mHeartBeatTimer.schedule(new HeartBeatTask(), 60000, HEARTBEAT_PERIOD);
	}
	
	private void stopHeartBeat()
	{
		if(null!=mHeartBeatTimer)
		{
			mHeartBeatTimer.cancel();
			mHeartBeatTimer.purge();
		}
	}
	
	
	
	private class HeartBeatTask extends TimerTask
	{
		@Override
		public void run()
		{
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "heartbeat");
			Log.i(TAG, "################HEARTBEAT################");
			SesameMail mail = new SesameMail();
			try {
				mail.addAttachment(mExporter.getMailLogFilePath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StringBuffer bodyBuffer = new StringBuffer();
			
			bodyBuffer.append("PMS available: "+mPmsAvailable);
			bodyBuffer.append("\nRepo available: "+mRepoAvailable);
			bodyBuffer.append("\nRepo data new: "+mRepoDataNew);
			
			if(null!=mLastPmsUpdate)
			{
				bodyBuffer.append("\nlast pms update:");
				bodyBuffer.append(mLastPmsUpdate.toString());				
			}
			else
			{
				bodyBuffer.append("\nno pms update yet...");
			}
			
			if(null!=mLastEnergyUpdate)
			{
				bodyBuffer.append("\nlast energy update:");
				bodyBuffer.append(mLastEnergyUpdate.toString());				
			}
			else
			{
				bodyBuffer.append("\nno energy update yet...");
			}
			bodyBuffer.append("\nlast meter reading:");
			try
			{
				bodyBuffer.append(SesameDataCache.getInstance().getLastEnergyDataTimeStamp().toString());
				
			}
			catch(Exception e)
			{
				
			}
			
			boolean res = mail.send(SesameDataCache.getInstance().getConfigData(), bodyBuffer.toString());
		}
	}

//	private void loadDevices() 
//	{
//		Log.i(TAG, "loading devices");
//		HostList hl = new HostList();
//		hl.addAll(mEdv1Hosts.getHosts());
//		hl.addAll(mEdv3Hosts.getHosts());
//		hl.addAll(mEdv6Hosts.getHosts());
//		mPmsHelper = new PmsHelper(mCtx, mFragMan, this, hl, null, null);
//	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View v = inflater.inflate(R.layout.pms_list_layout, null);
//		mList = (ListView)v.findViewById(R.id.pms_list_layout_list);
//		mList.setAdapter(mAdapter);
//		mList.setOnItemClickListener(this);
//
//		// style seperator
//		mList.setDivider(new ColorDrawable(Color.WHITE));
//		mList.setDividerHeight(1);
//
//		return v;
//	}


	private void updateComputerRoomInfos()
	{
		if(null==SesameDataCache.getInstance()||null==mAdapter)
		{
			return;
		}
//		Log.i(TAG, "updating computer room infos");
		final int[] activeInactive1 = getActiveAndInactiveDevCount(mEdv1Hosts);
		final int[] activeInactive3 = getActiveAndInactiveDevCount(mEdv3Hosts);
		final int[] activeInactive6 = getActiveAndInactiveDevCount(mEdv6Hosts);
		
		final boolean dirty1 = containsDirtyDevices(mEdv1Hosts);
		final boolean dirty3 = containsDirtyDevices(mEdv3Hosts);
		final boolean dirty6 = containsDirtyDevices(mEdv6Hosts);
		
		final int num1Notifications = getNumNotifications(mEdv1Hosts);
		final int num3Notifications = getNumNotifications(mEdv3Hosts);
		final int num6Notifications = getNumNotifications(mEdv6Hosts);
		
		runOnUiThread(new Runnable() 
		{	
			@Override
			public void run() 
			{
//				Log.i(TAG, "onUiThread");
				if(mInfos.size()!=3)
				{
//					Log.i(TAG, "size of list not 3 ==> creating new");
					ComputerRoomInformation cri1 = new ComputerRoomInformation(getString(R.string.global_Room1_name), SesameDataCache.EDV1_PLACE, activeInactive1[1], activeInactive1[0], false);
					ComputerRoomInformation cri3 = new ComputerRoomInformation(getString(R.string.global_Room3_name), SesameDataCache.EDV3_PLACE, activeInactive3[1], activeInactive3[0], false);
					ComputerRoomInformation cri6 = new ComputerRoomInformation(getString(R.string.global_Room6_name), SesameDataCache.EDV6_PLACE, activeInactive6[1], activeInactive6[0], false);
					mInfos.add(cri1);
					mInfos.add(cri3);
					mInfos.add(cri6);
				}
				else
				{
//					Log.i(TAG, "size of list == 3 ==> updating values");
					ComputerRoomInformation cri1 = mInfos.get(0);
					cri1.setNumActiveComputers(activeInactive1[0]);
					cri1.setNumIdleComputers(activeInactive1[1]);
					cri1.setDirty(dirty1);
					cri1.setNumNotifications(num1Notifications);

					ComputerRoomInformation cri3 = mInfos.get(1);
					cri3.setNumActiveComputers(activeInactive3[0]);
					cri3.setNumIdleComputers(activeInactive3[1]);
					cri3.setDirty(dirty3);
					cri3.setNumNotifications(num3Notifications);

					ComputerRoomInformation cri6 = mInfos.get(2);
					cri6.setNumActiveComputers(activeInactive6[0]);
					cri6.setNumIdleComputers(activeInactive6[1]);
					cri6.setDirty(dirty6);
					cri6.setNumNotifications(num6Notifications);
				}
				mAdapter.notifyDataSetChanged();
			}
		});

	}
	
	private int getNumNotifications(HostList _hosts)
	{
		int res = 0;
	
		for(String mac:_hosts.getMacList())
		{
			ControllableDevice cd = SesameDataCache.getInstance().getDeviceByMac(mac);
			if(null!=cd)
			{
				if(cd.getIdleSinceMinutes()>=ControllableDevice.IDLE_NOTIFICATION_THRESHOLD)
				{
					res++;
				}
			}
		}
		return res;
	}
	
	private boolean containsDirtyDevices(HostList _hosts)
	{
		for(String mac:_hosts.getMacList())
		{
			ControllableDevice cd = SesameDataCache.getInstance().getDeviceByMac(mac);
			if(null!=cd)
			{
				if(cd.isDirty())
				{
					return true;
				}
			}
		}
		return false;
	}

	private int[]getActiveAndInactiveDevCount(HostList _hosts)
	{
		int active = 0;
		int inactive = 0;

		//		ArrayList<ControllableDevice> devices = mPmsHelper.get
		for(String mac:_hosts.getMacList())
		{
			ControllableDevice cd = SesameDataCache.getInstance().getDeviceByMac(mac);
			if(null!=cd)
			{
				if(cd.isAlive())
				{
					active++;
				}
				else
				{
					inactive++;
				}
			}
		}

		return new int[]{active, inactive};
	}

//	private ArrayList<ComputerRoomInformation> createDummyInfos()
//	{
//		ArrayList<ComputerRoomInformation> infos = new ArrayList<ComputerRoomInformation>();
//		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room1_name), 10, 5, false));
//		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room3_name), 5, 12, false));
//		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room6_name), 6, 15, false));
//		return infos;
//	}

	//	@Override
	//	public void onListItemClick(ListView l, View v, int position, long id) {
	//		// TODO Auto-generated method stub
	//		ComputerRoomInformation cri = mAdapter.getItem(position);
	//		Log.e(TAG, cri.toString());
	////		FragmentManager fm = getFragmentManager();
	////		FragmentTransaction ft = fm.beginTransaction();
	////		String tag;
	//		String roomName = cri.getRoomName();
	//		if(roomName.equals(mCtx.getString(R.string.global_Room1_name)))
	//		{
	//			if(mShowNotification)
	//			{
	//				Log.e(TAG, "notification");
	//				new PMS_MockDetailFragment(R.drawable.ic_edv1_pms_detail_notification).show(getFragmentManager(), roomName);
	//			}
	//			else
	//			{
	//				Log.e(TAG, "no notification");
	//				new PMS_MockDetailFragment(R.drawable.ic_edv1_pms_detail_no_notification).show(getFragmentManager(), roomName);
	//			}
	////			new PMS_DetailFragment(mCtx, new EDV1Hosts()).show(getFragmentManager(), roomName);
	////			tag = RoomName.EDV_1.name();
	////			ft.remove(fm.findFragmentByTag(tag));
	////			ft.add(new PMS_DetailFragment(mCtx, new EDV1Hosts()), tag);
	//		}
	//		else if(roomName.equals(mCtx.getString(R.string.global_Room3_name)))
	//		{
	//			new PMS_MockDetailFragment(R.drawable.ic_edv3_pms_detail_no_notification).show(getFragmentManager(), roomName);
	////			new PMS_DetailFragment(mCtx, new EDV3Hosts()).show(getFragmentManager(), roomName);
	////			tag = RoomName.EDV_3.name();
	////			ft.remove(fm.findFragmentByTag(tag));
	////			ft.add(new PMS_DetailFragment(mCtx, new EDV3Hosts()), tag);
	//		}
	//		else if(roomName.equals(mCtx.getString(R.string.global_Room6_name)))
	//		{
	//			new PMS_MockDetailFragment(R.drawable.ic_edv6_pms_detail_no_notification).show(getFragmentManager(), roomName);
	////			new PMS_DetailFragment(mCtx, new EDV6Hosts()).show(getFragmentManager(), roomName);
	////			tag = RoomName.EDV_6.name();
	////			ft.remove(fm.findFragmentByTag(tag));
	////			ft.add(new PMS_DetailFragment(mCtx, new EDV6Hosts()), tag);
	//		
	//		}
	////		ft.commit();
	//	}

	public boolean isShowNotification() {
		return mShowNotification;
	}

	public void setShowNotification(boolean _showNotification) {
//		mAdapter.getItem(0).setShowNotification(_showNotification);
		mShowNotification = _showNotification;
		//		mAdapter.notifyDataSetChanged();
		//		getListView().invalidate();
		//		mAdapter.getView().invalidate();
		//		getListView().postInvalidate();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mAdapter.notifyDataSetChanged();
			}
		});


	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		ComputerRoomInformation cri = mAdapter.getItem(arg2);
		
		Intent i = new Intent(this, PMSClientActivity.class);
		i.putExtra(PMSClientActivity.COMPUTER_ROOM_INFO_KEY, cri);
		startActivity(i);
//		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.sesame_phone_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.today_item:
			startActivity(new Intent(this, TodayChartActivity.class));
//			finish();
			return true;
		case R.id.week_item:
			startActivity(new Intent(this, ComparisonActivity.class));
//			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	

//	@Override
//	public void notifyPMSUpdated() 
//	{
////		Log.i(TAG, "notified about pms update");
//		updateComputerRoomInfos();	
////		if(null!=mPMSClientFrag)
////		{
////			mPMSClientFrag.startSingleUiUpdate();
////		}
//	}
	
	public class UpdateTask extends TimerTask
	{
		@Override
		public void run() 
		{
			updateComputerRoomInfos();	
		}	
	}
	
	private void stopUpdates()
	{
		if(null!=mUpdateTimer)
		{
			mUpdateTimer.cancel();
			mUpdateTimer.purge();
		}
	}
	
	private void startUpdates()
	{
		stopUpdates();
		mUpdateTimer = new Timer();
		mUpdateTimer.schedule(new UpdateTask(), 0, UPDATE_PERIOD);
	}

	@Override
	public void onDestroy() 
	{
		stopUpdates();
		SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "application closed");
		SesameLogger.export();
		SesameMail mail = new SesameMail();
		try {
			mail.addAttachment(mExporter.getMailLogFilePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		mail.setBody("app was shut down");
		
		try {
			mail.send(SesameDataCache.getInstance().getConfigData(), "app was shut down");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SesameLogger.stopContinuousExporting();
		SesameDataCache.cleanUp();
//		if(null!=mPMSClientFrag)
//		{
//			mPMSClientFrag.dismiss();
//		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
//		stopUpdates();
		if(null!=SesameDataCache.getInstance())
		{
			SesameDataCache.getInstance().unregisterSesameUpdateListener(this);			
		}
		super.onPause();
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		
		if(null!=SesameDataCache.getInstance())
		{
			SesameDataCache.getInstance().registerSesameUpdateListener(PMSRoomsListActivity.this);
		}
	}

	public void notifyAboutNotifications(final ArrayList<SesameNotification> _notifications) 
	{
//		Log.i(TAG, "notified about notification:"+sn.getMac());
		//				ControllableDevice cd = mPmsHelper.getDeviceByMac(sn.getMac());
		//		Log.i(TAG, "---------------------------------------");
		//		Log.i(TAG, Arrays.toString((String[]) mEdv1Hosts.getMacList().toArray(new String[mEdv1Hosts.getMacList().size()])));
		//		Log.i(TAG, "---------------------------------------");
		//		Log.i(TAG, Arrays.toString((String[]) mEdv3Hosts.getMacList().toArray(new String[mEdv3Hosts.getMacList().size()])));
		//		Log.i(TAG, "---------------------------------------");
		//		Log.i(TAG, Arrays.toString((String[]) mEdv6Hosts.getMacList().toArray(new String[mEdv6Hosts.getMacList().size()])));
		//		Log.i(TAG, "---------------------------------------");
		boolean found = false;
		int numEdv1Notifications = 0;
		int numEdv3Notifications = 0;
		int numEdv6Notifications = 0;
		
		for(SesameNotification sn:_notifications)
		{
			if(isMacInList(mEdv1Hosts, sn.getMac()))
			{
				numEdv1Notifications++;
			}
			else if(isMacInList(mEdv3Hosts, sn.getMac()))
			{
				numEdv3Notifications++;
			}
			else if(isMacInList(mEdv6Hosts, sn.getMac()))
			{
				numEdv6Notifications++;
			}
		}
		
		final int finalEdv1Notifications = numEdv1Notifications;
		final int finalEdv3Notifications = numEdv3Notifications;
		final int finalEdv6Notifications = numEdv6Notifications;
		runOnUiThread(new Runnable() 
		{	
			@Override
			public void run() 
			{
				mAdapter.getItem(0).setNumNotifications(finalEdv1Notifications);
				mAdapter.getItem(1).setNumNotifications(finalEdv3Notifications);
				mAdapter.getItem(2).setNumNotifications(finalEdv6Notifications);
			}
		});	
//		int idx = -1;
//		for(String mac:mEdv1Hosts.getMacList())
//		{
//			if(mac.equals(sn.getMac()))
//			{
//				idx = 0;
//				found = true;
//				break;
//			}
//		}
//		if(!found)
//		{
//			for(String mac:mEdv3Hosts.getMacList())
//			{
//				if(mac.equals(sn.getMac()))
//				{
////					cri = mAdapter.getItem(1);
//					idx = 1;
//					found = true;
//					break;
//				}
//			}					
//		}
//		if(!found)
//		{
//			for(String mac:mEdv6Hosts.getMacList())
//			{
//				if(mac.equals(sn.getMac()))
//				{
////					cri = mAdapter.getItem(2);
//					idx = 2;
//					found = true;
//					break;
//				}
//			}					
//		}
//		if(found)
//		{
//			final int finalIdx = idx;
//			mUiHandler.post(new Runnable() 
//			{	
//				@Override
//				public void run() 
//				{
//					mAdapter.getItem(finalIdx).setShowNotification(true);	
//				}
//			});				
//		}
//		else
//		{
//			Log.e(TAG, sn.getMac()+" not found in list");
//		}
	}

	private boolean isMacInList(HostList _hl, String _mac)
	{
		for(String mac:_hl.getMacList())
		{
			if(mac.equals(_mac))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void notifyPmsUpdate(boolean _success) 
	{
		Log.e(TAG, "notified about pms update:"+_success);
		
		if(_success)
		{
			mPmsUpdateFailCount = 0;
			mLastPmsUpdate = new Date();
			mPmsAvailable = true;
			updateComputerRoomInfos();
		}
		else
		{
			mPmsUpdateFailCount++;
		}
		if(mPmsUpdateFailCount>=mMaxPmsUpdateFailCount)
		{
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "connection to pms potentially lost...");
			if(mPmsAvailable)
			{
				new SesameMail().send(SesameDataCache.getInstance().getConfigData(), NotificationType.PMS_FAILED);
			}
			mPmsAvailable = false;
//			if(shouldNotify(mLastPmsFailNotification))
//			{
//				new SesameMail().send(mDataCache.getConfigData(), NotificationType.PMS_FAILED);
//				mLastPmsFailNotification = new Date();
//			}
		}
		
	}

	@Override
	public void notifyEnergyUpdate(boolean _success) 
	{
		// TODO Auto-generated method stub
		Log.e(TAG, "notified about energyUpdate:"+_success);
		if(_success)
		{
			mRepoAvailable = true;
			mEnergyUpdateFailCount = 0;
			mLastEnergyUpdate = new Date();
			
		}
		else
		{
			mEnergyUpdateFailCount++;
		}
		
		if(mEnergyUpdateFailCount>=mMaxEnergyUpdateFailCount)
		{
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "connection to repository lost");
			if(mRepoAvailable)
			{
				new SesameMail().send(SesameDataCache.getInstance().getConfigData(), NotificationType.REPO_FAILED);
			}
			mRepoAvailable = false;
//			if(shouldNotify(mLastRepoConnectionLostNotification))
//			{
//				new SesameMail().send(mDataCache.getConfigData(), NotificationType.REPO_FAILED);
//				mLastRepoConnectionLostNotification = new Date();
//			}
		}
		
		long lastUpdateMillis = SesameDataCache.getInstance().getLastEnergyDataTimeStamp().getTime();
		long nowMillis = new Date().getTime();
		long diff = nowMillis-lastUpdateMillis;
		if(diff<0)
		{
			Log.e(TAG, "error computing diff for energy update times");
		}
		
		if(diff>3600000*mNumHoursBeforeRepoFailNotification)
		{
			SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "last update of energy data is older than "+mNumHoursBeforeRepoFailNotification+" hours");
			if(mRepoDataNew)
			{
				new SesameMail().send(SesameDataCache.getInstance().getConfigData(), NotificationType.REPO_OLD);
			}
			mRepoDataNew = false;
//			if(shouldNotify(mLastRepoOldNotification))
//			{
//				new SesameMail().send(mDataCache.getConfigData(), NotificationType.REPO_OLD);
//				mLastRepoOldNotification = new Date();
//			}
		}
		else
		{
			mRepoDataNew = true;
		}
		
	}

	@Override
	public void notifyConnectivityLoss() {
		// TODO Auto-generated method stub
		Log.e(TAG, "notified about connectivity loss");
		runOnUiThread(new Runnable()
		{	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SesameLogger.log(EntryType.APPLICATION_INFO, TAG, "connection lost");
				Toast.makeText(PMSRoomsListActivity.this, getString(R.string.connection_loss_message), Toast.LENGTH_LONG).show();
//				mDataCache.cleanUp();
			}
		});
	}
	

}
