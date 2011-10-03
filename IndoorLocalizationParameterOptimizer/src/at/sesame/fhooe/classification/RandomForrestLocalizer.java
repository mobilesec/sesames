package at.sesame.fhooe.classification;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

public class RandomForrestLocalizer 
implements IClassifier 
{
	private RandomForest mRandomForrest = new RandomForest();
	@Override
	public String getName() 
	{
		return "RandomForrest";
	}

	
	@Override
	public String findOptimalParameters(Instances _trainingSet, Instances _test) 
	{
		if(null==_trainingSet||null==_test)
		{
			System.out.println("random forrest: passed set was null!");
		}
		RandomForest scheme = new RandomForest();
		scheme.setNumFeatures(0);
		scheme.setSeed(1);
		double[] res = Optimizer.generateEmptyDoubleArray(101);
		Evaluation evaluation;
		try 
		{
			
			for(int i = 0;i<=100;i++)
			{
				scheme.setNumTrees(i);
				scheme.buildClassifier(_trainingSet);
				evaluation = new Evaluation(_trainingSet);
				evaluation.evaluateModel(scheme, _test);
				res[i]=evaluation.pctCorrect();
//				System.out.println(i+" ok");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return Optimizer.extractBestResults(res, 0,"num Trees = ");	
	}


	@Override
	public void setTrainingData(Instances _inst) {
		try 
		{
			//optimal parameter
			mRandomForrest.setNumFeatures(0);
			mRandomForrest.setSeed(1);
			mRandomForrest.setNumTrees(12);
			mRandomForrest.buildClassifier(_inst);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public double[] classify(Instance _inst) {
		try {
			return mRandomForrest.distributionForInstance(_inst);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
