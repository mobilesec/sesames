package at.sesame.fhooe.evaluation;

import java.util.ArrayList;

import android.util.Log;

import weka.core.Instance;

public class LiveEvaluationResults 
{
	private static final String TAG = "LiveEvaluationResults";
	
	private static ArrayList<EvaluationResult> mList = new ArrayList<EvaluationResult>();
	
	public static void addResult(Instance _i, String _result, boolean _success)
	{
		mList.add(new EvaluationResult(_i, _result, _success));
	}
	
	public static double getCorrectPercentage()
	{
		int numCorrect = getResultsBySuccess(true).size();
		return (double)((double)numCorrect/(double)mList.size()*100);
	}
	
	public static double getIncorrectPercentage()
	{
		int numCorrect = getResultsBySuccess(false).size();
		return (double)((double)numCorrect/(double)mList.size()*100);
	}
	
	public static void printAllResults()
	{
		printResults(mList);
	}
	
	
	public static void printNegativeResults()
	{
		printResults(getResultsBySuccess(false));
	}
	
	public static void printPositiveResults()
	{
		printResults(getResultsBySuccess(true));
	}
	
	private static void printResults(ArrayList<EvaluationResult> _results)
	{
		for(EvaluationResult er:_results)
		{
			Log.e(TAG, er.toString());
			Log.e(TAG, "-----------------");
		}
	}
	
	public static ArrayList<EvaluationResult> getResultsBySuccess(boolean _success)
	{
		ArrayList<EvaluationResult> res = new ArrayList<EvaluationResult>();
		for(EvaluationResult er:mList)
		{
			if(er.isSuccess()==_success)
			{
				res.add(er);
			}
		}
		return res;
	}
	
}
