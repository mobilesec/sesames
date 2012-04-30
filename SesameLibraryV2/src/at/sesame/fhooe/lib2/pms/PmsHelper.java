package at.sesame.fhooe.lib2.pms;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import at.sesame.fhooe.lib2.R;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.pms.SeparatorListEntry.ListType;
import at.sesame.fhooe.lib2.pms.hosts.HostList;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice.PowerOffState;

public class PmsHelper
{
	private static final String TAG = "PmsUiHelper";
	/**
	 * this enumeration is used to determine which kind of device was selected
	 * @author admin
	 *
	 */
	public enum SelectedType
	{
		active,
		inactive,
		none
	};
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
	//	private  ArrayList<IListEntry> mActiveEntries;
	//	private ArrayList<IListEntry> mInactiveEntries;

	private HashMap<String, Boolean> mSelectionMap = new HashMap<String, Boolean>();
//	private HashMap<String, Boolean> mDirtyMap = new HashMap<String, Boolean>();

	//	private IPmsUi mUi;

	private SelectedType mSelectedType;

	private Context mCtx;

//	private PMSController mController;

	private boolean mDevicesLoaded = false;

	private IPMSUpdateListener mUpdateListener;
	
	private SesameDataCache mCache;// = SesameDataCache.getInstance(null);

	//	private ArrayList<ControllableDeviceListEntry> mEntries;
	//	/**
	//	 * mapping of hostname and selection status
	//	 */
	//	private HashMap<String, Boolean> mSelection = new HashMap<String, Boolean>();

	private String mRoomName;

	private FragmentManager mFragMan;
	
	public PmsHelper(Context _ctx, FragmentManager _fm, IPMSUpdateListener _updateListener, String _roomName, ViewGroup _activeDeviceControl, ViewGroup _inactiveDeviceControl)
	{
		mCtx = _ctx;
		mCache = SesameDataCache.getInstance(mCtx);
		mRoomName = _roomName;
//		mController = new PMSController(mCtx, this, _hosts2Load, _fm);
		mUpdateListener = _updateListener;
		//		mUi = _listener;
		mFragMan = _fm;
		//		mUi = _ui;


		mActiveDeviceControlContainer = _activeDeviceControl;
		mInactiveDeviceControlContainer = _inactiveDeviceControl;

		initializeSelectionMap();
//		initializeDirtyMap();
		//		resetDirtyMap();
		setSelectedType(SelectedType.none);
	}

	//	public void createListEntries(ArrayList<ControllableDevice> _devs)
	//	{
	//		ArrayList<IListEntry>[] entries = getListEntries(_devs);
	//		mActiveEntries = entries[0];
	//		mInactiveEntries = entries[1];
	//	}

	//	public int getNumSelectedDevices()
	//	{
	//		int cnt = 0;
	//		
	//		for(ControllableDevice cd:mSelectionMap.keySet())
	//		{
	//			if(mSelectionMap.get(cd))
	//			{
	//				cnt++;
	//			}
	//		}
	//		return cnt;
	//	}


	/**
	 * handles an attempt to select a device. if the list of selected devices already contains a device 
	 * of different type (active/inactive) the device is not selected. 
	 * @param _cd the device to select
	 * @return true if the device was selected, false otherwise
	 */
	public boolean handleSingleSelectionAttempt(ControllableDevice _cd, boolean _checked)
	{
		boolean res = false;

		switch (mSelectedType) {
		case none:
			if(_checked)
			{
				boolean selected = selectDevice(_cd, true);
				//				updateMultipleSelectionWidgets();
				//				System.out.println("single selection result = "+selected);
				res = true;
			}
			break;
		case active:
			if(_cd.isAlive())
			{
				res = selectDevice(_cd,_checked);
				//				updateMultipleSelectionWidgets();
				//				res = true;
			}
			else
			{
				toastSelectionFail();
				res = false;
			}
			break;
		case inactive:
			if(_cd.isAlive())
			{
				toastSelectionFail();
				res = false;
			}
			else
			{
				res = selectDevice(_cd,_checked);
				//				updateMultipleSelectionWidgets();
				//				res = selectDevice(_cd, _select);
			}
			break;
		}
		//		updateMultipleSelectionWidgets();
		//		if(mSelectedType.equals(SelectedType.none))
		//		{
		//			Log.e(TAG, "handling single selection attempt with no devices selected");
		//			setControlContainerVisibility(View.GONE, View.GONE);
		//		}
		mUpdateListener.notifyPMSUpdated();
		return res;
	}

