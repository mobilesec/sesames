package at.sesame.fhooe.lib2.pms.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import at.sesame.fhooe.lib2.R;

public class PMSCantWakeUpDialogFragment 
extends DialogFragment 
{
	private Dialog mDialog;
	
	public PMSCantWakeUpDialogFragment(String _title)
	{
		AlertDialog.Builder wakeUpBuilder = new AlertDialog.Builder(getActivity());
		wakeUpBuilder.setCancelable(true);
		wakeUpBuilder.setTitle(_title);
		wakeUpBuilder.setMessage(R.string.PMSClientActivity_cantWakeUpDialogMessage);

		wakeUpBuilder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		mDialog = wakeUpBuilder.create();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return mDialog;
	}

	
}
