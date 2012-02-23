package at.sesame.fhooe.midsd.hd.pms;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import at.sesame.fhooe.midsd.R;
import at.sesame.fhooe.midsd.hd.pms.hosts.EDV1Hosts;
import at.sesame.fhooe.midsd.hd.pms.hosts.EDV3Hosts;
import at.sesame.fhooe.midsd.hd.pms.hosts.EDV6Hosts;

public class PMSFragment 
extends ListFragment 
{
	private static final String TAG = "PMSFragment";
//	private ArrayList<ComputerRoomInformation> mInfos;
	private PMSListAdapter mAdapter;
	private Context mCtx;
	
	public PMSFragment(Context _ctx)
	{
		mCtx = _ctx;
		mAdapter = new PMSListAdapter(mCtx, 1, createDummyInfos());
		setListAdapter(mAdapter);
	}
	
	private ArrayList<ComputerRoomInformation> createDummyInfos()
	{
		ArrayList<ComputerRoomInformation> infos = new ArrayList<ComputerRoomInformation>();
		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room1_name), 10, 5));
		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room3_name), 3, 52));
		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room6_name), 6, 15));
		return infos;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		ComputerRoomInformation cri = mAdapter.getItem(position);
		Log.e(TAG, cri.toString());
//		FragmentManager fm = getFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		String tag;
		String roomName = cri.getRoomName();
		if(roomName.equals(mCtx.getString(R.string.global_Room1_name)))
		{

			new PMS_DetailFragment(mCtx, new EDV1Hosts()).show(getFragmentManager(), roomName);
//			tag = RoomName.EDV_1.name();
//			ft.remove(fm.findFragmentByTag(tag));
//			ft.add(new PMS_DetailFragment(mCtx, new EDV1Hosts()), tag);
		}
		else if(roomName.equals(mCtx.getString(R.string.global_Room3_name)))
		{
			new PMS_DetailFragment(mCtx, new EDV3Hosts()).show(getFragmentManager(), roomName);
//			tag = RoomName.EDV_3.name();
//			ft.remove(fm.findFragmentByTag(tag));
//			ft.add(new PMS_DetailFragment(mCtx, new EDV3Hosts()), tag);
		}
		else if(roomName.equals(mCtx.getString(R.string.global_Room6_name)))
		{
			new PMS_DetailFragment(mCtx, new EDV6Hosts()).show(getFragmentManager(), roomName);
//			tag = RoomName.EDV_6.name();
//			ft.remove(fm.findFragmentByTag(tag));
//			ft.add(new PMS_DetailFragment(mCtx, new EDV6Hosts()), tag);
		
		}
//		ft.commit();
	}
	
	
	
}
