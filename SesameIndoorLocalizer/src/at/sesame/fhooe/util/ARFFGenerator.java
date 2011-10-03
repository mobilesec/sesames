package at.sesame.fhooe.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import android.util.Log;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class ARFFGenerator 
{
	public static void writeInstancesToArff(Instances _inst, String _path)
	{
		//write relation
		StringBuffer sb = new StringBuffer();
		sb.append("@relation ");
		sb.append(_inst.relationName());
		sb.append("\n\n");
		
		//write attributes
		for(int i = 0;i<_inst.numAttributes();i++)
		{
			Attribute att = _inst.attribute(i);
			sb.append("@attribute ");
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
		sb.append("@data\n");
		
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
	 * writes the passed content to the passed file
	 * @param _f the file specifying the location for the arff file
	 * @param _content the content of the arff file
	 */
	private static boolean writeArffFile(String _path, String _content)
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
