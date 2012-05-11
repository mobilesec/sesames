package at.sesame.fhooe.lib2.pms.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import at.sesame.fhooe.lib2.R;


public class PMSNetworkingInProgressDialogFragment 
extends DialogFragment 
{
	private static final String TAG = "PMSNetworking...";
//	private ProgressDialog mDialog;
//	private Context  mCtx;;
//	public PMSNetworkingInProgressDialogFragment(Context _ctx)
//	{
//		mCtx = _ctx;
//	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.e(TAG, "creating progress dialog...");
		// TODO Auto-generated method stub
		ProgressDialog pd = new ProgressDialog(this.getActivity());
		pd.setMessage(getActivity().getString(R.string.PMSClientActivity_networkingProgressDialogTitle));
//		mNetworkingDialog.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
		//		mNetworkingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setIndeterminate(true);
		return pd;
	}
	
	
//	public void setMax(int _max)
//	{
//		mDialog.setMax(_max);
//	}
}