	private void toastSelectionFail()
	{
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mCtx, R.string.selection_fail_message, Toast.LENGTH_LONG).show();

			}
		});
	}

	/**
	 * changes the selection state of one device
	 * @param _cdle the list entry associated with the device
	 * @param _select determines whether the device should selected or deselected
	 * @return true if the device was selected, false otherwise
	 */
	private boolean selectDevice(ControllableDevice _cd, boolean _select)
	{
		//		_cdle.setSelection(_select);
		//		ControllableDevice cd = _cdle.getControllableDevice();
		//		ControllableDeviceListEntry cdle = getListEntryFromDevice(_cd);
		//		if(null==cdle)
		//		{
		//			System.out.println("selectDev: dev not found");
		//			return false;
		//		}
		//		cdle.setSelection(_select);
		mSelectionMap.put(_cd.getMac(), _select);

		if(_cd.isAlive())
		{
			setSelectedType(SelectedType.active);
		}
		else
		{
			//			mSelectedType = SelectedType.inactive;
			setSelectedType(SelectedType.inactive);
		}

		if(getSelectedDevices().isEmpty())
		{
			//			mSelectedType = SelectedType.none;
			setSelectedType(SelectedType.none);
		}
		//		mSelection.put(_cdle.getControllableDevice().getMac(), _select);
		return mSelectionMap.get(_cd.getMac());
	}



	public void handlePowerClick(ControllableDevice _cd)
	{
//		mController.handlePowerClick(_cd);
	}

	//	public ControllableDeviceListEntry getEntry(ControllableDevice _cd)
	//	{
	//		return getUiInfo(new ControllableDeviceListEntry(_cd));
	//	}

//	public void startUpdates()
//	{
//		mController.startAutoUpdate();
//	}
//
//	public void stopUpdates()
//	{
//		mController.stopAutoUpdate();
//	}

