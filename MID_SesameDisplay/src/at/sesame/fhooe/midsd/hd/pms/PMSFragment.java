package at.sesame.fhooe.midsd.hd.pms;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import at.sesame.fhooe.midsd.R;

public class PMSFragment 
extends Fragment implements OnItemClickListener
{
	private static final String TAG = "PMSFragment";
//	private ArrayList<ComputerRoomInformation> mInfos;
	private PMSListAdapter mAdapter;
	private Context mCtx;
	private ArrayList<ComputerRoomInformation> mInfos;
	
	private boolean mShowNotification = false;
	private Handler mUiHandler;
	
	private ListView mList;
	
	public PMSFragment(Context _ctx, Handler _uiHandler)
	{
		mCtx = _ctx;
		mUiHandler = _uiHandler;
		mInfos = createDummyInfos();

		mAdapter = new PMSListAdapter(mCtx, 1, mInfos);

		
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pms_list_layout, null);
		mList = (ListView)v.findViewById(R.id.pms_list_layout_list);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(this);
		return v;
	}



	private ArrayList<ComputerRoomInformation> createDummyInfos()
	{
		ArrayList<ComputerRoomInformation> infos = new ArrayList<ComputerRoomInformation>();
		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room1_name), 10, 5));
		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room3_name), 5, 12));
		infos.add(new ComputerRoomInformation(mCtx.getString(R.string.global_Room6_name), 6, 15));
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
		mAdapter.getItem(0).setShowNotification(_showNotification);
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
		Log.e(TAG, cri.toString());
//		FragmentManager fm = getFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		String tag;
		String roomName = cri.getRoomName();
		if(roomName.equals(mCtx.getString(R.string.global_Room1_name)))
		{
			if(mShowNotification)
			{
				Log.e(TAG, "notification");
				new PMS_MockDetailFragment(R.drawable.ic_edv1_pms_detail_notification).show(getFragmentManager(), roomName);
			}
			else
			{
				Log.e(TAG, "no notification");
				new PMS_MockDetailFragment(R.drawable.ic_edv1_pms_detail_no_notification).show(getFragmentManager(), roomName);
			}
//			new PMS_DetailFragment(mCtx, new EDV1Hosts()).show(getFragmentManager(), roomName);
//			tag = RoomName.EDV_1.name();
//			ft.remove(fm.findFragmentByTag(tag));
//			ft.add(new PMS_DetailFragment(mCtx, new EDV1Hosts()), tag);
		}
		else if(roomName.equals(mCtx.getString(R.string.global_Room3_name)))
		{
			new PMS_MockDetailFragment(R.drawable.ic_edv3_pms_detail_no_notification).show(getFragmentManager(), roomName);
//			new PMS_DetailFragment(mCtx, new EDV3Hosts()).show(getFragmentManager(), roomName);
//			tag = RoomName.EDV_3.name();
//			ft.remove(fm.findFragmentByTag(tag));
//			ft.add(new PMS_DetailFragment(mCtx, new EDV3Hosts()), tag);
		}
		else if(roomName.equals(mCtx.getString(R.string.global_Room6_name)))
		{
			new PMS_MockDetailFragment(R.drawable.ic_edv6_pms_detail_no_notification).show(getFragmentManager(), roomName);
//			new PMS_DetailFragment(mCtx, new EDV6Hosts()).show(getFragmentManager(), roomName);
//			tag = RoomName.EDV_6.name();
//			ft.remove(fm.findFragmentByTag(tag));
//			ft.add(new PMS_DetailFragment(mCtx, new EDV6Hosts()), tag);
		
		}
		
	}
	
	
	
}
