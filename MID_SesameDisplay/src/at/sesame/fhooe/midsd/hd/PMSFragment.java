package at.sesame.fhooe.midsd.hd;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.ListFragment;

public class PMSFragment 
extends ListFragment 
{
//	private ArrayList<ComputerRoomInformation> mInfos;
	private PMSListAdapter mAdapter;
	
	public PMSFragment(Context _ctx)
	{
		mAdapter = new PMSListAdapter(_ctx, 1, createDummyInfos());
		setListAdapter(mAdapter);
	}
	
	private ArrayList<ComputerRoomInformation> createDummyInfos()
	{
		ArrayList<ComputerRoomInformation> infos = new ArrayList<ComputerRoomInformation>();
		infos.add(new ComputerRoomInformation("EDV 1", 10, 5));
		infos.add(new ComputerRoomInformation("EDV 3", 3, 52));
		infos.add(new ComputerRoomInformation("EDV 6", 6, 15));
		return infos;
	}
	
}
