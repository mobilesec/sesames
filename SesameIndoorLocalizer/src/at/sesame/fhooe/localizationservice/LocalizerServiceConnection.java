package at.sesame.fhooe.localizationservice;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class LocalizerServiceConnection 
implements ServiceConnection 
{
	private ILocalizationService mService = null;
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) 
	{
		@SuppressWarnings("unused")
		ILocalizationService mService = ILocalizationService.Stub.asInterface(arg1);
		Log.e("LocalizerServiceConnection", "binding service");
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
	}
	
	public ILocalizationService getService()
	{
		return mService;
	}

}
