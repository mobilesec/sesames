package at.sesame.fhooe.lib2.pms.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import at.sesame.fhooe.lib2.R;


public class PMSNetworkingInProgressDialogFragment 
extends DialogFragment 
{
	private ProgressDialog mDialog;
	public PMSNetworkingInProgressDialogFragment(Context _ctx)
	{
		mDialog = new ProgressDialog(_ctx);
		mDialog.setMessage(_ctx.getString(R.string.PMSClientActivity_networkingProgressDialogTitle));
//		mNetworkingDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		//		mNetworkingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setIndeterminate(true);
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		return mDialog;
	}
	
	
	public void setMax(int _max)
	{
		mDialog.setMax(_max);
	}
}
