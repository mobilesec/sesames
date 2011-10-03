package at.sesame.fhooe.classification;

import weka.classifiers.Evaluation;
import weka.classifiers.lazy.KStar;
import weka.core.Instance;
import weka.core.Instances;

public class KStarLocalizer 
implements IClassifier 
{

	private KStar mKStar = new KStar();
	private Instances mTraining;
	
	@Override
	public String getName() 
	{
		return "K*";
	}

	@Override
	public synchronized String findOptimalParameters(Instances _trainingSet, Instances _test) 
	{
		
		double[] res = Optimizer.generateEmptyDoubleArray(101);
		KStar scheme = new KStar();
		if(null==_trainingSet||null==_test)
		{
			System.out.println("k*: passed set was null!");
		}
		Evaluation evaluation;
		try 
		{
			
			for(int i = 0;i<=100;i++)
			{
				scheme.setGlobalBlend(i);
				scheme.buildClassifier(_trainingSet);
				evaluation = new Evaluation(_trainingSet);
//				long start = System.currentTimeMillis();
				evaluation.evaluateModel(scheme, _test);
//				printEvaluationResults(i, evaluation, start);
				res[i] = evaluation.pctCorrect();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return Optimizer.extractBestResults(res, 0,"B=");
	}
	
	@SuppressWarnings("unused")
	private synchronized void printEvaluationResults(int _b, Evaluation _e, long _start)
	{
		double correct = _e.pctCorrect();
		long duration =  System.currentTimeMillis() - _start;
		System.out.println("B="+_b);
		
		System.out.println("correct classifications:"+correct+"(Duration = "+duration+" ms)");
	}

	@Override
	public synchronized void setTrainingData(Instances _inst) 
	{
		try {
			//optimal parameter
			mKStar.setGlobalBlend(0);
			mKStar.buildClassifier(_inst);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public synchronized double[] classify(Instance _inst) {
		try {
			double res = mKStar.classifyInstance(_inst);
			return mKStar.distributionForInstance(_inst);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
