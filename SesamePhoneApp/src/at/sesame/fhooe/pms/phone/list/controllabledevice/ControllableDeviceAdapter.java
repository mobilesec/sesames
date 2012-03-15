/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 11/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.pms.phone.list.controllabledevice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
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
import at.sesame.fhooe.lib.pms.model.ControllableDevice;
import at.sesame.fhooe.phone.R;
import at.sesame.fhooe.phone.pms.PMSClientActivity;

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
	 * the activity that owns this adapter
	 */
	private PMSClientActivity mOwner;


	/**
	 * integer constant for single selection
	 */
	private static final int SINGLE_SELECTION = 0;
	
	/**
	 * integer constant for group selection
	 */
	private static final int GROUP_SELECTION = 1;

	/**
	 * creates a new ControllableDeviceAdapter
	 * @param _owner the owner of the adapter
	 * @param objects the model to be shown via the adapter
	 */
	public ControllableDeviceAdapter(PMSClientActivity _owner, List<IListEntry> objects) 
	{
		super(_owner, 0, objects);
		mContext = _owner;
		mOwner = _owner;
		mDevs = (ArrayList<IListEntry>)objects;
		mLi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int _pos, View _convertView, ViewGroup _parent)
	{
		View v = _convertView;
		IListEntry item = mDevs.get(_pos);
		if(null!=item)
		{
			if(item.isSeparator())
			{
				SeparatorListEntry sep = (SeparatorListEntry)item;
				v = mLi.inflate(R.layout.controllable_device_listseparator, null);


				TextView tv = (TextView)v.findViewById(R.id.separatorNameLabel);
				tv.setText(sep.getTitle());

				CheckBox separatorCb = (CheckBox)v.findViewById(R.id.separatorCheckBox);
				separatorCb.setChecked(sep.isSelected());
				separatorCb.setOnCheckedChangeListener(this);
				//the separator associated with the checkbox is set as tag for the checkbox to 
				//be able to determine which separator was selected in onCheckedChanged
				separatorCb.setTag(sep);
			}
			else
			{
				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)item;
				ControllableDevice cd = cdle.getControllableDevice();

				if(cdle.isSelected())
				{
					v = mLi.inflate(R.layout.controllable_device_listitem_selected, null);
				}
				else
				{
					v = mLi.inflate(R.layout.controllable_device_listitem_not_selected, null);
				}

				if(null!=cd)
				{
					ImageView osView = (ImageView)v.findViewById(R.id.osIconView);

					switch(cd.getOs())
					{
					case windows:
						osView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_list_windows));
						break;
					case linux:
						osView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_list_linux));
						break;
					case mac:
						osView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_list_mac));
						break;
					case unknown:
						osView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_list_unknown));
						break;
					}
					FrameLayout container = (FrameLayout)v.findViewById(R.id.controllable_device_list_item_placeholder);
					if(cdle.isDirty())
					{
						//						LayoutInflater inflater = mLi.in
						ProgressBar pg = (ProgressBar ) mLi.inflate(R.layout.dirty_progressbar, null);
						//						pg.setIndeterminate(true);

						container.addView(pg);
					}
					else
					{
						//						ImageButton powerView = (ImageButton)v.findViewById(R.id.controllable_device_list_item_powerIconView);
						ImageButton powerView = new ImageButton(mContext);

						powerView.setOnClickListener(this);
						powerView.setTag(cd);
						powerView.setBackgroundColor(Color.TRANSPARENT);

						if(cd.isAlive())
						{
							powerView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_power_on));
						}
						else
						{
							powerView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_power_off));
						}

						container.addView(powerView);
					}

					CheckBox cb = (CheckBox)v.findViewById(R.id.controllable_device_list_item_selection_box);
					if(null!=cb)
					{
						cb.setOnCheckedChangeListener(null);
						if(cdle.isSelected())
						{
							cb.setChecked(true);
						}

						cb.setOnCheckedChangeListener(this);
						//the ControllableDevice associated with the checkbox is set as tag for the checkbox to 
						//be able to determine which device was selected in onCheckedChanged
						cb.setTag(cd);
					}
					else
					{
						Log.e(TAG, "checkbox was null");
					}

					TextView nameLabel =(TextView) v.findViewById(R.id.nameLabel);
					nameLabel.setText(cd.getHostname());

					TextView ipLabel = (TextView)v.findViewById(R.id.ipLabel);
					ipLabel.setText(cd.getIp());

					TextView idleLabel = (TextView)v.findViewById(R.id.idleLabel);
//					idleLabel.setText(mContext.getString(R.string.ControllableDeviceAdapter_idleLabel_text)+cd.getIdleSince());
					idleLabel.setText(cd.getIdleString());
				}
			}
		}

		return v;
	}

	@Override
	public void onClick(View arg0) 
	{
		ControllableDevice cd = extractDeviceFromTag(arg0);
		if(null!=cd)
		{
			mOwner.handlePowerClick(cd);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		buttonView.setOnCheckedChangeListener(null);
		int type = identifyCheckSource(buttonView);
		switch(type)
		{
		case SINGLE_SELECTION:
			handleSingleSelection(buttonView, isChecked);
			break;
		case GROUP_SELECTION:
			SeparatorListEntry sle = extractSeparatorFromTag(buttonView);
			
			boolean selected = mOwner.handleMultipleSelectionAttempt(sle.getType(), isChecked);
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
			boolean selected = mOwner.handleSingleSelectionAttempt(cd, isChecked);
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
