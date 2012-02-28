package at.fhooe.mc.extern.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import android.os.Environment;
import android.util.Log;
import at.fhooe.mc.extern.fingerprintInformation.FingerPrint;
import at.fhooe.mc.extern.fingerprintInformation.FingerPrintItem;

public class ARFFGenerator 
{
	private static final String TAG = "ARFFGenerator";
	private static final String RELATION_TAG = "@relation ";
	private static final String ATTRIBUTE_TAG = "@attribute ";
	private static final String DATA_TAG = "@data";
	private static final String ARFF_ENDING = ".arff";
	private static final String MP_ATTRIBUTE_NAME = "MP";
	private static final String ROOM_ATTRIBUTE_NAME = "Room";
	private static final String NUMERIC_TAG = "numeric";

	private static int mNoBssidRssi;
	private static ArrayList<String> mUniqueBssids;
	private static int mArffLineCount = 0;

	public enum ARFFType
	{
		ROOM,
		MP
	}

	public static void writeInstancesToArff(Instances _inst, String _path)
	{
		//write relation
		StringBuffer sb = new StringBuffer();
		sb.append(RELATION_TAG);
		sb.append(_inst.relationName());
		sb.append("\n\n");

		//write attributes
		for(int i = 0;i<_inst.numAttributes();i++)
		{
			Attribute att = _inst.attribute(i);
			sb.append(ATTRIBUTE_TAG);
			sb.append(att.name());
			sb.append(" ");
			if(att.isNominal()||att.isString())
			{
				sb.append("{");

				@SuppressWarnings("unchecked")
				Enumeration<String> e = att.enumerateValues();
				StringBuffer valueBuffer = new StringBuffer();
				while(e.hasMoreElements())
				{
					valueBuffer.append("'");
					valueBuffer.append(e.nextElement());
					valueBuffer.append("'");
					valueBuffer.append(",");
				}
				String values = valueBuffer.toString();
				sb.append(values.substring(0, values.length()-1));
				//					Log.e("arff", msg)
				//					sb.append(att.e);


				//				sb.append(att.value(att.numValues()-1));
				sb.append("}\n");
			}
			else
			{
				sb.append("numeric\n");
			}
		}

		//write data
		sb.append("\n\n");
		sb.append(DATA_TAG);
		sb.append("\n");

		for(int i = 0;i<_inst.numInstances();i++)
		{
			Instance inst = _inst.instance(i);
			for(int j = 0;j<inst.numAttributes()-1;j++)
			{
				if(inst.attribute(j).isNominal()||inst.attribute(j).isString())
				{
					sb.append("'");
					sb.append(inst.stringValue(inst.attribute(j)));
					sb.append("'");
				}
				else
				{
					sb.append(inst.value(j));
				}
				sb.append(",");
			}
			if(inst.attribute(inst.numAttributes()-1).isNominal()||inst.attribute(inst.numAttributes()-1).isString())
			{
				sb.append(inst.stringValue(inst.attribute(inst.numAttributes()-1)));
			}
			else
			{
				sb.append(inst.value(inst.numAttributes()-1));
			}
			sb.append("\n");
		}

		Log.e("arff", sb.toString());
		writeArffFile(_path, sb.toString());
	}

