package at.sesame.fhooe.viewexperiments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

class AppQuitDialog 
extends android.support.v4.app.DialogFragment 
{
	private static final String TAG = "AppQuitDialog";
	private static final String TITLE_KEY = "title";
	private Context mContext;
	private String mQuitKey;
	
	private AppQuitDialog(String _quitKey)
	{
		mQuitKey = _quitKey;
	}
	
	public static AppQuitDialog newInstance(String _title, String _quitKey) {
        AppQuitDialog frag = new AppQuitDialog(_quitKey);
        Bundle args = new Bundle();
        args.putString(TITLE_KEY, _title);
        frag.setArguments(args);
        return frag;
    }
	
	@Override
	public Dialog onCreateDialog(Bundle _args)
	{	
		Log.e(TAG, "creating dialog");
		final Dialog d = new Dialog(getActivity());
		d.setTitle(getArguments().getString(TITLE_KEY));
		d.setContentView(R.layout.app_quit_dialog_layout);
		
		final TextView tv = (TextView)d.findViewById(R.id.textView1);
		tv.setText("Bitte das Kennwort zum Beenden der Applikation eingeben.");
		
		final EditText edit = (EditText)d.findViewById(R.id.editText1);
		
		Button ok = (Button)d.findViewById(R.id.quitDialogOkButt);
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e(TAG, mQuitKey);
				Log.e(TAG, edit.getText().toString());
				if(edit.getText().toString().equals(mQuitKey))
				{
					getActivity().finish();
				}
				else
				{
					Toast.makeText(getActivity(), "Falsches Kennwort eingegeben.", Toast.LENGTH_SHORT).show();
				}
				
			}
		}); 
		
		Button cancel = (Button)d.findViewById(R.id.quitDialogCancelButt);
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				d.dismiss();
				
			}
		});
				
		return d;
	}

}
