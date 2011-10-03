package at.sesame.fhooe.evaluation;

import java.util.Arrays;

import weka.core.Instance;

public class EvaluationResult 
{
	private Instance mI = null;
	private boolean mSuccess;
	private String mResult;
	
	public EvaluationResult(Instance _i, String _result, boolean _success)
	{
		mI = _i;
		mResult = _result;
		mSuccess = _success;
	}

	public Instance getInstance() {
		return mI;
	}

	public boolean isSuccess() {
		return mSuccess;
	}

	public String getResult() {
		return mResult;
	}
	
	public String toString()
	{
		String instanceString = mI==null?"instance was null":Arrays.toString(mI.toDoubleArray());
		return instanceString+"\n"+
				"matched location = "+mResult+"\n"+
				"correct = "+mSuccess;
	}
	
}
