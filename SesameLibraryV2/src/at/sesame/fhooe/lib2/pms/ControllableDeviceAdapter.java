/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 11/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib2.pms;

import java.util.ArrayList;
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
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory;
import at.sesame.fhooe.lib2.pms.dialogs.PMSDialogFactory.DialogType;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;


public class ControllableDeviceAdapter 
extends ArrayAdapter<IListEntry>
implements OnClickListener, OnCheckedChangeListener
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
		View rowView = _convertView;
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
			return rowView;
		}
		IListEntry item = mDevs.get(_pos);
		if(null!=item)
		{
			long start = System.currentTimeMillis();
			ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)item;
			ControllableDevice cd = cdle.getControllableDevice();
			if(item.isSeparator())
			{
				SeparatorListEntry sle = (SeparatorListEntry)item;
				rowView = mLi.inflate(R.layout.controllable_device_listseparator, null);


				TextView tv = (TextView)rowView.findViewById(R.id.separatorNameLabel);
				tv.setText(sle.getTitle());

				CheckBox separatorCb = (CheckBox)rowView.findViewById(R.id.separatorCheckBox);
				separatorCb.setChecked(sle.isSelected());
				separatorCb.setOnCheckedChangeListener(this);
				//the separator associated with the checkbox is set as tag for the checkbox to 
				//be able to determine which separator was selected in onCheckedChanged
				separatorCb.setTag(sle);
			}
			else
			{
				if(null==rowView)
				{
					ViewHolder vh = new ViewHolder();
//					vh.CONTROLLABLE_DEV = ((ControllableDeviceListEntry)item).getControllableDevice();
					
//				System.out.println("adapter found device:"+cdle.isSelected());
					if(cdle.isSelected())
					{
						rowView = mLi.inflate(R.layout.controllable_device_listitem_selected, null);
					}
					else
					{
						rowView = mLi.inflate(R.layout.controllable_device_listitem_not_selected, null);
					}
					vh.OS_VIEW = (ImageView)rowView.findViewById(R.id.osIconView);
					vh.CONTAINER = (FrameLayout)rowView.findViewById(R.id.controllable_device_list_item_placeholder);
					vh.DIRTY_BAR = (ProgressBar)mLi.inflate(R.layout.dirty_progressbar, null);
				
					vh.CB = (CheckBox)rowView.findViewById(R.id.controllable_device_list_item_selection_box);
					vh.NAME_LABEL =(TextView) rowView.findViewById(R.id.nameLabel);
					vh.IP_LABEL = (TextView)rowView.findViewById(R.id.ipLabel);
					vh.IDLE_LABEL = (TextView)rowView.findViewById(R.id.idleLabel);
					rowView.setTag(vh);
				}
				ViewHolder holder = (ViewHolder)rowView.getTag();
				if(null!=cd)
				{

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
					

					
					if(!cdle.isDirty())
					{
//						ImageButton powerView = (ImageButton)v.findViewById(R.id.controllable_device_list_item_powerIconView);

						holder.POWER_BUTT = new ImageButton(mContext);	
						holder.POWER_BUTT.setOnClickListener(this);
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
					}
					else
					{
						//						LayoutInflater inflater = mLi.in
//						ProgressBar pg = (ProgressBar ) mLi.inflate(R.layout.dirty_progressbar, null);
						//						pg.setIndeterminate(true);

						holder.CONTAINER.removeAllViews();
						holder.CONTAINER.addView(holder.DIRTY_BAR);
					}

					if(null!=holder.CB)
					{
						holder.CB.setOnCheckedChangeListener(null);
						holder.CB.setChecked(cdle.isSelected());

						holder.CB.setOnCheckedChangeListener(this);
						//the ControllableDevice associated with the checkbox is set as tag for the checkbox to 
						//be able to determine which device was selected in onCheckedChanged
						holder.CB.setTag(cd);
					}
					else
					{
						Log.e(TAG, "checkbox was null");
					}

					holder.NAME_LABEL.setText(cd.getHostname());

					holder.IP_LABEL.setText(cd.getIp());

//					idleLabel.setText(mContext.getString(R.string.ControllableDeviceAdapter_idleLabel_text)+cd.getIdleSince());
					holder.IDLE_LABEL.setText(cd.getIdleString());
					long duration = System.currentTimeMillis()-start;
//					Log.e(TAG, "view generation took:"+duration+"ms");
				}
				else
				{
					Log.e(TAG, "cd was null");
				}
			}
		}

		return rowView;
	}

	@Override
	public void onClick(View arg0) 
	{
		ControllableDevice cd = extractDeviceFromTag(arg0);
		mUiHelper.handlePowerClick(cd);
//		mUi.handlePowerClick(cd);
//		if(cd.isAlive())
//		{
//			PMSDialogFactory.showDialog(DialogType.ACTIVE_DEVICE_ACTION_DIALOG, _fm, _handler, _params)
//		}

	}

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
			sle.setSelected(selected);
			buttonView.setChecked(selected);
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
	
	

}