package at.sesame.fhooe.calibration;

import java.util.ArrayList;
import java.util.Enumeration;

import android.util.Log;
import at.sesame.fhooe.util.ArrayHelper;
import at.sesame.fhooe.util.InstanceHelper;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * this class offers methods to compute different kinds of normalization parameters to overcome
 * device heterogenity
 * @author Peter Riedl
 *
 */
public class Calibrator 
{
	private static final String TAG = "Calibrator";
	private static final double UNRECEIVED_BSSID_RSSI = -100;
	/**
	 * performs simple offset calibration. 
	 * @param _reference the reference instances
	 * @param _calibration the calibration instances
	 * @return the offset that has to be added to every instance in the calibration instances to match
	 * instances of the reference instances
	 */
	public double performOffsetCalibration(Instances _reference, Instances _calibration)
	{	
		Instance firstInst = _calibration.firstInstance();
		String attribute2check = firstInst.stringValue(firstInst.classAttribute());
		Instances extractedRef = InstanceHelper.extractInstances(_reference, attribute2check);
		ArrayList<double[]> extractedRefList = InstanceHelper.generateAttributeValueList(extractedRef);
		
		Instances extractedCal = InstanceHelper.extractInstances(_calibration, attribute2check);
		ArrayList<double[]> extractedCalList = InstanceHelper.generateAttributeValueList(extractedCal);
		
		double[] offsets = new double[_reference.numAttributes()];
		
		for(int i = 0;i<extractedRefList.size();i++)
		{
			double refMean = ArrayHelper.mean(extractedRefList.get(i));
			double calMean = ArrayHelper.mean(extractedCalList.get(i));
			offsets[i]=refMean-calMean;
		}
		return ArrayHelper.mean(offsets);
	}
	
	/**
	 * computes intersect and slope needed to transform instances from the calibration data
	 * to the reference data
	 * @param _reference reference data
	 * @param _calibration calibration data
	 * @return a double array of size two. index 0 holds the intersect, 
	 * index 1 holds the slope
	 */
	public double[] performLinearCalibration(Instances _reference, Instances _calibration)
	{
		ArrayList<double[]> xy = getDoubleArraysFromInstances(_reference, _calibration);
		return performLinearRegression(xy.get(0),xy.get(1));
	}
	
	/**
	 * computes r and A for the formula y = A*r^x
	 * @param _reference reference data
	 * @param _calibration calibration data
	 * @return a double array of size two. index 0 holds A, index 1 holds r;
	 */
	public double[] performLogarithmicCalibration(Instances _reference, Instances _calibration)
	{
		ArrayList<double[]> xy = getDoubleArraysFromInstances(_reference, _calibration);
		return performLogarithmicRegression(xy.get(0), xy.get(1));
	}
	
	private ArrayList<double[]> getDoubleArraysFromInstances(Instances _reference, Instances _calibration)
	{
//		Instance firstInst = _calibration.firstInstance();
//		String attribute2check = firstInst.stringValue(firstInst.classAttribute());
		String attribute2check = getFirstCommonAttribute(_reference, _calibration);
		
		ArrayList<double[]> extractedRefList = getArrayListFromInstances(_reference, attribute2check);
//		Log.e(TAG, "length of references before removal:"+extractedRefList.size());
//		printArrayListContentLength(extractedRefList);
		
		extractedRefList = ArrayHelper.removeValues(extractedRefList, UNRECEIVED_BSSID_RSSI);
//		Log.e(TAG, "length of references after removal:"+extractedRefList.size());
//		printArrayListContentLength(extractedRefList);
		
		ArrayList<double[]> extractedCalList = getArrayListFromInstances(_calibration, attribute2check);
//		Log.e(TAG, "length of calibration before removal:"+extractedCalList.size());
//		printArrayListContentLength(extractedCalList);
		extractedCalList = ArrayHelper.removeValues(extractedCalList, UNRECEIVED_BSSID_RSSI);
//		Log.e(TAG, "length of calibration after removal:"+extractedCalList.size());
//		printArrayListContentLength(extractedCalList);
		
//		double[] x = new double[_reference.numAttributes()];
//		double[] y = new double[_calibration.numAttributes()];
		ArrayList<Double>x = new ArrayList<Double>();
		ArrayList<Double>y = new ArrayList<Double>();
		
		for(int i = 0;i<extractedRefList.size();i++)
		{
			double[]refArr = extractedRefList.get(i);
			double[]calArr = extractedCalList.get(i);
			if(refArr.length>0&&calArr.length>0)
			{
				x.add(ArrayHelper.mean(refArr));
				y.add(ArrayHelper.mean(calArr));
			}
//			x[i] = ArrayHelper.mean(extractedRefList.get(i));
//			y[i] = ArrayHelper.mean(extractedCalList.get(i));
		}
		
		ArrayList<double[]>res = new ArrayList<double[]>();
		res.add(ArrayHelper.getDoubleArrayFromArrayList(x));
		res.add(ArrayHelper.getDoubleArrayFromArrayList(y));
		return res;
	}
	
