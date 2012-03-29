package at.sesame.fhooe.lib2.data.semantic.parsing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SemanticQueryResultParser 
{
	private static final String TAG = "SemanticQueryResultParser";
	
	private static JSONArray extractValues(String _result)
	{
		JSONObject jsonResult;
		try 
		{
			jsonResult = new JSONObject(_result);
			JSONObject resultObject = jsonResult.getJSONObject("results");
			JSONArray jsonResultArray = resultObject.getJSONArray("bindings");
			return jsonResultArray;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static HashMap<String, String> parseSensorsQueryResult(String _result)
	{
		HashMap<String, String> res = new HashMap<String, String>();
		JSONArray jsonResultArray = extractValues(_result);
		try
		{
			for (int i = 0; i < jsonResultArray.length(); i++) 
			{
				JSONObject jsonObject = jsonResultArray.getJSONObject(i);
				JSONObject sensorObject = jsonObject.getJSONObject("y");
				String sensorId = sensorObject.getString("value");
//				Log.i(TAG, "Meter="+meterString);
//				removeSesamePrefix(meterString);
//				res.add(removeSesamePrefix(resultString));
				JSONObject measurementPlaceObject = jsonObject.getJSONObject("x");
				String measurmentPlaceId = measurementPlaceObject.getString("value");
				res.put(removeSesamePrefix(measurmentPlaceId), removeSesamePrefix(sensorId));
//				Log.i(TAG, "MeasurementPlace="+measurmentPlaceString);
//				removeSesamePrefix(measurmentPlaceString);
			}
		}
		catch(JSONException _je)
		{
			_je.printStackTrace();
		}
		return res;
	}
	
//	public static ArrayList<String> parseSingleVarStringResult(String _result, String _varName)
//	{
//		ArrayList<String> res = new ArrayList<String>();
//		JSONArray jsonResultArray = extractValues(_result);
//		try
//		{
//			for (int i = 0; i < jsonResultArray.length(); i++) 
//			{
//				JSONObject jsonObject = jsonResultArray.getJSONObject(i);
//				JSONObject data = jsonObject.getJSONObject(_varName);
//				String resultString = data.getString("value");
////				Log.i(TAG, "Meter="+meterString);
////				removeSesamePrefix(meterString);
//				res.add(removeSesamePrefix(resultString));
////				JSONObject measurementPlace = jsonObject.getJSONObject("x");
////				String measurmentPlaceString = measurementPlace.getString("value");
////				Log.i(TAG, "MeasurementPlace="+measurmentPlaceString);
////				removeSesamePrefix(measurmentPlaceString);
//			}
//		}
//		catch(JSONException _je)
//		{
//			_je.printStackTrace();
//		}
//		return res;
//	}
	
//	public static ArrayList<String> parseLocations(String _result)
//	{
//		ArrayList<String> res = new ArrayList<String>();
//		JSONArray jsonResultArray = extractValues(_result);
//		try
//		{
//			for (int i = 0; i < jsonResultArray.length(); i++) 
//			{
//				JSONObject jsonObject = jsonResultArray.getJSONObject(i);
//				JSONObject location = jsonObject.getJSONObject("y");
//				String locationString = location.getString("value");
////				Log.i(TAG, "Meter="+meterString);
////				removeSesamePrefix(meterString);
//				res.add(removeSesamePrefix(locationString));
////				JSONObject measurementPlace = jsonObject.getJSONObject("x");
////				String measurmentPlaceString = measurementPlace.getString("value");
////				Log.i(TAG, "MeasurementPlace="+measurmentPlaceString);
////				removeSesamePrefix(measurmentPlaceString);
//			}
//		}
//		catch(JSONException _je)
//		{
//			_je.printStackTrace();
//		}
//		return res;
//	}
	
	public static  HashMap<Date, Double> parseValues(String _result)
	{
		JSONArray jsonResultArray = extractValues(_result);
		HashMap<Date, Double> res = new HashMap<Date, Double>();
		try
		{
			for (int i = 0; i < jsonResultArray.length(); i++) 
			{
				JSONObject resultObject = jsonResultArray.getJSONObject(i);
				JSONObject valueObject = resultObject.getJSONObject("z");
				double val = valueObject.getDouble("value");
//				Log.i(TAG, "VALUE="+val);

				JSONObject timestamp = resultObject.getJSONObject("y");
				String timeStampString = timestamp.getString("value");
				Date d;
				try 
				{
					d = SemanticRepoHelper.OPEN_RDF_DATE_FORMAT.parse(timeStampString);
//					Log.i(TAG, "TIMESTAMP="+d.toLocaleString());
					res.put(d, val);
				} 
				catch (ParseException e) 
				{
					e.printStackTrace();
				}
			}
		}
		catch(JSONException _je)
		{
			_je.printStackTrace();
		}
		return res;
	}
	
	private static String removeSesamePrefix(String _s)
	{
		
		String prefix = SemanticRepoHelper.PREFIXES.get(SemanticRepoHelper.DEFAULT_PREFIX_KEY);
		if(_s.length()<=prefix.length())
		{
			return "";
		}

		String result =  _s.substring(prefix.length());
		Log.i(TAG, "result:"+result);
		return result;
	}
}