//	public ControllableDevice getDeviceByMac(String _mac)
//	{
////		for(int i = 0;i<mController.getAllDevices().size();i++)
////		{
////			ControllableDevice cd = mController
////			if(cd.getMac().equals(_mac))
////			{
////				return cd;
////			}
////		}
//		return mController.getDeviceFromMac(_mac);
//	}
//
//	public PMSController getController()
//	{
//		return mController;
//	}

	//	/**
	//	 * finds the listentry associated with the passed ControllableDevice
	//	 * @param _cd the ControllableDevice to get the list entry for
	//	 * @return the listentry associated with the passed ControllableDevice
	//	 */
	//	private ControllableDeviceListEntry getListEntryFromDevice(ControllableDevice _cd)
	//	{
	//		for(IListEntry entry:mActiveEntries)
	//		{
	//			if(entry instanceof ControllableDeviceListEntry)
	//			{
	//				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
	//				if(cdle.getControllableDevice().equals(_cd))
	//				{
	//					return cdle;
	//				}
	//			}
	//		}
	//		for(IListEntry entry:mInactiveEntries)
	//		{
	//			if(entry instanceof ControllableDeviceListEntry)
	//			{
	//				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
	//				if(cdle.getControllableDevice().equals(_cd))
	//				{
	//					return cdle;
	//				}
	//			}
	//		}
	//		return null;
	//	}

	//	/**
	//	 * determines which type of ControllableDevice is currently selected
	//	 * @return the type of selected devices
	//	 */
	//	private SelectedType getSelectedType()
	//	{
	////		ArrayList<ControllableDevice>selectedDevs = getSelectedDevices();
	//		if(null==_selectedDevs||_selectedDevs.size()==0)
	//		{
	//			return SelectedType.none;
	//		}
	//		ControllableDevice cd = _selectedDevs.get(0);
	//		if(cd.isAlive())
	//		{
	//			return SelectedType.active;
	//		}
	//		else
	//		{
	//			return SelectedType.inactive;
	//		}
	//	}

	public ControllableDeviceListEntry setUiInfo(ControllableDeviceListEntry _cdle)
	{
		ControllableDevice cd = _cdle.getControllableDevice();
		//		Log.i(TAG, "updating list entry:"+_cdle.toString());
		//		Log.i(TAG, "device from entry:"+cd.toString());
		//		Log.i(TAG, "-----------------");
		//		for(ControllableDevice controlable:mSelectionMap.keySet())
		//		{
		//			Log.i(TAG, controlable.toString());
		//			if(controlable.equals(cd))
		//			{
		//				Log.i(TAG, "equal");
		//			}
		//			else
		//			{
		//				Log.e(TAG, "not equal");
		//			}
		//		}
		if(null==mSelectionMap)
		{
			Log.e(TAG, "selection map was null");
		}
//		if(null==mDirtyMap)
//		{
//			Log.e(TAG, "dirty map was null");
//		}
		Boolean selected = mSelectionMap.get(cd.getMac());
		//			Log.i(TAG, "selected:"+selected);
		//			_cdle.setSelection(selected);
//
//		if(null==mDirtyMap)
//		{
//			Log.e(TAG, "dirty map was null");
//		}
//		Boolean dirty = mDirtyMap.get(cd.getMac());
		try
		{
			//			Log.i(TAG, "dirty:"+dirty);
			_cdle.setSelection(selected);
//			_cdle.setDirty(dirty);
			//			_cdle.setDirty(dirty);		
			//			return new PmsUiInfo(selected, dirty);
			return _cdle;
		}
		catch(NullPointerException _npe)
		{
			String prefix = "";
			if(null==selected)
			{
				prefix += "selected";
			}
//			if(null==dirty)
//			{
//				prefix+=" dirty";
//			}
			Log.e(TAG, prefix+" failed for:"+_cdle.getControllableDevice().getHostname());
		}
		return null;
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
		//		ArrayList<IListEntry> listToCheck;
		System.out.println("***selecting devices:"+_type);
		System.out.println("***selecting devices:"+_select);
		switch (_type) 
		{
		case active:
			selectionFlag = true;
			if(_select)
			{				
				setSelectedType(SelectedType.active);
			}
			else
			{
				setSelectedType(SelectedType.none);
			}
			break;
		case inactive:
			selectionFlag = false;
			//			mSelectedType = SelectedType.inactive;
			if(_select)
			{				
				setSelectedType(SelectedType.inactive);
			}
			else
			{
				setSelectedType(SelectedType.none);
			}
			break;
		case none:
			deselectAll();
			return true;
		default:
			return false;
		}

		//		boolean result = true;
		for(ControllableDevice cd:mCache.getDevicesForRoom(mRoomName))
		{
			if(cd.isAlive()==selectionFlag)
			{
				if(cd.isAlive())
				{
					if(cd.getIdleSinceMinutes()>=ControllableDevice.IDLE_NOTIFICATION_THRESHOLD)
					{
						mSelectionMap.put(cd.getMac(), _select);
					}
				}
				else
				{
					mSelectionMap.put(cd.getMac(), _select);					
				}
			}
		}
		//		for(IListEntry entry:listToCheck)
		//		{
		//			if(entry instanceof ControllableDeviceListEntry)
		//			{
		////				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
		////				if(cdle.getControllableDevice().isAlive()==selectionFlag)
		//				{
		//					if(! selectDevice(((ControllableDeviceListEntry)entry).getControllableDevice(), _select))
		//					{
		//						result = false;
		//					}
		//				}
		//			}
		//		}
		//		updateMultipleSelectionWidgets();
		//		mUi.notifyPMSUpdated();
		return true;

	}

	/**
	 * this method is called when multiple selection of a category is requested (checkbox in separator)
	 * @param _type specifies which kind of ControllableDevice was selected
	 * @param _isChecked determines whether the device was selected or deselected
	 * @return true if the device could be selected, false otherwise
	 */
	public boolean handleMultipleSelectionAttempt(ListType _type, boolean _isChecked)
	{
//		System.out.println("handle multiple selection attempt");
//		System.out.println("selected type = "+mSelectedType.name());
		boolean res  = false;
		switch(_type)
		{
		case active:
//			System.out.println("active group selection");
			switch(mSelectedType)
			{
			case active:
			case none:

				res = selectDevices(SelectedType.active, _isChecked);
				break;
				//				return true;
			case inactive:
				toastSelectionFail();
				return false;
			}
			break;
		case inactive:
//			System.out.println("inactive group selection");
			switch(mSelectedType)
			{
			case inactive:
			case none:
				res =  selectDevices(SelectedType.inactive, _isChecked);
				break;
				//				return true;
			case active:
				toastSelectionFail();
				return false;
			}
			break;
		}
		mUpdateListener.notifyPMSUpdated();
		return res;

	}

	/**
	 * hides or shows the containers for actions on multiple selected ControllableDevices
	 */
	private synchronized void updateMultipleSelectionWidgets()
	{
		switch(mSelectedType)
		{
		case active:
//			System.out.println("active");
			setControlContainerVisibility(View.VISIBLE, View.GONE);
			break;
		case inactive:
//			System.out.println("inactive");
			setControlContainerVisibility(View.GONE, View.VISIBLE);
			break;
		case none:
//			System.out.println("none");
			setControlContainerVisibility(View.GONE, View.GONE);
			break;
		}
	}

	/**
	 * deselects all devices
	 */
	public void deselectAll()
	{
//		System.out.println("deselect all");
		initializeSelectionMap();
		setSelectedType(SelectedType.none);

		//		for(IListEntry entry:mActiveEntries)
		//		{
		//			if(entry instanceof ControllableDeviceListEntry)
		//			{
		//				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
		//				cdle.setSelection(false);
		//			}
		//		}
		//		
		//		for(IListEntry entry:mInactiveEntries)
		//		{
		//			if(entry instanceof ControllableDeviceListEntry)
		//			{
		//				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
		//				cdle.setSelection(false);
		//			}
		//		}
		//
		////		for(ControllableDevice cd:mAllDevices)
		////		{
		////			mSelection.put(cd.getMac(), false);
		////		}
		//		mSelectedType = SelectedType.none;
		////		mUi.notifyPMSUpdated();
		//		Handler uiHandler = new Handler(Looper.getMainLooper());
		//		uiHandler.post(new Runnable() {
		//
		//			@Override
		//			public void run() {
		//				setControlContainerVisibility(View.GONE, View.GONE);
		//
		//			}
		//		});


	}

	private synchronized void setSelectedType(SelectedType _type)
	{
//		System.out.println("setSelectedType:"+_type.name());
		mSelectedType = _type;
		updateMultipleSelectionWidgets();
	}

