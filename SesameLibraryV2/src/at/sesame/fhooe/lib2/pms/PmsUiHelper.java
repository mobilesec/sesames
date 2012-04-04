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
import at.sesame.fhooe.lib2.pms.SeparatorListEntry.ListType;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;

public class PmsUiHelper 
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
	private  ArrayList<IListEntry> mActiveEntries;
	private ArrayList<IListEntry> mInactiveEntries;
	
	private IPmsUi mUi;
	
	private SelectedType mSelectedType = SelectedType.none;
	
	private Context mCtx;
	
//	/**
//	 * mapping of hostname and selection status
//	 */
//	private HashMap<String, Boolean> mSelection = new HashMap<String, Boolean>();
	
	public PmsUiHelper(Context _ctx, IPmsUi _ui, ViewGroup _activeDeviceControl, ViewGroup _inactiveDeviceControl)
	{
		mCtx = _ctx;
//		mUi = _listener;
//		mFragMan = _fragMan;
		mUi = _ui;
		
		
		mActiveDeviceControlContainer = _activeDeviceControl;
		mInactiveDeviceControlContainer = _inactiveDeviceControl;
		setControlContainerVisibility(View.GONE, View.GONE);
	}
	
	public void createListEntries(ArrayList<ControllableDevice> _devs)
	{
		ArrayList<IListEntry>[] entries = getListEntries(_devs);
		mActiveEntries = entries[0];
		mInactiveEntries = entries[1];
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

		switch (mSelectedType) {
		case none:
			if(_checked)
			{
				boolean selected = selectDevice(_cd, true);
//				updateMultipleSelectionWidgets();
				System.out.println("single selection result = "+selected);
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
				mUi.notifySelectionFail();
				res = false;
			}
			break;
		case inactive:
			if(_cd.isAlive())
			{
				mUi.notifySelectionFail();
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
		updateMultipleSelectionWidgets();
//		if(mSelectedType.equals(SelectedType.none))
//		{
//			Log.e(TAG, "handling single selection attempt with no devices selected");
//			setControlContainerVisibility(View.GONE, View.GONE);
//		}
//		mUi.notifyPMSUpdated();
		return res;
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
		ControllableDeviceListEntry cdle = getListEntryFromDevice(_cd);
		if(null==cdle)
		{
			System.out.println("selectDev: dev not found");
			return false;
		}
		cdle.setSelection(_select);
		
		if(_cd.isAlive())
		{
			mSelectedType = SelectedType.active;
		}
		else
		{
			mSelectedType = SelectedType.inactive;
		}
		
		if(getSelectedDevices().isEmpty())
		{
			mSelectedType = SelectedType.none;
		}
//		mSelection.put(_cdle.getControllableDevice().getMac(), _select);
		return cdle.isSelected();
	}
	
	/**
	 * finds the listentry associated with the passed ControllableDevice
	 * @param _cd the ControllableDevice to get the list entry for
	 * @return the listentry associated with the passed ControllableDevice
	 */
	private ControllableDeviceListEntry getListEntryFromDevice(ControllableDevice _cd)
	{
		for(IListEntry entry:mActiveEntries)
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
		for(IListEntry entry:mInactiveEntries)
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
	
	/**
	 * sets the selection state of all devices of a certain category
	 * @param _type the type of ControllableDevices to be selected
	 * @param _select determines whether the devices should selected or deselected
	 * @return true if the group of devices was selected, false otherwise
	 */
	private boolean selectDevices(SelectedType _type, boolean _select)
	{
//		boolean selectionFlag;
		ArrayList<IListEntry> listToCheck;
		System.out.println("***selecting devices:"+_type);
		System.out.println("***selecting devices:"+_select);
		switch (_type) 
		{
		case active:
			listToCheck = mActiveEntries;
			break;
		case inactive:
			listToCheck = mInactiveEntries;
			break;
		case none:
			deselectAll();
		default:
			return false;
		}

		boolean result = true;
		for(IListEntry entry:listToCheck)
		{
			if(entry instanceof ControllableDeviceListEntry)
			{
//				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
//				if(cdle.getControllableDevice().isAlive()==selectionFlag)
				{
					if(! selectDevice(((ControllableDeviceListEntry)entry).getControllableDevice(), _select))
					{
						result = false;
					}
				}
			}
		}
		updateMultipleSelectionWidgets();
//		mUi.notifyPMSUpdated();
		return result;
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
			System.out.println("active group selection");
			switch(mSelectedType)
			{
			case active:
			case none:

				return selectDevices(SelectedType.active, _isChecked);
				//				return true;
			case inactive:
				mUi.notifySelectionFail();
				return false;
			}
			break;
		case inactive:
			System.out.println("inactive group selection");
			switch(mSelectedType)
			{
			case inactive:
			case none:
				return selectDevices(SelectedType.inactive, _isChecked);
				//				return true;
			case active:
				mUi.notifySelectionFail();
				return false;
			}
			break;
		}
		return false;

	}
	
	/**
	 * hides or shows the containers for actions on multiple selected ControllableDevices
	 */
	private void updateMultipleSelectionWidgets()
	{
		switch(mSelectedType)
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
	 * deselects all devices
	 */
	public void deselectAll()
	{

		for(IListEntry entry:mActiveEntries)
		{
			if(entry instanceof ControllableDeviceListEntry)
			{
				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
				cdle.setSelection(false);
			}
		}
		
		for(IListEntry entry:mInactiveEntries)
		{
			if(entry instanceof ControllableDeviceListEntry)
			{
				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)entry;
				cdle.setSelection(false);
			}
		}

//		for(ControllableDevice cd:mAllDevices)
//		{
//			mSelection.put(cd.getMac(), false);
//		}
		mSelectedType = SelectedType.none;
		mUi.notifyPMSUpdated();
		Handler uiHandler = new Handler(Looper.getMainLooper());
		uiHandler.post(new Runnable() {

			@Override
			public void run() {
				setControlContainerVisibility(View.GONE, View.GONE);

			}
		});


	}
	
	/**
	 * marks the passed device as dirty (information currently not consistent ==> progress bar is shown)
	 * @param _cd the ControllableDevice to mark as dirty
	 */
	public void markDirty(final ControllableDevice _cd)
	{
		ControllableDeviceListEntry entry = getListEntryFromDevice(_cd);
		entry.setDirty(true);
		mUi.notifyPMSUpdated();
		Log.e(TAG, "marked dirty:"+_cd.getHostname());
	}
	
	/**
	 * shows/hides the containers for actions on multiple selected ControllableDevices
	 */
	public void setControlContainerVisibility(int _v1, int _v2)
	{
		mActiveDeviceControlContainer.setVisibility(_v1);

		mInactiveDeviceControlContainer.setVisibility(_v2);
	}
	
	public boolean isDeviceSelected(ControllableDevice _cd)
	{
		System.out.println("checking if dev is selected");
		for(IListEntry entry:mActiveEntries)
		{
			if(entry instanceof ControllableDeviceListEntry)
			{
				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry) entry;
				if(cdle.getControllableDevice().equals(_cd))
				{
					boolean selected =  cdle.isSelected();
					System.out.println("dev found, selected:"+selected);
					return selected;
				}
			}
		}
		
		for(IListEntry entry:mInactiveEntries)
		{
			if(entry instanceof ControllableDeviceListEntry)
			{
				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry) entry;
				if(cdle.getControllableDevice().equals(_cd))
				{
					boolean selected =  cdle.isSelected();
					System.out.println("dev found, selected:"+selected);
					return selected;
				}
			}
		}
		System.out.println("?????controllable device not found");
		return false;
	}

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
	
	private ArrayList<ControllableDeviceListEntry> getSelectedDeviceListEntries()
	{
		ArrayList<ControllableDeviceListEntry> res = new ArrayList<ControllableDeviceListEntry>();
		ArrayList<IListEntry> entries2check;
		switch(mSelectedType)
		{
		case active:
			entries2check = mActiveEntries;
			break;
		case inactive:
			entries2check = mInactiveEntries;
			break;
		case none:
		default:
			return res;
		}
		
		for(IListEntry entry:entries2check)
		{
			if(entry instanceof ControllableDeviceListEntry)
			{
				res.add((ControllableDeviceListEntry)entry);
			}
		}
		return res;
	}
	
	public ArrayList<IListEntry>[]getListEntries(ArrayList<ControllableDevice> _devs)
	{
		ArrayList<IListEntry>[] res = (ArrayList<IListEntry>[])new ArrayList[2];
		ArrayList<ControllableDevice> activeDevs = new ArrayList<ControllableDevice>();
		ArrayList<ControllableDevice> inactiveDevs = new ArrayList<ControllableDevice>();

		for(ControllableDevice cd:_devs)
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
		ArrayList<IListEntry> activeDevEntries = new ArrayList<IListEntry>();
		activeDevEntries.add(new SeparatorListEntry(mCtx, ListType.active, activeDevs.size()));
		
		ArrayList<IListEntry> inactiveDevEntries = new ArrayList<IListEntry>();
		inactiveDevEntries.add(new SeparatorListEntry(mCtx, ListType.inactive, inactiveDevs.size()));
		for(ControllableDevice cd:activeDevs)
		{
			activeDevEntries.add(new ControllableDeviceListEntry(cd));
		}
		for(ControllableDevice cd:inactiveDevs)
		{
			inactiveDevEntries.add(new ControllableDeviceListEntry(cd));
		}
		res[0] = activeDevEntries;
		res[1] = inactiveDevEntries;
		return res;
	}
	
	public ArrayList<ControllableDevice> getSelectedDevices()
	{
		ArrayList<ControllableDevice> res = new ArrayList<ControllableDevice>();
		
		for(ControllableDeviceListEntry cdle:getSelectedDeviceListEntries())
		{
			if(cdle.isSelected())
			{
				res.add(cdle.getControllableDevice());
			}
		}
		
		return res;
	}
}
