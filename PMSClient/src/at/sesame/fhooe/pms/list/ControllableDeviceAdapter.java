package at.sesame.fhooe.pms.list;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
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
				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setOnTouchListener(null);
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
						
						
						if(null!=powerView)
						{
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
						}
						else
						{
							Log.e(TAG, "powerView was null");
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
		buttonView.setOnCheckedChangeListener(null);
		ControllableDevice cd = extractDeviceFromTag(buttonView);
		if(null!=cd)
		{
			boolean selected = mOwner.handleSelectionAttempt(cd, isChecked);
			if(selected)
			{
				buttonView.setChecked(true);
			}
			else
			{
				buttonView.setChecked(false);
			}
		}

		buttonView.setOnCheckedChangeListener(this);

	}

	private ControllableDevice extractDeviceFromTag(View _v)
	{
		Object o = _v.getTag();
		if(o instanceof ControllableDevice)
		{
			return (ControllableDevice)o;
		}
		return null;
	}

}
