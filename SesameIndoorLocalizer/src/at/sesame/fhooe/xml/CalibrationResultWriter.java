package at.sesame.fhooe.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class CalibrationResultWriter 
{
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
	
	public static boolean exportLinearRegressionResults(String _path, double _a, double _b, String _trainingSetName)
	{
		StringBuffer sb = new StringBuffer(XML_HEADER);
		sb.append(getXmlTag("LinearRegression", true, 0));
		sb.append("\n");
		sb.append(getXmlEntry("TrainingSet", _trainingSetName, 1));
		sb.append(getXmlEntry("a", ""+_a, 1));
		sb.append(getXmlEntry("b", ""+_b, 1));
		sb.append(getXmlTag("LinearRegression", false, 0));
		return writeXmlFile(new File(_path), sb.toString());
	}
	
	/**
	 * creates a xml tag with specified tag name, value and number of tabs to 
	 * append before the tag (for human readability of the xml file)
	 * @param _tag the name of the tag
	 * @param _value the value inside the tag
	 * @param _level the number of tabs to enter
	 * @return a string containing the xml tag specified by the passed parameters
	 */
	private static String getXmlEntry(String _tag, String _value, int _level)
	{
		StringBuffer sb = new StringBuffer();
//		for(int i = 0;i<_level;i++)
//		{
//			sb.append("\t");
//		}
			
		sb.append(getXmlTag(_tag, true, _level));
		sb.append(_value);
		sb.append(getXmlTag(_tag, false, 0));
		sb.append("\n");
		return sb.toString();
		
	}
	
	/**
	 * creates a new XML Tag
	 * @param _tag the name of the tag
	 * @param _open boolean indicating whether it is 
	 * an opening tag or a closing tag.
	 * @return a string containing the tag
	 */
	private static String getXmlTag(String _tag, boolean _open, int _level)
	{
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i<_level;i++)
		{
			sb.append("\t");
		}
		sb.append("<");
		if(!_open)
		{
			sb.append("/");
		}
		sb.append(_tag);
		sb.append(">");
		
		return sb.toString();
	}
	
	/**
	 * writes the passed content to the passed file
	 * @param _f the file specifying the location for the xml file
	 * @param _content the content of the xml file
	 */
	private static boolean writeXmlFile(File _f, String _content)
	{
		if(!_f.getAbsolutePath().endsWith(".xml"))
		{
			_f = new File(_f.getAbsolutePath()+".xml");
		}
		if(!_f.exists())
		{
			_f.getParentFile().mkdirs();
			try {
				_f.createNewFile();
			} 
			catch (IOException e) 
			{

				return false;
			}
			if(_f.exists())
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
			PrintWriter pw = new PrintWriter(_f);
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