//	/**
//	 * marks the passed device as dirty (information currently not consistent ==> progress bar is shown)
//	 * @param _cd the ControllableDevice to mark as dirty
//	 */
//	public void markDirty(final ControllableDevice _cd, boolean _dirty)
//	{
//		//		ControllableDeviceListEntry entry = getListEntryFromDevice(_cd);
//		//		entry.setDirty(true);
//		//		mUi.notifyPMSUpdated();
//		mDirtyMap.put(_cd.getMac(), _dirty);
//		mUpdateListener.notifyPMSUpdated();
////		Log.e(TAG, "marked dirty:"+_cd.getHostname());
//	}

	/**
	 * shows/hides the containers for actions on multiple selected ControllableDevices
	 */
	public void setControlContainerVisibility(final int _v1, final int _v2)
	{
//		System.out.println("set control container visibility ("+_v1+", "+_v2+")");
		if(null==mActiveDeviceControlContainer || null==mInactiveDeviceControlContainer)
		{
			return;
		}
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mActiveDeviceControlContainer.setVisibility(_v1);

				mInactiveDeviceControlContainer.setVisibility(_v2);

			}
		});
	}

	//	public boolean isDeviceSelected(ControllableDevice _cd)
	//	{
	////		System.out.println("checking if dev is selected");
	////		for(IListEntry entry:mActiveEntries)
	////		{
	////			if(entry instanceof ControllableDeviceListEntry)
	////			{
	////				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry) entry;
	////				if(cdle.getControllableDevice().equals(_cd))
	////				{
	////					boolean selected =  cdle.isSelected();
	////					System.out.println("dev found, selected:"+selected);
	////					return selected;
	////				}
	////			}
	////		}
	////		
	////		for(IListEntry entry:mInactiveEntries)
	////		{
	////			if(entry instanceof ControllableDeviceListEntry)
	////			{
	////				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry) entry;
	////				if(cdle.getControllableDevice().equals(_cd))
	////				{
	////					boolean selected =  cdle.isSelected();
	////					System.out.println("dev found, selected:"+selected);
	////					return selected;
	////				}
	////			}
	////		}
	////		System.out.println("?????controllable device not found");
	////		return false;
	//		return mSelectionMap.get(_cd);
	//	}

	//	public HashMap<String, Boolean> getSelectionMap(ArrayList<ControllableDevice> _devs) {
	//		HashMap<String, Boolean> res = new HashMap<String, Boolean>();
	//
	//		ArrayList<IListEntry>[] entries = getListEntries(mCtx, _devs);
	//		mActiveEntries = entries[0];
	//		mInactiveEntries = entries[1];
	//		
	//		
	//		//		for(ControllableDeviceListEntry cdle:getSelectedDeviceListEntries())
	////		{
	//////				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
	////				ControllableDevice cd = cdle.getControllableDevice();
	////				res.put(cd.getMac(), cdle.isSelected());
	////			
	////		}
	//		for(IListEntry entry:mActiveEntries)
	//		{
	//			if(entry instanceof ControllableDeviceListEntry)
	//			{
	//				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
	//				res.put(cdle.getControllableDevice().getMac(), cdle.isSelected());
	//			}
	//		}
	//		for(IListEntry entry:mInactiveEntries)
	//		{
	//			if(entry instanceof ControllableDeviceListEntry)
	//			{
	//				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
	//				res.put(cdle.getControllableDevice().getMac(), cdle.isSelected());
	//			}
	//		}
	//		return res;
	//	}

	//	private ArrayList<ControllableDeviceListEntry> getSelectedDeviceListEntries()
	//	{
	//		ArrayList<ControllableDeviceListEntry> res = new ArrayList<ControllableDeviceListEntry>();
	//		ArrayList<IListEntry> entries2check;
	//		switch(mSelectedType)
	//		{
	//		case active:
	//			entries2check = mActiveEntries;
	//			break;
	//		case inactive:
	//			entries2check = mInactiveEntries;
	//			break;
	//		case none:
	//		default:
	//			return res;
	//		}
	//		
	//		for(IListEntry entry:entries2check)
	//		{
	//			if(entry instanceof ControllableDeviceListEntry)
	//			{
	//				res.add((ControllableDeviceListEntry)entry);
	//			}
	//		}
	//		return res;
	//	}
	//	
	//	public ArrayList<IListEntry>[]getListEntries(ArrayList<ControllableDevice> _devs)
	//	{
	//		ArrayList<IListEntry>[] res = (ArrayList<IListEntry>[])new ArrayList[2];
	//		ArrayList<ControllableDevice> activeDevs = new ArrayList<ControllableDevice>();
	//		ArrayList<ControllableDevice> inactiveDevs = new ArrayList<ControllableDevice>();
	//
	//		for(ControllableDevice cd:_devs)
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
	//		ArrayList<IListEntry> activeDevEntries = new ArrayList<IListEntry>();
	//		activeDevEntries.add(new SeparatorListEntry(mCtx, ListType.active, activeDevs.size()));
	//		
	//		ArrayList<IListEntry> inactiveDevEntries = new ArrayList<IListEntry>();
	//		inactiveDevEntries.add(new SeparatorListEntry(mCtx, ListType.inactive, inactiveDevs.size()));
	//		for(ControllableDevice cd:activeDevs)
	//		{
	//			activeDevEntries.add(new ControllableDeviceListEntry(cd));
	//		}
	//		for(ControllableDevice cd:inactiveDevs)
	//		{
	//			inactiveDevEntries.add(new ControllableDeviceListEntry(cd));
	//		}
	//		res[0] = activeDevEntries;
	//		res[1] = inactiveDevEntries;
	//		return res;
	//	}

	//	public ArrayList<ControllableDevice> getSelectedDevices()
	//	{
	//		ArrayList<ControllableDevice> res = new ArrayList<ControllableDevice>();
	//		
	//		for(ControllableDeviceListEntry cdle:getSelectedDeviceListEntries())
	//		{
	//			if(cdle.isSelected())
	//			{
	//				res.add(cdle.getControllableDevice());
	//			}
	//		}
	//		
	//		return res;
	//	}

	public ArrayList<ControllableDevice> getSelectedDevices()
	{
		ArrayList<ControllableDevice> res = new ArrayList<ControllableDevice>();
		for(ControllableDevice cd:mCache.getDevicesForRoom(mRoomName))
		{
			if(mSelectionMap.get(cd.getMac()))
			{
				res.add(cd);
			}
		}
		return res;

	}


	public void notifyDevicesLoaded()
	{
		mDevicesLoaded = true;
		initializeSelectionMap();
//		initializeDirtyMap();
		setSelectedType(SelectedType.none);
		mUpdateListener.notifyPMSUpdated();
		//		mUpdateListener.notifyPMSUpdated();

	}

	private void initializeSelectionMap()
	{
		System.out.println("initialize selection map");
		for(ControllableDevice cd:mCache.getDevicesForRoom(mRoomName))
		{
			mSelectionMap.put(cd.getMac(), false);
		}
	}

//	public void initializeDirtyMap()
//	{
//		for(ControllableDevice cd:mCache.getDevicesForRoom(mRoomName))
//		{
//			mDirtyMap.put(cd.getMac(), false);
//		}
//	}

	public boolean areDevicesLoaded()
	{
		return mDevicesLoaded;
	}

	public void handlePowerOffAll()
	{
		mCache.getController().powerOffDevices(this, mFragMan, getSelectedDevices(), PowerOffState.shutdown);
	}

	public void handleSleepAll()
	{
		mCache.getController().powerOffDevices(this, mFragMan, getSelectedDevices(), PowerOffState.sleep);
	}

	public void handleWakeUpAll()
	{
		mCache.getController().wakeupDevices(this, mFragMan, getSelectedDevices());
	}

	public void notifyPMSUpdated() {
		mUpdateListener.notifyPMSUpdated();
		
	}
}
