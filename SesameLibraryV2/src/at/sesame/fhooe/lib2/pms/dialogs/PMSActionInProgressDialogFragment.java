package at.sesame.fhooe.lib2.pms.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PMSActionInProgressDialogFragment 
extends DialogFragment 
{
	public static final String TITLE_BUNDLE_KEY = "title";
	public static final String MAX_BUNDLE_KEY = "max";
	
	private ProgressDialog mProgressDialog;
	
	public PMSActionInProgressDialogFragment()
	{
		mProgressDialog = new ProgressDialog(getActivity());
		//		mActionInProgressDialog.setMessage(getString(R.string.PMSClientActivity_networkingProgressDialogTitle));
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}
	
//	public void setMax(int _max)
//	{
//		mProgressDialog.setMax(_max);
//	}
	
	public void incrementProgressBy(int _val)
	{
		mProgressDialog.incrementProgressBy(_val);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString(TITLE_BUNDLE_KEY);
		int max = getArguments().getInt(MAX_BUNDLE_KEY);
		mProgressDialog.setTitle(title);
		mProgressDialog.setMax(max);
		return mProgressDialog;
	}

	
}
