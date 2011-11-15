/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms.errorhandling.handlers;

import java.lang.reflect.Type;
import java.util.List;

import org.codegist.crest.config.ParamType;
import org.codegist.crest.handler.ErrorHandler;
import org.codegist.crest.io.Request;
import org.codegist.crest.io.RequestException;
import org.codegist.crest.io.Response;
import org.codegist.crest.param.Param;

import android.util.Log;
import at.sesame.fhooe.lib.pms.errorhandling.ErrorForwarder;
import at.sesame.fhooe.lib.pms.errorhandling.IErrorReceiver.RequestType;

/**
 * abstract base class for ErrorHandlers. functionality common to all ErrorHandlers is
 * implemented here.  
 */
public abstract class PMSErrorHandler
implements ErrorHandler
{
	/**
	 * the tag to identify the logger output of this class
	 */
	private static final String TAG = "PMSErrorHandler";
	
	/**
	 * instance of ErrorForwarder that informs all registered ErrorReceivers about
	 * errors caught here
	 */
	private ErrorForwarder mErrorForwarder = ErrorForwarder.getInstance();

	/**
	 * message for HTTP status code 400
	 */
	protected static final String PMSErrorMessage400 = "invalid target-state or JSON-data invalid";
	
	/**
	 * message for HTTP status code 401
	 */
	protected static final String PMSErrorMessage401 = "authorization on target computer failed";
	
	/**
	 * message for HTTP status code 404
	 */
	protected static final String PMSErrorMessage404 = "ID not known";
	
	/**
	 * message for HTTP status code 406
	 */
	protected static final String PMSErrorMessage406 = "os not supported/found";
	
	/**
	 * message for HTTP status code 410
	 */
	protected static final String PMSErrorMessage410 = "the selected device is currently not reachable";
	
	/**
	 * message for HTTP status code 500
	 */
	protected static final String PMSErrorMessage500 = "general HTTP error occured";
	
	/**
	 * message for HTTP status code 501
	 */
	protected static final String PMSErrorMessage501 = "the command is not supported by the target operating system";
	
	/**
	 * message for unrecognized HTTP status codes
	 */
	protected static final String PMSErrorMessageCodeNotRecognized = "HTTP status code not recognized";
	
	private static final String MAC_PARAM_NAME = "mac";


	@Override
	public <T> T handle(Request arg0, Exception arg1) throws Exception 
	{
//		Log.e(TAG, "return type:"+this.);
		String methodName = arg0.getMethodConfig().getMethod().getName();
		Log.e(TAG, methodName);
		List<Param> params = arg0.getParams(ParamType.PATH);
		Log.e(TAG, "params:\n");
		String mac = "";
		for(Param p:params)
		{
			if(p.getParamConfig().getName().equals(MAC_PARAM_NAME))
			{
				mac = (String)p.getValue().toArray()[0];
			}
		}
		RequestType type = RequestType.unknown;
		try
		{
			type = RequestType.valueOf(methodName);
		}
		catch(Exception _e)
		{
			
		}
		if(null==arg1)
		{
			Log.e(TAG, "passed exception was null");
			return null;
		}
		if(null==arg1.getMessage())
		{
			Log.e(TAG, "message was null");
		}
		else
		{
			Log.e(TAG, arg1.getLocalizedMessage());
		}

		if(arg1 instanceof RequestException)
		{
			try
			{
				RequestException reqEx = (RequestException)arg1;
				Response resp = reqEx.getResponse();
//				Type t = resp.getExpectedGenericType();
//				Log.e(TAG, "expectedGenericType="+t.toString());
				int code = resp.getStatusCode();
				Log.e(TAG, "HTTP ERROR code: "+code);
				StringBuilder sb = new StringBuilder();
				sb.append(code);
				sb.append(": ");

				handleHttpError(sb, code);

				mErrorForwarder.notifyError(type, mac,code, sb.toString());
			}
			catch(NullPointerException _npe)
			{
				return null;
			}
		}
		else
		{
			mErrorForwarder.notifyError(type, mac,-1,arg1.getMessage());
		}
		return null;
	}
	
	/**
	 * this method is called when the handle method of the PMSErrorHandler recognizes
	 * that an error originated from a HTTP status code >=400. the concrete implementations
	 * of this class process the identified status code
	 * @param _msg a StringBuilder to append the correct message to 
	 * @param _code the status code of the HTTP response
	 */
	protected abstract void handleHttpError(StringBuilder _msg, int _code);
}
