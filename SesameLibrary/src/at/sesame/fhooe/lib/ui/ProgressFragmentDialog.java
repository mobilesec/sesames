package at.sesame.fhooe.lib.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class ProgressFragmentDialog 
extends DialogFragment
{
	private static final String TITLE_KEY = "title";
	private static final String MESSAGE_KEY = "message";
	
	public static ProgressFragmentDialog newInstance(String _title, String _message) {
        ProgressFragmentDialog frag = new ProgressFragmentDialog();
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, _title);
        args.putString(MESSAGE_KEY, _message);
        frag.setArguments(args);
        return frag;
    }
	
	@Override
	public Dialog onCreateDialog(Bundle _args)
	{
		ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setTitle(getArguments().getString(TITLE_KEY));
		pd.setMessage(getArguments().getString(MESSAGE_KEY));
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
		
		pd.setIndeterminate(true);
		return pd;
	}


}
