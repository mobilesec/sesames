package at.sesame.fhooe.lib2.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.sesame.fhooe.lib2.R;


public class LoginDialogFragment 
extends DialogFragment 
{
	private ILoginListener mLoginListener;
	
	
	
	
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog d = new Dialog(getActivity());
		d.setContentView(R.layout.login_dialog_layout);
		
		final EditText userField = (EditText)d.findViewById(R.id.login_dialog_user_field);
		final EditText pwdField = (EditText)d.findViewById(R.id.login_dialog_layout_password_field);
		
		Button cancelButt = (Button)d.findViewById(R.id.login_dialog_layout_cancel_butt);
		cancelButt.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) {
				d.dismiss();
				
			}
		});
		
		Button loginButt = (Button)d.findViewById(R.id.login_dialog_layout_login_butt);
		loginButt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				String user = userField.getText().toString();
				String pass = pwdField.getText().toString();
				if(null==mLoginListener)
				{
					toastMessage("could not check login");
					return;
				}
				
				if(mLoginListener.checkLogin(user, pass))
				{
					toastMessage("login successful");
					d.dismiss();
				}
				else
				{
					toastMessage("login failed");
				}
				
			}
		});
		
		return d;
	}
	
	public void show(FragmentManager _fragMan, ILoginListener _listener)
	{
		mLoginListener = _listener;
		super.show(_fragMan, null);
	}


	private void toastMessage(String _msg)
	{
		Toast.makeText(getActivity(), _msg, Toast.LENGTH_LONG).show();
	}
}
