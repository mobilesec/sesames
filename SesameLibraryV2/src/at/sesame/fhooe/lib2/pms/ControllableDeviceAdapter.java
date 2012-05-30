/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 11/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib2.pms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import at.sesame.fhooe.lib2.R;
import at.sesame.fhooe.lib2.pms.SeparatorListEntry.ListType;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory.DialogType;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;


public class ControllableDeviceAdapter 
extends ArrayAdapter<IListEntry>
implements OnCheckedChangeListener
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "ControllableDeviceAdapter";
	
	/**
	 * the owning activity's context
	 */
	private Context mContext = null;
	
	/**
	 * the model to be displayed in the list
	 */
	private ArrayList<IListEntry> mDevs = null;
	
	/**
	 * the owning activity's layout inflater
	 */
	private LayoutInflater mLi;
	


	/**
	 * integer constant for single selection
	 */
	private static final int SINGLE_SELECTION = 0;
	
	/**
	 * integer constant for group selection
	 */
	private static final int GROUP_SELECTION = 1;
	
	private PmsHelper mUiHelper;
	
	private static Drawable OFF_DRAWABLE;
	private static Drawable ON_DRAWABLE;
	
	private static Drawable WINDOWS_DRAWABLE;
	private static Drawable LINUX_DRAWABLE;
	private static Drawable MAC_DRAWABLE;
	private static Drawable UNKNOWN_DRAWABLE;
	
	//private static Drawable NOT_SELECTED_BG;
	//private static Drawable SELECTED_BG;
	
	private static ProgressBar mDirtyProgressBar;
	
//	private IPmsUi mUi;
	
