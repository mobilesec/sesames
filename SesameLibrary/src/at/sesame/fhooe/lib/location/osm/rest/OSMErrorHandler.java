package at.sesame.fhooe.lib.location.osm.rest;

import org.codegist.crest.handler.ErrorHandler;
import org.codegist.crest.io.Request;
import org.codegist.crest.io.RequestException;

import android.util.Log;

public class OSMErrorHandler 
implements ErrorHandler 
{
	private static final String TAG = "OSMErrorHandler";

	@Override
	public <T> T handle(Request arg0, Exception arg1) throws Exception {
		Log.e(TAG, arg0.getMethodConfig().getMethod().getName());
//		Log.e(TAG, arg1.);
		arg1.printStackTrace();
		if(arg1 instanceof RequestException)
		{
			RequestException re = (RequestException)arg1;
			Log.e(TAG, re.getResponse().getStatusCode()+" "+re.getMessage());
		}
		return (T) "error";
	}

}
