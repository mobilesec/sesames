package at.sesame.fhooe.lib.pms.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import at.sesame.fhooe.lib.R;

public class PMSNoNetworkDialogFragment 
extends DialogFragment 
{
	private Dialog mDialog;
	
	public PMSNoNetworkDialogFragment()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCancelable(true);
		builder.setTitle(R.string.PMSClientActivity_noNetworkDialogTitle);
		builder.setMessage(R.string.PMSClientActivity_noNetworkDialogMessage);

		builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		mDialog = builder.create();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return mDialog;
	}
	
	
}
