package at.sesame.fhooe.pms.list.commands;

import android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class CommandListEntry 
{
	
	public enum CommandType
	{
		shutDown,
		wakeUp,
		sleep,
		cancel
	}
	
	private String mTitle;
	private Drawable mIcon;

	public CommandListEntry(Context _c, CommandType _type)
	{
		switch(_type)
		{
		case shutDown:
			mTitle = _c.getString(at.sesame.fhooe.pms.R.string.PMSClientActivity_activeDeviceDialogShutDownCommand);
			mIcon = _c.getResources().getDrawable(at.sesame.fhooe.pms.R.drawable.ic_menu_power);
			break;
		case wakeUp:
			mTitle = _c.getString(at.sesame.fhooe.pms.R.string.PMSClientActivity_inactiveDeviceDialogWakeUpCommand);
			mIcon = _c.getResources().getDrawable(at.sesame.fhooe.pms.R.drawable.ic_menu_power);
			break;
		case sleep:
			mTitle = _c.getString(at.sesame.fhooe.pms.R.string.PMSClientActivity_activeDeviceDialogSleepCommand);
			mIcon = _c.getResources().getDrawable(at.sesame.fhooe.pms.R.drawable.ic_menu_sleep);
			break;
		case cancel:
			mTitle = _c.getString(R.string.cancel);
			mIcon = _c.getResources().getDrawable(at.sesame.fhooe.pms.R.drawable.ic_menu_cancel);
			break;
		}
	}

	public String getTitle() {
		return mTitle;
	}

	public Drawable getIcon() {
		return mIcon;
	}	
}
