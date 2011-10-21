/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms.entitywriter;

import java.io.OutputStream;
import java.util.List;

import org.codegist.crest.config.ParamType;
import org.codegist.crest.entity.EntityWriter;
import org.codegist.crest.io.Request;
import org.codegist.crest.param.Param;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * this class implements the interface EntityWriter used by the CRest
 * library to write data to the body of a http-request.
 *
 */
public class PMSEntityWriter
implements EntityWriter
{
	/**
	 * a byte array representation of the data to be sent in the body
	 */
	private byte[] mData;
	
	/**
	 * creates a json object from the form parameters of the passed request
	 * and stores the binary representation in the mData field
	 * @param _req the http request to parse parameters from
	 */
	private void extractData(Request _req)
	{
		List<Param> paramList = _req.getParams(ParamType.FORM);
		JSONObject json = new JSONObject();
		for(Param p:paramList)
		{
			String name = p.getParamConfig().getName();
			String val = p.getValue().toArray()[0].toString();
			try 
			{
				json.put(name, val);
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		mData = json.toString().getBytes();
	}

	@Override
	public int getContentLength(Request arg0) 
	{
		extractData(arg0);
		return mData.length;
	}

	@Override
	public String getContentType(Request arg0) 
	{
		return "application/json";
	}

	@Override
	public void writeTo(Request arg0, OutputStream arg1) throws Exception 
	{
		extractData(arg0);
		arg1.write(mData);
	}
}
