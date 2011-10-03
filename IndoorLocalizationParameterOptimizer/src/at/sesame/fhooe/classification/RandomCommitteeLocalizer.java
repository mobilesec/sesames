package at.sesame.fhooe.classification;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.RandomCommittee;
import weka.classifiers.trees.RandomTree;
import weka.core.Instance;
import weka.core.Instances;

public class RandomCommitteeLocalizer 
implements IClassifier 
{
	private RandomCommittee mRandomCommittee = new RandomCommittee();
	@Override
	public String getName() 
	{
		return "Random Committee";
	}

	
	@Override
	public String findOptimalParameters(Instances _trainingSet, Instances _test) 
	{
		if(null==_trainingSet||null==_test)
		{
			System.out.println("random committee: passed set was null!");
		}
		RandomCommittee scheme = new RandomCommittee();
		int numMembers2check = 50;
		double[]res = Optimizer.generateEmptyDoubleArray(numMembers2check);

//		String optionsClassName = " -W \"weka.classifiers.trees.RandomTree -K 0 -M 1.0 -S 1\"";
//		String optionsBase = "-S 1 -I ";//10"+className;

		RandomTree rt = new RandomTree();
		rt.setKValue(0);
		rt.setMaxDepth(0);
		rt.setMinNum(1.0);
		scheme.setClassifier(rt);
		
		
		Evaluation evaluation;
		try 
		{	
			for(int i = 1;i<=numMembers2check;i++)
			{
//				String options = optionsBase+i+optionsClassName;
//				System.out.println(options);
//				scheme.setOptions(weka.core.Utils.splitOptions(options));
				scheme.setNumIterations(i);
				scheme.buildClassifier(_trainingSet);
				evaluation = new Evaluation(_trainingSet);
				evaluation.evaluateModel(scheme, _test);
				res[i-1] = evaluation.pctCorrect();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return Optimizer.extractBestResults(res, 1, "num Members =");
		
	}


	@Override
	public void setTrainingData(Instances _inst) 
	{
		try {
			//optimal parameter
			RandomTree rt = new RandomTree();
			rt.setKValue(0);
			rt.setMaxDepth(0);
			rt.setMinNum(1.0);
			mRandomCommittee.setClassifier(rt);
			mRandomCommittee.setNumIterations(10);
			mRandomCommittee.buildClassifier(_inst);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public double[] classify(Instance _inst) {
		try {
			return mRandomCommittee.distributionForInstance(_inst);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
