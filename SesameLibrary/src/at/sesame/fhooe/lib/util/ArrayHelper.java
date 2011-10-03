package at.sesame.fhooe.lib.util;

import java.util.ArrayList;

import android.util.Log;

public class ArrayHelper 
{
	/**
	 * computes the sum of all values of a passed double array
	 * @param _vals the double array to compute the sum from
	 * @return the sum of the elements of the passed array
	 */
	public static double sum(double[] _vals)
	{
		double sum = 0 ;
		for(int i =0;i<_vals.length;i++)
		{
			sum+=_vals[i];
		}
		return sum;
	}
	
	/**
	 * multiplies two passed double arrays element by element
	 * @param _a the first array
	 * @param _b the second array
	 * @return an array containing the element wise multiplication
	 * of the passed arrays
	 */
	public static double[] multiply(double[]_a, double[] _b)
	{
		double[] res = new double[_a.length];
		for(int i = 0;i<res.length;i++)
		{
			res[i] = _a[i]*_b[i];
		}
		return res;
	}
	
	/**
	 * multiplies each element of the passed double array
	 * with itself
	 * @param _a the array to square
	 * @return an array containing squared values of the passed array
	 */
	public static double[] square(double[] _a)
	{
		return multiply(_a, _a);
	}

	/**
	 * computes the mean of a passed double array
	 * @param _arr the array to compute the mean from
	 * @return the mean of the passed double array
	 */
	public static double mean(double[] _arr)
	{
		double sum = 0;
		
		for(double d:_arr)
		{
			sum+=d;
		}
		return sum/_arr.length;
	}
	
	/**
	 * computes the minimal value of a passed array
	 * @param _arr the array to get the minimum from
	 * @return a double[] of size 2. the first index holds the minimum value of the passed array,
	 * the second index holds the index where the minimum value is in the passed array
	 */
	public static double[] min(double[] _arr)
	{
		double min = Double.MAX_VALUE;
		double minIdx = -1;
		
		for(int i = 0;i<_arr.length;i++)
		{
			if(_arr[i]<min)
			{
				min = _arr[i];
				minIdx = i;
			}
		}
		return new double[]{min, minIdx};
	}
	
	/**
	 * computes the maximal value of a passed array
	 * @param _arr the array to get the maximum from
	 * @return a double[] of size 2. the first index holds the maximum value of the passed array,
	 * the second index holds the index where the maximum value is in the passed array
	 */
	public static double[] max(double[] _arr)
	{
		double max = Double.MIN_VALUE;
		double maxIdx = -1;
		
		for(int i = 0;i<_arr.length;i++)
		{
			if(_arr[i]>max)
			{
				max = _arr[i];
				maxIdx = i;
			}
		}
		return new double[]{max, maxIdx};
	}
	
	/**
	 * computes the log10 of all values of the passed array
	 * @param _arr the array to compute log10 from
	 * @return an array containing the log10 of the passed array
	 */
	public static double[] log10(double[] _arr)
	{
		double[]res = new double[_arr.length];
		for(int i = 0;i<res.length;i++)
		{
			if(0.0==_arr[i])
			{
				res[i]=0;
				continue;
			}
//			Log.e("ArrayHelper", "before log:"+_arr[i]);
			res[i]=Math.log10(Math.abs(_arr[i]));
//			Log.e("ArrayHelper", "after log:"+res[i]);
		}
		return res;
	}
	
	/**
	 * prints the passed array
	 * @param _tag the tag for the print
	 * @param arr the array to print
	 */
	public static void printArray(String _tag, double[] arr)
	{
		StringBuffer sb = new StringBuffer();
		
		for(int i = 0;i<arr.length;i++)
		{
			sb.append(arr[i]);
			sb.append("\n");
		}
		Log.e(_tag, sb.toString());
		Log.e(_tag, "--------------------");
	}
	
	/**
     * checks every double[] in the passed list of arrays for occurrences of the passed value,
     * removes them and returns the newly created list of double arrays
     * @param _arr the list of arrays to remove values from
     * @param _val the values to remove from the arrays in the list
     * @return a list containing the arrays without the passed value
     */
    public static ArrayList<double[]> removeValues(ArrayList<double[]> _arr, double _val)
    {
    	ArrayList<double[]> res = new ArrayList<double[]>();
    	
    	for(double[]arr:_arr)
    	{
    		res.add(removeValues(arr, _val));
    	}
    	return res;
    }
    
    /**
     * checks the passed double array for occurrences of the passed value and removes them
     * @param _arr the array to remove values from
     * @param _value the value to remove
     * @return an array without the passed value
     */
    private static double[]removeValues(double[] _arr, double _value)
    {
    	double[]res = new double[_arr.length-countOccurrence(_arr, _value)];
    	int resIdx = 0;
    	for(double d:_arr)
    	{
    		if(d!=_value)
    		{
    			res[resIdx]=d;
    			resIdx++;
    		}
    	}
    	return res;
    }
    
    /**
     * counts the number of occurrences of the passed value in the passed array
     * @param _arr the array to count the occurrences
     * @param _value the value to count
     * @return the number of occurrences of the passed value in the passed array
     */
    private static int countOccurrence(double[] _arr, double _value)
    {
    	int res = 0;
    	for(double d:_arr)
    	{
    		if(d==_value)
    		{
    			res++;
    		}
    	}
    	return res;
    }
    
    /**
     * transforms an ArrayList of Double values to an Array of double values
     * @param _list the ArrayList with Double values
     * @return an array with double values
     */
    public static double[] getDoubleArrayFromArrayList(ArrayList<Double> _list)
	{
		double[] res = new double[_list.size()];
		for(int i = 0;i<res.length;i++)
		{
			res[i] = _list.get(i).doubleValue();
		}
		return res;
	}
}
