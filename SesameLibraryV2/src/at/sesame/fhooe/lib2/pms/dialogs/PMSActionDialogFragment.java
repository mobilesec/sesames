package at.sesame.fhooe.lib2.pms.dialogs;

import java.util.ArrayList;

import android.app.Dialog;
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

	public enum PMSDialogType
	{
		ActiveDeviceActionDialog,
		InactiveDeviceActionDialog,
	}

	public PMSActionDialogFragment(final IPMSDialogActionHandler _handler, final ControllableDevice _cd, PMSDialogType _type)
	{
		mDialog = new Dialog(getActivity());
		mDialog.setContentView(R.layout.custom_action_dialog);
		mDialog.setTitle(_cd.getHostname());
		final ArrayList<CommandListEntry> cles = new ArrayList<CommandListEntry>();
		ListView commands = (ListView)mDialog.findViewById(R.id.cusomtActionDialogCommandList);
		commands.setAdapter(new CommandAdapter(getActivity(), cles));
		TextView message = (TextView)mDialog.findViewById(R.id.messageLabel);
		commands.setBackgroundColor(android.R.color.white);
		switch(_type)
		{
		case ActiveDeviceActionDialog:
			if(_cd.getIdleSinceMinutes()<IDLE_MINUTES_WARNING_THRESHOLD)
			{

				message.setText(getString(R.string.PMSClientActivity_activeDeviceActionDialogBaseMessage)+_cd.getIdleSinceMinutes()+")");
			}
			//			strings.add(getString(R.string.PMSClientActivity_activeDeviceDialogShutDownCommand));
			//			strings.add(getString(R.string.PMSClientActivity_activeDeviceDialogSleepCommand));
			//			strings.add(getString(android.R.string.cancel));
			cles.add(new CommandListEntry(getActivity(), CommandType.shutDown));
			cles.add(new CommandListEntry(getActivity(), CommandType.sleep));
			cles.add(new CommandListEntry(getActivity(), CommandType.cancel));

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
			cles.add(new CommandListEntry(getActivity(), CommandType.wakeUp));
			cles.add(new CommandListEntry(getActivity(), CommandType.cancel));
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
	}
}
