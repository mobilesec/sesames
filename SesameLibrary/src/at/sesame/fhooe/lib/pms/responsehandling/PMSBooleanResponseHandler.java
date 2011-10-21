/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms.responsehandling;

import org.codegist.crest.handler.ResponseHandler;
import org.codegist.crest.io.Response;

/**
 * whenever the response of a GET-call results in an status code <400
 * this class returns true to indicate that the call was successful
 *
 */
public class PMSBooleanResponseHandler 
implements ResponseHandler 
{	
	@Override
	public Object handle(Response arg0) throws Exception 
	{
		return new Boolean(true);
	}

}
