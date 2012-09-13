package at.sesame.fhooe.lib2.data.semantic.parsing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import at.sesame.fhooe.lib2.data.EnhancedSesameNotification;
import at.sesame.fhooe.lib2.data.SesameDataCache;
import at.sesame.fhooe.lib2.data.SesameMeasurementPlace;
import at.sesame.fhooe.lib2.data.SesameNotification;
import at.sesame.fhooe.lib2.data.SesameSensor;
import at.sesame.fhooe.lib2.data.SesameNotification.NotificationType;
import at.sesame.fhooe.lib2.data.SesameSensor.SensorType;

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
//			Log.e(TAG, "??????Length of JSON result ARRAY:"+jsonResultArray.length());
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
				Log.e(TAG, "PARSING RESULT:"+i);
				JSONObject jsonObject = jsonResultArray.getJSONObject(i);
				JSONObject sensorObject = jsonObject.getJSONObject("y");
				String sensorId = sensorObject.getString("value");
				
				//				Log.i(TAG, "Meter="+meterString);
				//				removeSesamePrefix(meterString);
				//				res.add(removeSesamePrefix(resultString));
				JSONObject measurementPlaceObject = jsonObject.getJSONObject("x");
				String measurmentPlaceId = measurementPlaceObject.getString("value");
				String reducedPlace = removeSesamePrefix(measurmentPlaceId);
				String reducedID = removeSesamePrefix(sensorId);
//				Log.e(TAG, "putting:"+reducedPlace+"@"+reducedID);
				if(!reducedID.contains("Volt"))
				{
					res.put( reducedPlace, reducedID);					
				}
				//				Log.i(TAG, "MeasurementPlace="+measurmentPlaceString);
				//				removeSesamePrefix(measurmentPlaceString);
			}
		}
		catch(Exception _e)
		{
			_e.printStackTrace();
		}
		return res;
	}
	
	public static ArrayList<EnhancedSesameNotification> parseEnhancedNotifications(String _result)
	{
		ArrayList<EnhancedSesameNotification> res = new ArrayList<EnhancedSesameNotification>();
		JSONArray arr = extractValues(_result);
	
		Log.e(TAG, arr.toString());
		try
		{
			for (int i = 0; i < arr.length(); i++) 
			{
				JSONObject jsonObject = arr.getJSONObject(i);
				
				JSONObject sensorObject = jsonObject.getJSONObject("sensor");
				String sensorId = removeSesamePrefix(sensorObject.getString("value"));
				
				JSONObject roomObject = jsonObject.getJSONObject("room");
				String roomId = removeSesamePrefix(roomObject.getString("value"));
				
//				JSONObject alertObject = jsonObject.getJSONObject("alert");
//				String alertId = alertObject.getString("value");
				
				JSONObject messageObject = jsonObject.getJSONObject("message");
				String message = messageObject.getString("value");
				
				JSONObject timeObject = jsonObject.getJSONObject("time");
				String timeString = timeObject.getString("value");
				Date d;
				
				d = SemanticRepoHelper.OPEN_RDF_DATE_FORMAT.parse(timeString);
				
				res.add(new EnhancedSesameNotification(new SesameMeasurementPlace(roomId), d, new SesameSensor(sensorId, SensorType.light), message));
			}
			
		}
		catch (Exception e) 
		{
			// TODO: handle exception
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
		catch(Exception _e)
		{
			_e.printStackTrace();
		}
		return res;
	}

	public static ArrayList<SesameNotification> parseNotifications(String _result)
	{
		ArrayList<SesameNotification> res = new ArrayList<SesameNotification>();
		JSONArray jsonResultArray = extractValues(_result);
		try
		{
			for (int i = 0; i < jsonResultArray.length(); i++) 
			{
				JSONObject resultObject = jsonResultArray.getJSONObject(i);
				JSONObject subject = resultObject.getJSONObject("s");
				JSONObject object = resultObject.getJSONObject("o");
//				Log.i(TAG, "subject = "+subject);
//				Log.i(TAG, "object = "+object);
				String typeString = removeSesamePrefix(subject.getString("value"));
				String macString = removeSesamePrefix(object.getString("value"));
				
//				Log.i(TAG, "typeString = "+typeString);
				macString = macString.substring(macString.indexOf(":")+1, macString.length());
				NotificationType type = null;
				if(typeString.contains("40"))
				{
					type = NotificationType.Type40;
				}
				else if(typeString.contains("60"))
				{
					type = NotificationType.Type60;
				}
				else if(typeString.contains("80"))
				{
					type = NotificationType.Type80;
				}
				
				res.add(new SesameNotification(type, macString));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
//		Log.i(TAG, "result:"+result);
		return result;
	}
}