	/**
	 * creates an ARFF file from the passed list of FingerPrintItems
	 * @param _items the items to be stored in the ARFF file
	 * @param _fileName the path where the ARFF file has to be stored
	 * @param _relationName the name of the relation
	 * @param _type specifies if the class attribute is "Room" or "MeasurementPoint"
	 * @param _noBssidRssi the RSSI of a not received BSSID 
	 * @return true if the export was successful, false otherwise
	 */
	public synchronized static boolean writeSurveyResultsToArff(ArrayList<? extends FingerPrintItem> _items, String _fileName, String _relationName, ARFFType _type, int _noBssidRssi)
	{
		mNoBssidRssi = _noBssidRssi;
		StringBuffer exportBuffer = new StringBuffer(RELATION_TAG);
		exportBuffer.append(_relationName);
		exportBuffer.append("\n\n");

		switch(_type)
		{
		case ROOM:
			ArrayList<String> uniqueRooms = getUniqueRooms(_items);
			exportBuffer.append(ATTRIBUTE_TAG);
			exportBuffer.append(" ");
			exportBuffer.append(ROOM_ATTRIBUTE_NAME);
			exportBuffer.append(" ");
			exportBuffer.append(getAttributeValueList(uniqueRooms));
			exportBuffer.append("\n");
			break;
		case MP:
			ArrayList<String> uniqueMPNames = getUniqueMPNames(_items);
			exportBuffer.append(ATTRIBUTE_TAG);
			exportBuffer.append(" ");
			exportBuffer.append(MP_ATTRIBUTE_NAME);
			exportBuffer.append(" ");
			exportBuffer.append(getAttributeValueList(uniqueMPNames));
			exportBuffer.append("\n");
			break;
		}
		Log.e(TAG, exportBuffer.toString());
		mUniqueBssids= getUniqueBSSIDs(_items);

		for(String s:mUniqueBssids)
		{
			exportBuffer.append(ATTRIBUTE_TAG);
			exportBuffer.append(" ");
			exportBuffer.append(s);
			exportBuffer.append(" ");
			exportBuffer.append(NUMERIC_TAG);
			exportBuffer.append("\n");
		}
		writeDataToBuffer(exportBuffer,_items, _type);
		Log.e(TAG, exportBuffer.toString());
		Log.e(TAG,"#####LINECOUNT="+mArffLineCount);
		StringBuffer pathBuffer = new StringBuffer(Environment.getExternalStorageDirectory().getAbsolutePath());
		pathBuffer.append("/");
		pathBuffer.append(_fileName);
		pathBuffer.append(getTimestampString());
		pathBuffer.append(ARFF_ENDING);
		boolean result = writeArffFile(pathBuffer.toString(), exportBuffer.toString());
		Log.e(TAG, "writing result:"+result);
		return true;
	}


	private static String getTimestampString() 
	{
		GregorianCalendar gc = new GregorianCalendar();
		int day = gc.get(Calendar.DAY_OF_MONTH);
		int month = gc.get(Calendar.MONTH);
		int hour = gc.get(Calendar.HOUR_OF_DAY);
		int minute = gc.get(Calendar.MINUTE);
		int second = gc.get(Calendar.SECOND);
		
		StringBuffer dateBuffer = new StringBuffer();
		dateBuffer.append("_");
		dateBuffer.append(day);
		dateBuffer.append("_");
		dateBuffer.append(month);
		dateBuffer.append("_");
		dateBuffer.append(hour);
		dateBuffer.append("_");
		dateBuffer.append(minute);
		dateBuffer.append("_");
		dateBuffer.append(second);
		return dateBuffer.toString();
	}

	/**
	 * extracts a list of unique BSSIDs from the passed list of FingerPrintItems and sorts it
	 * @param _items the FingerPrintItems to extract the information from
	 * @return a sorted list of unique BSSIDs
	 */
	private static ArrayList<String> getUniqueBSSIDs(ArrayList<? extends FingerPrintItem> _items) 
	{
		ArrayList<String> uniqueBSSIDs = new ArrayList<String>();
		for(FingerPrintItem fpi:_items)
		{
			ArrayList<FingerPrint> fingerPrints = fpi.getFingerPrints();
			for(FingerPrint fp:fingerPrints)
			{
				String bssid = fp.getBSSID();
				if(!uniqueBSSIDs.contains(bssid))
				{
					uniqueBSSIDs.add(bssid);
				}
			}
		}

		sort(uniqueBSSIDs);
		return uniqueBSSIDs;
	}

	/**
	 * extracts a list of unique rooms from the passed list of FingerPrintItems and sorts it
	 * @param _items the FingerPrintItems to extract the information from
	 * @return a sorted list of unique rooms
	 */
	private static ArrayList<String> getUniqueRooms(ArrayList<? extends FingerPrintItem> _items)
	{
		ArrayList<String> uniqueRooms = new ArrayList<String>();
		for(FingerPrintItem fpi:_items)
		{
			String room = fpi.getRoom();
			if(!uniqueRooms.contains(room))
			{
				uniqueRooms.add(room);
			}
		}

		sort(uniqueRooms);
		return uniqueRooms;
	}

