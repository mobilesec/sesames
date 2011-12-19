package at.sesame.fhooe.service.error;

import org.codegist.crest.handler.ErrorHandler;
import org.codegist.crest.io.Request;
import org.codegist.crest.io.RequestException;

import android.util.Log;

public class EnergyDataErrorHandler implements ErrorHandler 
{
	private static final String TAG = "EnergyDataErrorHandler";
	@Override
	public <T> T handle(Request arg0, Exception arg1) throws Exception 
	{
		RequestException reqEx = null;
		if(arg1 instanceof RequestException)
		{
			reqEx = (RequestException)arg1;
			Log.e(TAG,"return code:" +((RequestException) arg1).getResponse().getStatusCode());
		}
		else
		{
			Log.e(TAG, "passed argument was no request exception");
		}
		return null;
	}

}
