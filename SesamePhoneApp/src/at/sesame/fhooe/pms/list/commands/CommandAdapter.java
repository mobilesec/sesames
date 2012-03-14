/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 11/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.pms.list.commands;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.sesame.fhooe.phone.R;

public class CommandAdapter 
extends ArrayAdapter<CommandListEntry> 
{
	/**
	 * the owning activity's layout inflater
	 */
	private LayoutInflater mLi;

	/**
	 * creates a new CommandAdapter
	 * @param context the context of the owning activity
	 * @param objects the model of the list
	 */
	public CommandAdapter(Context context,List<CommandListEntry> objects) 
	{
		super(context, 0, objects);
		mLi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) 
	{
		CommandListEntry cle = getItem(arg0);
		View v = mLi.inflate(R.layout.command_list_entry, null);
		ImageView iv = (ImageView) v.findViewById(R.id.command_list_entry_imageView);
		iv.setImageDrawable(cle.getIcon());
		
		TextView tv = (TextView)v.findViewById(R.id.command_list_entry_textView);
		tv.setText(cle.getTitle());
		// TODO Auto-generated method stub
		return v;
	}

}
