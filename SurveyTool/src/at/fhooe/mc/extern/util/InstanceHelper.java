package at.fhooe.mc.extern.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instances;
import android.content.res.AssetManager;
import android.util.Log;

public class InstanceHelper 
{
	private static final String TAG = "InstanceCreator";
	private static final String ARFF_ENDING = ".arff";
	
	public static Instances getInstancesFromAssets(AssetManager _am, String _path)
    {
    	InputStream isTrain = null;
    	try {
    		isTrain = _am.open(_path);
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return InstanceHelper.getInstancesFromInputStream(isTrain);
    }
	
	public static Instances getInstancesFromFile(File _f)
    {
    	if(null==_f)
    	{
    		Log.e(TAG, "file does not exist");
    		return null;
    	}
    	InputStream is = null;
    	try 
    	{
			is = new FileInputStream(_f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return getInstancesFromInputStream(is);
    }
    
    public static Instances getInstancesFromInputStream(InputStream _is)
    {
    	if(null==_is)
    	{
    		Log.e(TAG, "passed inputstream was null");
    		return null;
    	}
    	Instances training = null;
        try 
        {
			BufferedReader brTrain = new BufferedReader(new InputStreamReader(_is));
			training = new Instances(brTrain);			
			training.setClassIndex(0);	
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
        return training;
    }
    
    
    
    /**
	 * creates a list of double arrays. each double array holds the values of one attribute
	 * of the passed instances
	 * @param _inst the instances to transform
	 * @return an ArrayList containing double arrays for each attribute
	 */
	public static ArrayList<double[]> generateAttributeValueList(Instances _inst)
	{
		ArrayList<double[]> res = new ArrayList<double[]>();
		for(int i = 0;i<_inst.numAttributes();i++)
		{
			Attribute a = _inst.attribute(i);
			if(a.equals(_inst.classAttribute()))
			{
				continue;
			}
			res.add(_inst.attributeToDoubleArray(i));
		}
		return res;
	}
	
	/**
	 * computes the first and last index of a certain attribute in the passed instances
	 * @param _inst the instances to get the indices from
	 * @param _attribute the attribute to look for
	 * @return a int array of size two. index 0 holds the first index, index 1 holds the last index
	 */
	public static int[] getIndicesOfClassAttribute(Instances _inst, String _attribute)
	{
		int[] res = new int[]{-1,-1};
		String currentAtt = "";
		String nextAtt = "";
		for(int i = 0;i<_inst.numInstances();i++)
		{
			currentAtt = _inst.instance(i).stringValue(_inst.classAttribute());
			if(res[0]==-1)
			{
				if(currentAtt.equals(_attribute))
				{
					res[0] = i;
				}
			}
			try
			{
				nextAtt = _inst.instance(i+1).stringValue(_inst.classAttribute());
			}
			catch(IndexOutOfBoundsException _ioobe)
			{
				res[1] = i;
				return res;
			}
			if(currentAtt.equals(_attribute))
			{
				if(!nextAtt.equals(_attribute))
				{
					res[1] = i;
					return res;
				}
			}
		}
		return res;
	}
	
	/**
	 * extracts all instances from the passed instances which's class attribute is equal to the
	 * passed attribute
	 * @param _i the instances to search in
	 * @param _attribute the attribute to search for
	 * @return all instances which's class attribute equals the passed attribute
	 */
	public static Instances extractInstances(Instances _i, String _attribute)
	{
//		Log.e(TAG, "attribute to extract:"+_attribute);
		int[] indices = getIndicesOfClassAttribute(_i, _attribute);
//		Log.e(TAG, "name:"+_i.relationName()+" min idx="+indices[0]+", max idx="+indices[1]);
		return new Instances(_i, indices[0], indices[1]-indices[0]+1);
	}
	
	/**
     * reads the URIs of all .arff files in the assets and stores them in an arraylist
     */
    public static ArrayList<String> getDataSetListFromAssets(AssetManager _am)
    {
    	ArrayList<String> res = new ArrayList<String>();
//    	AssetManager am = getAssets();
    	try 
    	{
			String[] directories = _am.list("");
			for(String dir:directories)
			{
				String[] filesInDir = _am.list(dir);
				for(String file:filesInDir)
				{
					if(file.endsWith(ARFF_ENDING))
					{
						res.add(dir+"/"+file);
					}
				}
			}	
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}
    	return res;
    }
}