	/**
	 * searches and returns the name of the first common class attribute of both passed instances
	 * @param _a the first set of instances
	 * @param _b the second set of instances
	 * @return the first common class attribute
	 */
	@SuppressWarnings("rawtypes")
	public static String getFirstCommonAttribute(Instances _a, Instances _b)
	{	
		Enumeration a = _a.enumerateInstances();
		
		while(a.hasMoreElements())
		{
			Enumeration b = _b.enumerateInstances();
			Instance i1 = (Instance)a.nextElement();
			String val1 = i1.stringValue(_a.classAttribute());
			while(b.hasMoreElements())
			{
				Instance i2 = (Instance)b.nextElement();
				String val2 = i2.stringValue(i2.classAttribute());
				if(val1.equals(val2))
				{
					return val1;
				}
			}
		}
		
		return "";
	}
	
	
//	private int getNo
	
	private void printArrayListContentLength(ArrayList<double[]>_arr)
	{
		StringBuffer sb = new StringBuffer();
		for(double[]arr:_arr)
		{
			sb.append(arr.length);
			sb.append(",");
		}
		
		Log.e(TAG, "array sizes:"+sb.toString());
	}
	
	private ArrayList<double[]> getArrayListFromInstances(Instances _inst, String _attribute2check)
	{
		Instances extracted = InstanceHelper.extractInstances(_inst, _attribute2check);
		return InstanceHelper.generateAttributeValueList(extracted);
//		return InstanceHelper.generateAttributeValueList(_inst);
	}
	
	
	
	/**
	 * computes intercept and slope from the two passed double arrays
	 * (formula: y = a+b*x)
	 * @param _x array containing x values
	 * @param _y array containing y values
	 * @return a double array of size two. index 0 holds the intercept, 
	 * index 1 holds the slope
	 */
	private double[] performLinearRegression(double[] _x, double[] _y) 
	{
		double[] res = new double[2];
		double sumXY =ArrayHelper.sum(ArrayHelper.multiply(_x, _y));
		double sumX = ArrayHelper.sum(_x);
		double sumY = ArrayHelper.sum(_y);
		double sumXSquare = ArrayHelper.sum(ArrayHelper.square(_x));
		double sumSquareX = Math.pow(sumX, 2);
		int n = _x.length;
		
		res[1] = ((n*sumXY)-(sumX*sumY))/((n*sumXSquare)-sumSquareX);
		res[0] = ArrayHelper.mean(_y)-res[1]*ArrayHelper.mean(_x);
		
		return res;
	}
	
	/**
	 * computes values for r and A for the formula: R = A*r^t
	 * @param _x array containing x values
	 * @param _y array containing y values
	 * @return a double array of size two. index 0 holds A, index 1 holds r
	 */
	private double[] performLogarithmicRegression(double[]_x, double[] _y)
	{	
		double[] bm = performLinearRegression(_x, ArrayHelper.log10(_y));
		
		return new double[]{-1*Math.pow(10, bm[0]), Math.pow(10, bm[1])};
	}

	/**
	 * performs linear transformation on a passed dataset
	 * @param _inst the original set of instances
	 * @param _a the offset for the new values
	 * @param _b the slope for the new values
	 * @return a set of instances with transformed values
	 */
	public static Instances performLinearTransformation(Instances _inst, double _a, double _b)
	{
		Instances res = new Instances(_inst, _inst.numInstances());
		for(int i = 0;i<_inst.numInstances();i++)
		{
			Instance curInst = _inst.instance(i);
			DenseInstance transformedInst = new DenseInstance(curInst.numAttributes());
			transformedInst.setDataset(res);
			for(int j = 0;j<curInst.numAttributes();j++)
			{
				Attribute a = curInst.attribute(j);
				if(a.isNominal()||a.isString())
				{
					transformedInst.setValue(j, curInst.stringValue(a));
				}
				else
				{
//					if(curInst.value(j)==UNRECEIVED_BSSID_RSSI)
//					{
//						transformedInst.setValue(j, UNRECEIVED_BSSID_RSSI);
//					}
//					else
//					{
						transformedInst.setValue(j, _a+_b*curInst.value(j));
//					}
				}
			}
			
			res.add(transformedInst);
		}
		return res;
	}
}
