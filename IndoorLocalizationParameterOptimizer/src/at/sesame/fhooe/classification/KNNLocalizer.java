package at.sesame.fhooe.classification;



import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class KNNLocalizer 
implements IClassifier
{
	
	private IBk mKNN = new IBk(4);
	private Instances mTraining;
	



	@Override
	public String getName() 
	{
		return "KNN";
	}


	@Override
	public String findOptimalParameters(Instances _trainingSet, Instances _test) 
	{
		int k2check = 50;
		double[] res = Optimizer.generateEmptyDoubleArray(k2check);
		IBk scheme = new IBk();
		scheme.setDistanceWeighting(new SelectedTag(IBk.WEIGHT_INVERSE, IBk.TAGS_WEIGHTING));

		Evaluation evaluation;
		try 
		{
			
			for(int i = 1;i<=k2check;i++)
			{
				scheme.setKNN(i);
				scheme.buildClassifier(_trainingSet);
				evaluation = new Evaluation(_trainingSet);
				evaluation.evaluateModel(scheme, _test);
				
				//printConfusionMatrix(evaluation.confusionMatrix());
				
//				System.out.println("results of KNN:"+Arrays.toString(evalRes)+"(size="+evalRes.length+")");
				res[i-1] = evaluation.pctCorrect();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return Optimizer.extractBestResults(res, 1, "K=");
	}
	
	private void printConfusionMatrix(double[][]_mat)
	{
		int i = 0;
		int j = 0;
		for(;i<_mat.length;i++)
		{
//			System.out.println();
			for(;j<_mat[i].length;j++)
			{
				//System.out.print(""+_mat[i][j]+"\t");
			}
		}
		System.out.println("rows:"+i+", cols:"+j);
	}

	@SuppressWarnings("unused")
	private void printEvaluationResults(int _k, Evaluation _e, long _start)
	{
		double correct = _e.pctCorrect();
		long duration =  System.currentTimeMillis() - _start;
		System.out.println("K="+_k);
		System.out.println("correct classifications:"+correct+"(Duration = "+duration+" ms)");
	}


	@Override
	public void setTrainingData(Instances _inst) {
		try 
		{
			//optimal parameter
			mKNN.setKNN(4);
			mKNN.setDistanceWeighting(new SelectedTag(IBk.WEIGHT_INVERSE, IBk.TAGS_WEIGHTING));
			mKNN.buildClassifier(_inst);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}


	@Override
	public double[] classify(Instance _inst) 
	{

//		mKNN.setKNN(4);
//		mKNN.setDistanceWeighting(new SelectedTag(IBk.WEIGHT_INVERSE, IBk.TAGS_WEIGHTING));
		try 
		{
			double res = mKNN.classifyInstance(_inst);
			return mKNN.distributionForInstance(_inst);
			//return mTraining.classAttribute().value((int) res);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
