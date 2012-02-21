package at.sesame.fhooe.midsd.hd.pms;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import at.sesame.fhooe.lib.pms.PMSProvider;
import at.sesame.fhooe.lib.pms.model.ControllableDevice;
import at.sesame.fhooe.lib.pms.model.ExtendedPMSStatus;
import at.sesame.fhooe.midsd.R;
import at.sesame.fhooe.midsd.hd.pms.hosts.HostList;


public class PMS_DetailFragment 
extends DialogFragment
{	
	private ListAdapter mActiveClientListAdapter;
	private ListAdapter mInactiveClientListAdapter;

	private Context mCtx;
	private HostList mHostList;

	public PMS_DetailFragment(Context _ctx, HostList _hosts)
	{
		mCtx = _ctx;
		mHostList = _hosts;
		loadListEntries();
	}

	private void loadListEntries() {
		//		ArrayList<ExtendedPMSStatus>statuses = PMSProvider.getPMS().extendedStatusList(PMSProvider.getDeviceList());
//		ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getExtendedPMSStatusList(mHostList.getMacList());
		ArrayList<ExtendedPMSStatus> statuses = PMSProvider.getExtendedPMSStatusList(PMSProvider.getDeviceList());
		ArrayList<ControllableDevice> allDevices = new ArrayList<ControllableDevice>();

		for(ExtendedPMSStatus exStat:statuses)
		{
			ControllableDevice cd = new ControllableDevice(mCtx, exStat, mHostList.getHostNameForMac(exStat.getMac()), "schule\\\\sesame_pms", " my_sesame_pms", true);
			//			ControllableDevice cd = new ControllableDevice(getApplicationContext(), host.getValue(), host.getKey(), "schule\\sesame_pms", " my_sesame_pms", true);
			if(cd.isValid())
			{
				allDevices.add(cd);
				//				mSelection.put(cd.getMac(), false);
			}
			//			PMSClientActivity.this.mNetworkingDialog.incrementProgressBy(1);
		}

		ArrayList<IListEntry> activeDevEntries = new ArrayList<IListEntry>();
		ArrayList<IListEntry> inactiveEntries = new ArrayList<IListEntry>();

		for(ControllableDevice cd:allDevices)
		{
			if(cd.isAlive())
			{
				activeDevEntries.add(new ControllableDeviceListEntry(cd));
			}
			else inactiveEntries.add(new ControllableDeviceListEntry(cd));
		}

		mActiveClientListAdapter = new ControllableDeviceAdapter(mCtx, activeDevEntries);
		mInactiveClientListAdapter = new ControllableDeviceAdapter(mCtx, inactiveEntries);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.pms_detail_fragment_layout, null);
		ListView active = (ListView)v.findViewById(R.id.activeClientList);
		ListView inactive = (ListView)v.findViewById(R.id.inactiveClientList);

		active.setAdapter(mActiveClientListAdapter);
		inactive.setAdapter(mInactiveClientListAdapter);
		return v;
	}



}
