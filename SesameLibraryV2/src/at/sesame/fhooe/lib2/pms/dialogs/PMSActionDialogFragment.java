package at.sesame.fhooe.lib2.pms.dialogs;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import at.sesame.fhooe.lib2.R;
import at.sesame.fhooe.lib2.pms.list.commands.CommandAdapter;
import at.sesame.fhooe.lib2.pms.list.commands.CommandListEntry;
import at.sesame.fhooe.lib2.pms.list.commands.CommandListEntry.CommandType;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;

public class PMSActionDialogFragment 
extends DialogFragment 
{
	/**
	 * threshold after which an idle time warning is displayed
	 */
	private static final int IDLE_MINUTES_WARNING_THRESHOLD = 30;

	private Dialog mDialog;

	public enum PMSActionDialogType
	{
		ActiveDeviceActionDialog,
		InactiveDeviceActionDialog,
	}

	public PMSActionDialogFragment(Context _ctx, final IPMSDialogActionHandler _handler, final ControllableDevice _cd, PMSActionDialogType _type)
	{
		mDialog = new Dialog(_ctx);
		mDialog.setContentView(R.layout.custom_action_dialog);
		mDialog.setTitle(_cd.getHostname());
		ArrayList<CommandListEntry> cles = new ArrayList<CommandListEntry>();
		ListView commands = (ListView)mDialog.findViewById(R.id.cusomtActionDialogCommandList);

		TextView message = (TextView)mDialog.findViewById(R.id.messageLabel);
		switch(_type)
		{
		case ActiveDeviceActionDialog:
			if(_cd.getIdleSinceMinutes()<IDLE_MINUTES_WARNING_THRESHOLD)
			{

				message.setText(_ctx.getString(R.string.PMSClientActivity_activeDeviceActionDialogBaseMessage)+_cd.getIdleSinceMinutes()+")");
			}
			//			strings.add(getString(R.string.PMSClientActivity_activeDeviceDialogShutDownCommand));
			//			strings.add(getString(R.string.PMSClientActivity_activeDeviceDialogSleepCommand));
			//			strings.add(getString(android.R.string.cancel));
			cles.add(new CommandListEntry(_ctx, CommandType.shutDown));
			cles.add(new CommandListEntry(_ctx, CommandType.sleep));
			cles.add(new CommandListEntry(_ctx, CommandType.cancel));

			commands.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch(arg2)
					{
					case 0:
						//						mSelectedDevice.powerOff(PowerOffState.shutdown);
						//						markDirty(mSelectedDevice);
						_handler.handleDialogPowerOff(_cd);
						mDialog.dismiss();
						break;
					case 1:
						//						mSelectedDevice.powerOff(PowerOffState.sleep);
						//						markDirty(mSelectedDevice);
						_handler.handleDialogSleep(_cd);
						mDialog.dismiss();
						break;
					case 2:
						mDialog.cancel();
						break;
					}
				}
			});	
			break;

		case InactiveDeviceActionDialog:
			//			strings.add(getString(R.string.PMSClientActivity_inactiveDeviceDialogWakeUpCommand));
			//			strings.add(getString(android.R.string.cancel));
			cles.add(new CommandListEntry(_ctx, CommandType.wakeUp));
			cles.add(new CommandListEntry(_ctx, CommandType.cancel));
			commands.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch(arg2)
					{
					case 0:
						//						mSelectedDevice.wakeUp();
						//						markDirty(mSelectedDevice);
						_handler.handleDialogWakeUp(_cd);
						mDialog.dismiss();
						break;
					case 1:
						mDialog.cancel();
						break;
					}
				}
			});
			break;
		}
		
		
		commands.setAdapter(new CommandAdapter(_ctx, cles));
		commands.setBackgroundColor(android.R.color.white);
	}
}