	/**
	 * extracts a list of unique MP names from the passed list of FingerPrintItems and sorts it
	 * @param _items the FingerPrintItems to extract the information from
	 * @return a sorted list of unique MP names
	 */
	private static ArrayList<String> getUniqueMPNames(ArrayList<? extends FingerPrintItem> _items)
	{
		ArrayList<String> uniqueMPs = new ArrayList<String>();
		for(FingerPrintItem fpi:_items)
		{
			String name = fpi.getName();
			if(!uniqueMPs.contains(name))
			{
				uniqueMPs.add(name);
			}
		}
		sort(uniqueMPs);
		return uniqueMPs;
	}

	/**
	 * creates a string that contains all values from the passed list 
	 * and is conform to the ARFF file format
	 * @param _vals the values to be written to the list
	 * @return a string containing all values from the passed list in ARFF conform format
	 */
	private static String getAttributeValueList(ArrayList<String> _vals)
	{
		StringBuffer sb = new StringBuffer("{");

		for(String s:_vals)
		{
			sb.append("'");
			sb.append(s);
			sb.append("'");
			sb.append(",");
		}
		String res = sb.toString();
		res = res.substring(0, res.length()-1);
		res = res + "}";

		return res;
	}

	/**
	 * sorts a passed ArrayList of Strings alphabetically ascending
	 * @param _items the ArrayList of Strings to be sorted
	 */
	private static void sort(ArrayList<String> _items)
	{
		Collections.sort(_items, new Comparator<String>() {
			@Override
			public int compare(String _s1, String _s2) {
				return _s1.compareToIgnoreCase(_s2);
			}
		});
	}


	private static void writeDataToBuffer(StringBuffer _sb, ArrayList<? extends FingerPrintItem> _items, ARFFType _type)
	{
		_sb.append(DATA_TAG);
		_sb.append("\n");

		switch(_type)
		{
		case ROOM:
			writeRoomDataToBuffer(_sb, _items);
			break;
		case MP:
			writeMPDataToBuffer(_sb, _items);
			break;
		}

	}

	private static void writeRoomDataToBuffer(StringBuffer _sb, ArrayList<? extends FingerPrintItem> _items)
	{
		mArffLineCount = 0;
		for(FingerPrintItem fpi:_items)
		{
			//			Log.e(TAG, fpi.toString());
			Log.e(TAG, "writing data for "+fpi.getName());
			String room = fpi.getRoom();
			Log.e(TAG, "processing results for room:"+room);
			_sb.append(writeRSSIToBuffer(room, fpi));
		}
	}

	private static void writeMPDataToBuffer(StringBuffer _sb, ArrayList<? extends FingerPrintItem> _items)
	{
		mArffLineCount = 0;
		for(FingerPrintItem fpi:_items)
		{
			Log.e(TAG, fpi.toString());
			String mp = fpi.getName();
			_sb.append(writeRSSIToBuffer(mp, fpi));
		}
	}