//	private PMSController mController;

	/**
	 * creates a new ControllableDeviceAdapter
	 * @param _owner the owner of the adapter
	 * @param objects the model to be shown via the adapter
	 */
	public ControllableDeviceAdapter(Context _ctx, List<IListEntry> objects, PmsHelper _uiHelper) 
	{
		super(_ctx, 0, objects);
//		mController = _controller;
		mUiHelper = _uiHelper;
		mContext = _ctx;
		mDevs = (ArrayList<IListEntry>)objects;
		mLi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDirtyProgressBar = (ProgressBar)mLi.inflate(R.layout.dirty_progressbar, null);
		loadDrawables();
	}
	
	private void loadDrawables()
	{
		WINDOWS_DRAWABLE = mContext.getResources().getDrawable(R.drawable.ic_list_windows);
		LINUX_DRAWABLE = mContext.getResources().getDrawable(R.drawable.ic_list_linux);
		MAC_DRAWABLE = mContext.getResources().getDrawable(R.drawable.ic_list_mac);
		UNKNOWN_DRAWABLE = mContext.getResources().getDrawable(R.drawable.ic_list_unknown);
		
		ON_DRAWABLE = mContext.getResources().getDrawable(R.drawable.ic_power_on);
		OFF_DRAWABLE = mContext.getResources().getDrawable(R.drawable.ic_power_off);
		
	//	NOT_SELECTED_BG = mContext.getResources().getDrawable(R.drawable.list_item_background);
	//	SELECTED_BG = mContext.getResources().getDrawable(R.drawable.list_item_background_selected);
	}
	
	static class ViewHolder
	{
//		public ControllableDevice CONTROLLABLE_DEV; 
		
		public ProgressBar DIRTY_BAR;
		public ImageView OS_VIEW;
		public FrameLayout CONTAINER;
		public ImageButton POWER_BUTT;
		public CheckBox CB;
		
		public TextView NAME_LABEL;
		public TextView IP_LABEL;
		public TextView IDLE_LABEL;
	}

	@Override
	public View getView(int _pos, View _convertView, ViewGroup _parent)
	{
//		Log.e(TAG, "getView "+_pos);
//		View rowView = _convertView;
		if(null==mDevs||(mDevs.size()-1)<_pos)
		{
			Log.e(TAG, "tried to access devices @"+_pos);
			if(null!=mDevs)
			{
				Log.e(TAG, "size of devs="+mDevs.size());
			}
			else
			{
				Log.e(TAG, "devs were null");
			}
			return _convertView;
		}
		IListEntry item = mDevs.get(_pos);
		if(null!=item)
		{
//			long start = System.currentTimeMillis();
			
			if(item.isSeparator())
			{
				SeparatorListEntry sle = (SeparatorListEntry)item;
				_convertView = mLi.inflate(R.layout.controllable_device_listseparator, null);

				TextView tv = (TextView)_convertView.findViewById(R.id.separatorNameLabel);
				tv.setText(sle.getTitle());

				
				CheckBox separatorCb = (CheckBox)_convertView.findViewById(R.id.separatorCheckBox);
				separatorCb.setChecked(sle.isSelected());
				separatorCb.setOnCheckedChangeListener(this);
				
				//the separator associated with the checkbox is set as tag for the checkbox to 
				//be able to determine which separator was selected in onCheckedChanged
				separatorCb.setTag(sle);
//				_convertView.setTag(null);
				return _convertView;
			}
			else
			{
				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)item;
				mUiHelper.setUiInfo(cdle);
				Log.e(TAG, "cdle #"+_pos+": selected="+cdle.isSelected());
				ControllableDevice cd = cdle.getControllableDevice();
				ViewHolder holder;
//				if(null==_convertView)
//				{
//				}
				if(null==_convertView)
				{
					_convertView = mLi.inflate(R.layout.controllable_device_listitem_not_selected, null);
					 holder = new ViewHolder();
//					vh.CONTROLLABLE_DEV = ((ControllableDeviceListEntry)item).getControllableDevice();
					
//				System.out.println("adapter found device:"+cdle.isSelected());
//					if(cdle.isSelected())
//					{
//						rowView = mLi.inflate(R.layout.controllable_device_listitem_selected, null);
//					}
//					else
//					{
					
//					}
					holder.OS_VIEW = (ImageView)_convertView.findViewById(R.id.osIconView);
					holder.CONTAINER = (FrameLayout)_convertView.findViewById(R.id.controllable_device_list_item_placeholder);
					holder.DIRTY_BAR = (ProgressBar)mLi.inflate(R.layout.dirty_progressbar, null);
					
					holder.CB = (CheckBox)_convertView.findViewById(R.id.controllable_device_list_item_selection_box);
//					holder.CB.setChecked(cdle.isSelected());
//					holder.CB.setTag(cd);
					holder.NAME_LABEL =(TextView) _convertView.findViewById(R.id.nameLabel);
					holder.IP_LABEL = (TextView)_convertView.findViewById(R.id.ipLabel);
					holder.IDLE_LABEL = (TextView)_convertView.findViewById(R.id.idleLabel);
					_convertView.setTag(holder);
				}
				else
				{
					 holder = (ViewHolder)_convertView.getTag();
					
				}

				
				
				Log.e(TAG, "holder for "+_pos+": "+holder);
				if(null!=cd)
				{

					if(null==holder.OS_VIEW)
					{
						Log.e(TAG, "os view of holder was null");
					}
					switch(cd.getOs())
					{
					case windows:
						holder.OS_VIEW.setImageDrawable(WINDOWS_DRAWABLE);
						break;
					case linux:
						holder.OS_VIEW.setImageDrawable(LINUX_DRAWABLE);
						break;
					case mac:
						holder.OS_VIEW.setImageDrawable(MAC_DRAWABLE);
						break;
					case unknown:
						holder.OS_VIEW.setImageDrawable(UNKNOWN_DRAWABLE);
						break;
					}
					
//					if(cdle.isSelected())
//					{
//						rowView.setBackgroundDrawable(SELECTED_BG);
//					}
//					else
//					{
//						rowView.setBackgroundDrawable(NOT_SELECTED_BG);
//					}
				
					if(!cd.isDirty())
					{
//						ImageButton powerView = (ImageButton)v.findViewById(R.id.controllable_device_list_item_powerIconView);

						holder.POWER_BUTT = new ImageButton(mContext);	
//						holder.POWER_BUTT.setOnClickListener(this);
						holder.POWER_BUTT.setTag(cd);
						holder.POWER_BUTT.setBackgroundColor(Color.TRANSPARENT);

						if(cd.isAlive())
						{
							holder.POWER_BUTT.setImageDrawable(ON_DRAWABLE);
						}
						else
						{
							holder.POWER_BUTT.setImageDrawable(OFF_DRAWABLE);
						}
						holder.CONTAINER.removeAllViews();
						holder.CONTAINER.addView(holder.POWER_BUTT);
//						holder.CB.setEnabled(true);
					}
					else
					{
						//						LayoutInflater inflater = mLi.in
//						ProgressBar pg = (ProgressBar ) mLi.inflate(R.layout.dirty_progressbar, null);
						//						pg.setIndeterminate(true);
						holder.CB.setEnabled(false);
						holder.CONTAINER.removeAllViews();
						holder.CONTAINER.addView(holder.DIRTY_BAR);
						
					}

					if(null!=holder.CB)
					{
						holder.CB.setOnCheckedChangeListener(null);
						holder.CB.setChecked(cdle.isSelected());

						holder.CB.setOnCheckedChangeListener(this);
//						the ControllableDevice associated with the checkbox is set as tag for the checkbox to 
//						be able to determine which device was selected in onCheckedChanged
						holder.CB.setTag(cd);
					}
					else
					{
						Log.e(TAG, "checkbox was null");
					}

					holder.NAME_LABEL.setText(cd.getHostname());

					holder.IP_LABEL.setText(cd.getIp());

					holder.IDLE_LABEL.setText("");
//					idleLabel.setText(mContext.getString(R.string.ControllableDeviceAdapter_idleLabel_text)+cd.getIdleSince());
					if(cd.isAlive())
					{
						if(cd.getIdleSinceMinutes()>=ControllableDevice.IDLE_NOTIFICATION_THRESHOLD)
						{
							holder.IDLE_LABEL.setTextColor(Color.RED);
							holder.IDLE_LABEL.setText(cd.getIdleString());
							if(!cd.isDirty())
							{
								holder.CB.setEnabled(true);								
								holder.CB.setVisibility(View.VISIBLE);
							}
						}
						else
						{
							holder.IDLE_LABEL.setTextColor(Color.WHITE);
							holder.IDLE_LABEL.setText(cd.getIdleString());
							holder.CB.setEnabled(false);
							holder.CB.setVisibility(View.INVISIBLE);
						}
						
					}
					else
					{
						holder.CB.setEnabled(true);
						holder.CB.setVisibility(View.VISIBLE);
					}

//					long duration = System.currentTimeMillis()-start;
//					Log.e(TAG, "view generation took:"+duration+"ms");
				}
				else
				{
					Log.e(TAG, "cd was null");
				}
			}
		}

		return _convertView;
	}

