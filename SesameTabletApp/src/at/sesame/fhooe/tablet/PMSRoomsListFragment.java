package at.sesame.fhooe.tablet;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import at.sesame.fhooe.lib2.R;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.data.SesameNotification;
import at.sesame.fhooe.lib2.pms.ComputerRoomInformation;
import at.sesame.fhooe.lib2.pms.IPMSUpdateListener;
import at.sesame.fhooe.lib2.pms.hosts.EDV1Hosts;
import at.sesame.fhooe.lib2.pms.hosts.EDV3Hosts;
import at.sesame.fhooe.lib2.pms.hosts.EDV6Hosts;
import at.sesame.fhooe.lib2.pms.hosts.HostList;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;


public class PMSRoomsListFragment 
extends Fragment implements IPMSUpdateListener, OnItemClickListener
{
	private static final String TAG = "PMSRoomListFragment";
	private static final int UPDATE_PERIOD = 5000;
	private PMSClientFragment mPMSClientFrag;
	//	private ArrayList<ComputerRoomInformation> mInfos;
	private PMSRoomListAdapter mAdapter;
	private Context mCtx;
	private ArrayList<ComputerRoomInformation> mInfos = new ArrayList<ComputerRoomInformation>();

	private boolean mShowNotification = false;
	private Handler mUiHandler;

	private ListView mList;
	private FragmentManager mFragMan;

//	private PmsHelper mPmsHelper;
//	private ArrayList<ControllableDevice> mAllDevs;

	private HostList mEdv1Hosts = new EDV1Hosts();
	private HostList mEdv3Hosts = new EDV3Hosts();
	private HostList mEdv6Hosts = new EDV6Hosts();
	
	private Timer mUpdateTimer;

	public PMSRoomsListFragment(Context _ctx, Handler _uiHandler, FragmentManager _fm)
	{
		mCtx = _ctx;

		mUiHandler = _uiHandler;
		mInfos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room1_name), 0, 0, false));
		mInfos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room3_name), 0, 0, false));
		mInfos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room6_name), 0, 0, false));
		//		mInfos = createDummyInfos();
		mFragMan = _fm;
		//		mPMSClientFrag = new PMSClientFragment(_ctx, _fm, mUiHandler);
