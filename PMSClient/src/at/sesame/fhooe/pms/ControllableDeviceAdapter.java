package at.sesame.fhooe.pms;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.sesame.fhooe.lib.pms.model.ControllableDevice;

public class ControllableDeviceAdapter 
extends ArrayAdapter<IListEntry> 
{
	private Context mContext = null;
	private ArrayList<IListEntry> mDevs = null;
	private LayoutInflater mLi;
	
	public ControllableDeviceAdapter(Context context, List<IListEntry> objects) 
	{
		super(context, 0, objects);
		mContext = context;
		mDevs = (ArrayList<IListEntry>)objects;
		mLi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public View getView(int _pos, View _convertView, ViewGroup _parent)
	{
		View v = _convertView;
//        if (v == null) 
//        {

//            v = li.inflate(R.layout.controllable_device_listitem, null);
//        }
//        ControllableDevice cd = mDevs.get(_pos);
		IListEntry item = mDevs.get(_pos);
		if(null!=item)
		{
			if(item.isSeparator())
			{
				SeparatorListEntry sep = (SeparatorListEntry)item;
				v = mLi.inflate(R.layout.controllable_device_listseparator, null);
				TextView tv = (TextView)v.findViewById(R.id.separatorNameLabel);
				tv.setText(sep.getTitle());
			}
			else
			{
				ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)item;
				ControllableDevice cd = cdle.getControllableDevice();
	            v = mLi.inflate(R.layout.controllable_device_listitem, null);
				if(null!=cd)
		        {
		        	TextView nameLabel =(TextView) v.findViewById(R.id.nameLabel);
		        	nameLabel.setText(cd.getHostname());
		        	
		        	TextView ipLabel = (TextView)v.findViewById(R.id.ipLabel);
		        	ipLabel.setText(cd.getIp());
		        	
		        	TextView idleLabel = (TextView)v.findViewById(R.id.idleLabel);
		        	idleLabel.setText("Idle since:"+cd.getIdleSince());
		        }
			}
		}
        
        return v;
	}

}
