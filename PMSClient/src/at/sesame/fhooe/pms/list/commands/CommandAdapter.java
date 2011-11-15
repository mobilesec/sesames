package at.sesame.fhooe.pms.list.commands;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.sesame.fhooe.pms.R;

public class CommandAdapter 
extends ArrayAdapter<CommandListEntry> 
{
	private LayoutInflater mLi;

	public CommandAdapter(Context context,List<CommandListEntry> objects) 
	{
		super(context, 0, objects);
		mLi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// TODO Auto-generated constructor stub
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