//		loadDevices();
		mAdapter = new PMSRoomListAdapter(mCtx, 1, mInfos);


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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pms_list_layout, null);
		mList = (ListView)v.findViewById(R.id.pms_list_layout_list);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(this);

		// style seperator
		mList.setDivider(new ColorDrawable(Color.WHITE));
		mList.setDividerHeight(1);

		return v;
	}


	private void updateComputerRoomInfos()
	{
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
		
		mUiHandler.post(new Runnable() 
		{	
			@Override
			public void run() 
			{
//				Log.i(TAG, "onUiThread");
				if(mInfos.size()!=3)
				{
//					Log.i(TAG, "size of list not 3 ==> creating new");
					ComputerRoomInformation cri1 = new ComputerRoomInformation(mCtx.getString(R.string.global_Room1_name), activeInactive1[1], activeInactive1[0], false);
					ComputerRoomInformation cri3 = new ComputerRoomInformation(mCtx.getString(R.string.global_Room3_name), activeInactive3[1], activeInactive3[0], false);
					ComputerRoomInformation cri6 = new ComputerRoomInformation(mCtx.getString(R.string.global_Room6_name), activeInactive6[1], activeInactive6[0], false);
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
			ControllableDevice cd = SesameDataCache.getInstance(null).getDeviceByMac(mac);
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
			ControllableDevice cd = SesameDataCache.getInstance(null).getDeviceByMac(mac);
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
			ControllableDevice cd = SesameDataCache.getInstance(null).getDeviceByMac(mac);
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

	private ArrayList<ComputerRoomInformation> createDummyInfos()
	{
		ArrayList<ComputerRoomInformation> infos = new ArrayList<ComputerRoomInformation>();
		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room1_name), 10, 5, false));
		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room3_name), 5, 12, false));
		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room6_name), 6, 15, false));
		return infos;
	}

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

		mUiHandler.post(new Runnable() {

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
//		cri.setShowNotification(false);
//		Log.e(TAG, cri.toString());
		//		FragmentManager fm = getFragmentManager();
		//		FragmentTransaction ft = fm.beginTransaction();
		//		String tag;
		String roomName = cri.getRoomName();
		if(roomName.equals(mCtx.getString(R.string.global_Room1_name)))
		{
			//			new PMSClientFragment(mCtx, mFragMan, mUiHandler, new SimulationHosts()).show(mFragMan, null);
			mPMSClientFrag = new PMSClientFragment(mCtx, mFragMan, mUiHandler, mCtx.getString(R.string.global_Room1_name), new EDV1Hosts());
//			if(mShowNotification)
//			{
//				Log.e(TAG, "notification");
//				//				new PMS_MockDetailFragment(R.drawable.ic_edv1_pms_detail_notification).show(getFragmentManager(), roomName);
//			}
//			else
//			{
//				Log.e(TAG, "no notification");
//				//				new PMS_MockDetailFragment(R.drawable.ic_edv1_pms_detail_no_notification).show(getFragmentManager(), roomName);
//			}
			//			new PMS_DetailFragment(mCtx, new EDV1Hosts()).show(getFragmentManager(), roomName);
			//			tag = RoomName.EDV_1.name();
			//			ft.remove(fm.findFragmentByTag(tag));
			//			ft.add(new PMS_DetailFragment(mCtx, new EDV1Hosts()), tag);
		}
		else if(roomName.equals(mCtx.getString(R.string.global_Room3_name)))
		{
			//			new PMSClientFragment(mCtx, mFragMan, mUiHandler, new SimulationHosts()).show(mFragMan, null);
			mPMSClientFrag = new PMSClientFragment(mCtx, mFragMan, mUiHandler, mCtx.getString(R.string.global_Room3_name), new EDV3Hosts());
			//			new PMS_MockDetailFragment(R.drawable.ic_edv3_pms_detail_no_notification).show(getFragmentManager(), roomName);
			//			new PMS_DetailFragment(mCtx, new EDV3Hosts()).show(getFragmentManager(), roomName);
			//			tag = RoomName.EDV_3.name();
			//			ft.remove(fm.findFragmentByTag(tag));
			//			ft.add(new PMS_DetailFragment(mCtx, new EDV3Hosts()), tag);
		}
		else if(roomName.equals(mCtx.getString(R.string.global_Room6_name)))
		{
			//			new PMSClientFragment(mCtx, mFragMan, mUiHandler, new SimulationHosts()).show(mFragMan, null);
			mPMSClientFrag = new PMSClientFragment(mCtx, mFragMan, mUiHandler, mCtx.getString(R.string.global_Room6_name), new EDV6Hosts());
			//			new PMS_MockDetailFragment(R.drawable.ic_edv6_pms_detail_no_notification).show(getFragmentManager(), roomName);
			//			new PMS_DetailFragment(mCtx, new EDV6Hosts()).show(getFragmentManager(), roomName);
			//			tag = RoomName.EDV_6.name();
			//			ft.remove(fm.findFragmentByTag(tag));
			//			ft.add(new PMS_DetailFragment(mCtx, new EDV6Hosts()), tag);

		}
		if(null!=mPMSClientFrag)
		{
			mPMSClientFrag.show(mFragMan, null);			
		}

		//		mPMSClientFrag.show(getFragmentManager(), null);

	}
	

	@Override
	public void notifyPMSUpdated() 
	{
//		Log.i(TAG, "notified about pms update");
		updateComputerRoomInfos();	
		if(null!=mPMSClientFrag)
		{
			mPMSClientFrag.startSingleUiUpdate();
		}
	}
	
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
		super.onDestroy();
	}



	@Override
	public void onPause() {
		stopUpdates();
		super.onPause();
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		startUpdates();
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
		mUiHandler.post(new Runnable() 
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
	

}
