/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms.entitywriter;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.codegist.crest.config.ParamType;
import org.codegist.crest.entity.EntityWriter;
import org.codegist.crest.io.Request;
import org.codegist.crest.param.Param;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * this class implements the interface EntityWriter used by the CRest
 * library to write data to the body of a http-request.
 *
 */
public class PMSEntityWriter
implements EntityWriter
{
	private static final String TAG = "PMSEntityWriter";
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
			if(p.getParamConfig().getName().equals("maclist"))
			{
//				Log.e(TAG,p.getParamConfig().getValueClass().toString());
				StringBuilder sb = new StringBuilder();
				sb.append("[");
//				p.g
//				ArrayList<String> hosts = (ArrayList<String>) p.getValue().toArray()[0];
				List<Object> hostObjects = (List<Object>) p.getValue();
				ArrayList<String> hosts = new ArrayList<String>();
				for(Object o:hostObjects)
				{
					hosts.add((String)o);
				}
//				Log.e(TAG, "number of entries="+p.getValue().size());
				
				for(int i =0 ;i<hosts.size()-1;i++)
				{
					JSONObject temp = new JSONObject();
					try {
						temp.put("mac", hosts.get(i));
						sb.append(temp.toString());
						sb.append(",");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					arr.put(json);
				}
				JSONObject lastObject = new JSONObject();
				try {
					lastObject.put("mac", hosts.get(hosts.size()-1));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sb.append(lastObject.toString());
				sb.append("]");
				mData = sb.toString().getBytes();
			}
			else
			{
//				Log.e(TAG, p.toString());
				String name = p.getParamConfig().getName();
				String val = p.getValue().toArray()[0].toString();
//				Log.e(TAG, "name = "+name+", val = "+val);
				JSONArray arr = new JSONArray();

				try 
				{
					json.put(name, val);
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
				mData = json.toString().getBytes();
			}

		}
		//TODO remove!!
		//		json.remove(paramList.get(0).getParamConfig().getName());
		
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
