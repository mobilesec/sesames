package at.sesame.fhooe.pms.list.controllabledevice;

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
import at.sesame.fhooe.pms.PMSClientActivity;
import at.sesame.fhooe.pms.R;

public class ControllableDeviceAdapter 
extends ArrayAdapter<IListEntry>
implements OnClickListener, OnCheckedChangeListener
{
	private static final String TAG = "ControllableDeviceAdapter";
	private Context mContext = null;
	private ArrayList<IListEntry> mDevs = null;
	private LayoutInflater mLi;
	private PMSClientActivity mOwner;


	private static final int SINGLE_SELECTION = 0;
	private static final int GROUP_SELECTION = 1;

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
				separatorCb.setTag(sep);
				
				//				switch(sep.getType())
				//				{
				//				case active:
				//					Log.e(TAG, "processing active separator");
				////					if(null==mActiveCheckBox)
				//					{
				//						Log.e(TAG, "active checkbox was null");
				//						mActiveCheckBox = separatorCb;
				////						mActiveCheckBox.setOnCheckedChangeListener(this);
				//					}
				//					break;
				//				case inactive:
				//					Log.e(TAG, "processing inactive separator");
				////					if(null==mInactiveCheckBox)
				//					{
				//						Log.e(TAG, "inactive checkbox was null");
				//						mInactiveCheckBox = separatorCb;
				////						mInactiveCheckBox.setOnCheckedChangeListener(this);
				//					}
				//					break;
				//				}
				//				v.setOnClickListener(null);
				//				v.setOnLongClickListener(null);
				//				v.setOnTouchListener(null);
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
					idleLabel.setText(mContext.getString(R.string.ControllableDeviceAdapter_idleLabel_text)+cd.getIdleSince());
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
		Log.e(TAG, "checkedChanged");
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
			Log.e(TAG, "result of handleMultipleSelection="+selected);
			break;
		}
		//		if(buttonView.equals(mActiveCheckBox))
		//		{
		//			boolean selected = mOwner.handleMultipleSelectionAttempt(ListType.active, isChecked);
		//			buttonView.setChecked(selected);
		//			Log.e(TAG, "result of handleMultipleSelection="+selected);
		//		}
		//		else if(buttonView.equals(mInactiveCheckBox))
		//		{
		//			boolean selected = mOwner.handleMultipleSelectionAttempt(ListType.inactive, isChecked);
		//			buttonView.setChecked(selected);
		//			Log.e(TAG, "result of handleMultipleSelection="+selected);
		//		}
		//		else
		//		{
		//			handleSingleSelection(buttonView, isChecked);
		//		}

		buttonView.setOnCheckedChangeListener(this);
	}

	private void handleSingleSelection(CompoundButton buttonView, boolean isChecked)
	{
		ControllableDevice cd = extractDeviceFromTag(buttonView);
		if(null!=cd)
		{
			boolean selected = mOwner.handleSingleSelectionAttempt(cd, isChecked);
			buttonView.setChecked(selected);
		}
	}

	private void handleMultipleSelection(CompoundButton buttonView, boolean isChecked)
	{
		SeparatorListEntry sle = extractSeparatorFromTag(buttonView);
		boolean selected = mOwner.handleMultipleSelectionAttempt(sle.getType(), isChecked);
		//		buttonView.setChecked(selected);
		//		Log.e(TAG, "result of handleMultipleSelection="+selected);

	}

	private ControllableDevice extractDeviceFromTag(View _v)
	{
		Object o = _v.getTag();
		return (ControllableDevice)o;
	}

	private SeparatorListEntry extractSeparatorFromTag(View _v)
	{
		Object o = _v.getTag();
		return (SeparatorListEntry)o;
	}

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
