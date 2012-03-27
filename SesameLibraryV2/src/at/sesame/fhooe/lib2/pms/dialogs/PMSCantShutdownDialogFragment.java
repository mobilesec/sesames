package at.sesame.fhooe.lib2.pms.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import at.sesame.fhooe.lib2.R;


public class PMSCantShutdownDialogFragment 
extends DialogFragment 
{
	private Dialog mDialog;
	
	public PMSCantShutdownDialogFragment()
	{
		AlertDialog.Builder shutDownBuilder = new AlertDialog.Builder(getActivity());
		shutDownBuilder.setCancelable(true);
		shutDownBuilder.setTitle("");
		shutDownBuilder.setMessage(R.string.PMSClientActivity_cantShutDownDialogMessage);

		shutDownBuilder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		mDialog = shutDownBuilder.create();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return mDialog;
	}

	
}
