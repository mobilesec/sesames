/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib2.pms.errorhandling.handlers;


import org.codegist.crest.handler.ErrorHandler;
import org.codegist.crest.io.Request;
import org.codegist.crest.io.RequestException;

import android.util.Log;

/**
 * handles errors that occur during requests that only produce boolean results
 * (fail or success)
 *
 */
public class PMSBooleanErrorHandler
implements ErrorHandler
{
	private static final String TAG = "BooleanErrorHandler";
	@SuppressWarnings("unchecked")
	@Override
	public <T> T handle(Request arg0, Exception arg1) throws Exception 
	{
		Log.e(TAG, arg0.toString());
		if(arg1 instanceof RequestException)
		{
			RequestException rex = (RequestException)arg1;
			Log.e(TAG, "HTTP ERROR code: "+rex.getResponse().getStatusCode());
		}
//		return  (T) new Boolean(false);
//		return (T) String.valueOf(false);
		return (T)new Object();
	}

//	@Override
//	protected void handleHttpError(StringBuilder _msg, int _code) {
//		switch(_code)
//		{
//		case 401:
//			_msg.append(PMSErrorMessage401);
//			break;
//		case 404:
//			_msg.append(PMSErrorMessage404);
//			break;
//		case 500:
//			_msg.append(PMSErrorMessage500);
//			break;
//		default:
//			_msg.append(PMSErrorMessageCodeNotRecognized);
//				
//		}
//		
//	}



}