//	@Override
//	public void onClick(View arg0) 
//	{
//		ControllableDevice cd = extractDeviceFromTag(arg0);
//		mUiHelper.handlePowerClick(cd);
////		mUi.handlePowerClick(cd);
////		if(cd.isAlive())
////		{
////			PMSDialogFactory.showDialog(DialogType.ACTIVE_DEVICE_ACTION_DIALOG, _fm, _handler, _params)
////		}
//
//	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		System.out.println("onCheckedChanged");
		buttonView.setOnCheckedChangeListener(null);
		int type = identifyCheckSource(buttonView);
		switch(type)
		{
		case SINGLE_SELECTION:
			handleSingleSelection(buttonView, isChecked);
			break;
		case GROUP_SELECTION:
			SeparatorListEntry sle = extractSeparatorFromTag(buttonView);
			boolean selected = mUiHelper.handleMultipleSelectionAttempt(sle.getType(), isChecked);
			System.out.println("result of group selection:"+selected);
			sle.setSelected(isChecked);
//			buttonView.setChecked(selected);
			break;
		}
		buttonView.setOnCheckedChangeListener(this);
	}

	/**
	 * determines which ControllableDevice was selected, and sets the checked state of the passed
	 * CompoundButton according to the result of handleSingleSelectionAttempt method in PMSClientActivity
	 * @param buttonView the CompoundButton that changed it's checked state
	 * @param isChecked boolean flag indicating whether the CompoundButton is checked or not
	 */
	private void handleSingleSelection(CompoundButton buttonView, boolean isChecked)
	{
		ControllableDevice cd = extractDeviceFromTag(buttonView);
		if(null!=cd)
		{
			boolean selected = mUiHelper.handleSingleSelectionAttempt(cd, isChecked);
			Log.i(TAG, "!!!!result of selection attempt:"+selected);
			buttonView.setChecked(selected);
		}
	}

	/**
	 * extracts a ControllableDevice from the tag of a passed view
	 * @param _v the view to get the ControllableDevice from
	 * @return the ControllableDevice contained in the view's tag
	 */
	private ControllableDevice extractDeviceFromTag(View _v)
	{
		Object o = _v.getTag();
		return (ControllableDevice)o;
	}

	/**
	 * extracts a SeparatorListEntry from the tag of a passed view
	 * @param _v the view to get the SeparatorListEntry from
	 * @return the SeparatorListEntry contained in the view's tag
	 */
	private SeparatorListEntry extractSeparatorFromTag(View _v)
	{
		Object o = _v.getTag();
		return (SeparatorListEntry)o;
	}

	/**
	 * determines whether a checkbox in a separator or a in a list entry was passed
	 * @param _cb the checkbox to be checked
	 * @return SINGLE_SELECTION if a list entry was contained in the tag, 
	 * GROUP_SELECTION if a separator was contained in the tag, -1 otherwise
	 */
	private int identifyCheckSource(CompoundButton _cb)
	{
		Object o = _cb.getTag();

		if(o instanceof ControllableDevice)
		{
			return SINGLE_SELECTION;
		}
		else if (o instanceof SeparatorListEntry)
		{
			return GROUP_SELECTION;
		}
		else
		{
			return -1;
		}
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if(mDevs.get(position).isSeparator())
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public IListEntry getItem(int position) {
		// TODO Auto-generated method stub
		return mDevs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

//	@Override
//	public void notifyDataSetChanged()
//	{
//		ArrayList<IListEntry> copy = ((ArrayList<IListEntry>) mDevs.clone());
//		
//		ArrayList<ControllableDeviceListEntry> activeEntries = new ArrayList<ControllableDeviceListEntry>();
//		ArrayList<ControllableDeviceListEntry> inactiveEntries = new ArrayList<ControllableDeviceListEntry>();
//		SeparatorListEntry activeSeparator = null;
//		SeparatorListEntry inactiveSeparator = null;
//		for(IListEntry le:copy)
//		{
//			if(!le.isSeparator())
//			{
//				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)le;
//				if(cdle.getControllableDevice().isAlive())
//				{
//					activeEntries.add(cdle);
//				}
//				else
//				{
//					inactiveEntries.add(cdle);
//				}
//			}
//			else
//			{
//				SeparatorListEntry sle = (SeparatorListEntry)le;
//				if(sle.getType().equals(ListType.active))
//				{
//					activeSeparator = sle;
//				}
//				else
//				{
//					inactiveSeparator = sle;
//				}
//			}
//		}
//		
//		Collections.sort(activeEntries, new ControllableDeviceListEntryComparator());
//		Collections.sort(inactiveEntries, new ControllableDeviceListEntryComparator());
//		
//		mDevs.clear();
//		mDevs.add(activeSeparator);
//		for(ControllableDeviceListEntry cdle:activeEntries)
//		{
//			mDevs.add(cdle);
//		}
//		mDevs.add(inactiveSeparator);
//		
//		for(ControllableDeviceListEntry cdle:inactiveEntries)
//		{
//			mDevs.add(cdle);
//		}
//		
//		super.notifyDataSetChanged();
//	}
	
	
	
//	public ControllableDeviceListEntry getEntryForDevice(ControllableDevice _cd)
//	{
//		for(IListEntry le:mDevs)
//		{
//			if(!le.isSeparator())
//			{
//				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)le;
//				if(cdle.getControllableDevice().equals(_cd))
//				{
//					return cdle;
//				}
//			}
//		}
//		return null;
//	}

	
	
}