	private static String writeRSSIToBuffer(String _classAtt,FingerPrintItem _fpi)
	{
		
		//		Hashtable<String, ArrayList<Integer>> rssiVals = new Hashtable<String, ArrayList<Integer>>();
		//		
		//		for(String key:mUniqueBssids)
		//		{
		//			rssiVals.put(key, new ArrayList<Integer>());
		//		}
		//		
		//		for(FingerPrint fp:_fpi.getFingerPrints())
		//		{
		//			Log.e(TAG, fp.toString());
		//			ArrayList<Integer> list=rssiVals.get(fp.getBSSID());
		//			list.add(fp.getLevel());
		//		}

		//		printHashtable(rssiVals);

		ArrayList<Integer> scanIDs = getScanIds(_fpi.getFingerPrints());
		if(null==scanIDs)
		{
			//TODO check
			Log.e(TAG, "no fingerprints available, skipping");
			return "";
		}
		Log.e(TAG, "fingerprints found, processing");
		StringBuffer resultBuffer = new StringBuffer();
		
		for(Integer id:scanIDs)
		{
			StringBuffer lineBuffer = new StringBuffer();
			lineBuffer.append("'");
			lineBuffer.append(_classAtt);
			lineBuffer.append("'");
			lineBuffer.append(",");
			
			ArrayList<FingerPrint> sameIdFingerPrints = getFingerPrintsByScanId(_fpi.getFingerPrints(), id.intValue());
			for(String bssid:mUniqueBssids)
			{
				boolean bssidFound = false;
				for(FingerPrint fp:sameIdFingerPrints)
				{

					if(fp.getBSSID().equals(bssid))
					{
						lineBuffer.append(fp.getLevel());

						bssidFound = true;
						break;
					}
					//						else
						//						{
					//							Log.e(TAG, "result:not equal");
					//						}
				}
				if(!bssidFound)
				{
					//						Log.e(TAG, "no fingerprint has a value for BSSID:"+bssid);
					lineBuffer.append(mNoBssidRssi);
				}
				lineBuffer.append(",");
//				Log.e(TAG, "currentLine:"+lineBuffer.toString());
			}
			String line = lineBuffer.toString();
			line = line.substring(0,line.length()-1);
			line = line+"\n";
			mArffLineCount++;
			resultBuffer.append(line);
		}
		
		return resultBuffer.toString();


		//		line.append(_classAtt);
		//		line.append("'");
		//		line.append(",");
		//		Log.e(TAG, "number of fingerprints stored:"+_fpi.getFingerPrints().size());
		//		for(FingerPrint fp:_fpi.getFingerPrints())
		//		{
		//			boolean bssidFound = false;
		//			for(String bssid:mUniqueBssids)
		//			{
		//				
		//				if(fp.getBSSID().equals(bssid))
		//				{
		//					line.append(fp.getLevel());
		//					
		//					bssidFound = true;
		//					break;
		//				}
		////				else
		////				{
		////					Log.e(TAG, "result:not equal");
		////				}
		//			}
		//			if(!bssidFound)
		//			{
		////				Log.e(TAG, "no fingerprint has a value for BSSID:"+bssid);
		//				line.append(mNoBssidRssi);
		//			}
		//			line.append(",");
		//			Log.e(TAG, "currentLine:"+line.toString());
		//		}
//		String res = "";//resultBuffer.toString();
//		res = res.substring(0, res.length()-1);
//		res = res+"\n";
//		return res;
	}

	private static ArrayList<Integer> getScanIds(ArrayList<FingerPrint> _fps)
	{	
		if(_fps.size()<=0)
		{
			return null;
		}
		ArrayList<Integer> res = new ArrayList<Integer>();

		res.add(new Integer(_fps.get(0).getScanID()));
		for(int i =1;i<_fps.size();i++)
		{
			Integer curId = new Integer(_fps.get(i).getScanID());
			if(!res.contains(curId))
			{
				res.add(curId);
			}
		}

		return res;
	}

	private static ArrayList<FingerPrint> getFingerPrintsByScanId(ArrayList<FingerPrint> _fps, int _id)
	{
		if(_fps.size()<=0)
		{
			return null;
		}
		ArrayList<FingerPrint> res = new ArrayList<FingerPrint>();

		for(FingerPrint fp:_fps)
		{
			if(fp.getScanID()==_id)
			{
				res.add(fp);
			}
		}

		return res;
	}


//	private static void printHashtable(Hashtable<String, ArrayList<Integer>> _table) 
//	{
//		Enumeration<String> keys = _table.keys();
//		while(keys.hasMoreElements())
//		{
//			String key = keys.nextElement();
//			ArrayList<Integer> vals = _table.get(key);
//			int[] intVals = new int[vals.size()];
//			for(int i = 0;i<intVals.length;i++)
//			{
//				intVals[i]=vals.get(i).intValue();
//			}
//			String valStr = Arrays.toString(intVals);
//			Log.e(TAG,key+":"+valStr);
//		}
//
//	}

	/**
	 * writes the passed content to the passed file
	 * @param _f the file specifying the location for the arff file
	 * @param _content the content of the arff file
	 */
	public static boolean writeArffFile(String _path, String _content)
	{
		File f = new File(_path);
		if(!f.exists())
		{
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} 
			catch (IOException e) 
			{

				return false;
			}
			if(f.exists())
			{
				System.out.println("file created successfully");
			}
			else
			{
				System.out.println("file creation failed");
			}
		}
		try 
		{
			PrintWriter pw = new PrintWriter(f);
			pw.write(_content);
			pw.flush();
			pw.close();
		} 
		catch (FileNotFoundException e) 
		{
			return false;
		}
		return true;
	}
}
