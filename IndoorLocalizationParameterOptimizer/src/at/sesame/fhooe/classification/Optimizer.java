package at.sesame.fhooe.classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import at.sesame.fhooe.fingerprintInformation.FingerPrintItem;
import at.sesame.fhooe.fingerprintInformation.Room;

import weka.core.Instance;
import weka.core.Instances;

public class Optimizer
{
	/**
	 * a list of all evaluated classifiers
	 */
	private ArrayList<IClassifier> mClassifiers = new ArrayList<IClassifier>();
	
	/**
	 * a list of all rooms stored in the datasets
	 */
	private ArrayList<Room> mRooms;
	
	/**
	 * a list of all FingerPrintItems in the dataset
	 */
	private ArrayList<FingerPrintItem> mFPIs;
	
	/**
	 *  number of classifiers used
	 */
	private static int mNumClassifiers = 0;
	
	/**
	 * number of combinations of training- and test-datasets
	 */
	private static int mNumCombinations = 0;
	
	/**
	 * number of currently finished sets
	 */
	private static int mNumCurResults = 0;
	
	/**
	 * number of correctly matched MPs 
	 */
	private int mNoCorrectMatchedMP = 0;
	
	/**
	 * number of incorrectly matched MPs
	 */
	private int mNoNotCorrectMatchedMP = 0;
	
	/**
	 * sum of errors in m of the currently evaluated classifier
	 */
	private int mSumMatchedMPErrors = 0;
	
	/**
	 * number of correctly matched rooms by MP
	 */
	private int mNoCorrectMatchedRoomMP = 0;
	
	/**
	 * number of incorrectly matched rooms by MP
	 */
	private int mNoNotCorrectMatchedRoomMP = 0;
	
	/**
	 * number of correctly matched rooms after post processing
	 */
	private int mNoCorrectMatchedRoomAfterPP = 0;
	
	/**
	 * number of incorrectly matched rooms after post processing
	 */
	private int mNoNotCorrectMatchedRoomAfterPP = 0;
	
	/**
	 * sum of errors in m of the currently evaluated classifier after post processing
	 */
	private int mSumMatchedMPErrorsPP = 0;
	
	
	
	private int mNoMatchedRooms = 0;
	private int mNoCorrectMatchedRooms = 0;
	private int mNoNotCorrectMatchedRooms = 0;
	
	/**
	 * list of the results of all optimized classifiers
	 */
	private static ArrayList<String> mResults = new ArrayList<String>();
	
	/**
	 * creates a new optimizer
	 * @param _rooms a list of all rooms in the datasets
	 * @param _fpis a list of all FingerPrintItems in the datasets
	 */
	public Optimizer(ArrayList<Room> _rooms, ArrayList<FingerPrintItem> _fpis)
	{
		mRooms = _rooms;
		mFPIs = _fpis;
//		mClassifiers.add(new KNNLocalizer());
//		mClassifiers.add(new KStarLocalizer());
//		mClassifiers.add(new RandomCommitteeLocalizer());
		mClassifiers.add(new RandomForrestLocalizer());
		mNumClassifiers = mClassifiers.size();
//		optimizeParameters();
//		batchClassifyMP();
		batchClassifyRoom();
	}
	
	private void batchClassifyMP()
	{	
		Instances all_mp = Optimizer.getInstancesFromFile("res/all/allDataForWekaTraining_MP.arff");
		Instances desire_mp = Optimizer.getInstancesFromFile("res/desire/desireData_20_07_mp.arff");
		Instances nexus_mp = Optimizer.getInstancesFromFile("res/nexus/nexusData_20_07_mp.arff");
		Instances g1_mp = Optimizer.getInstancesFromFile("res/g1/g1Data_21_07_mp.arff");
		
		Instances desire_mp_normalized_all = Optimizer.getInstancesFromFile("res/normalized/desireNormalizedForAll.arff");
		Instances nexus_mp_normalized_all = Optimizer.getInstancesFromFile("res/normalized/nexusNormalizedForAll.arff");
		Instances g1_mp_normalized_all = Optimizer.getInstancesFromFile("res/normalized/g1NormalizedForAll.arff");
		
		Instances desire_mp_normalized_nexus = Optimizer.getInstancesFromFile("res/normalized/desireNormalizedForNexus.arff");
		Instances g1_mp_normalized_nexus = Optimizer.getInstancesFromFile("res/normalized/g1NormalizedForNexus.arff");
		
		Instances nexus_mp_normalized_desire = Optimizer.getInstancesFromFile("res/normalized/nexusNormalizedForDesire.arff");
		Instances g1_mp_normalized_desire = Optimizer.getInstancesFromFile("res/normalized/g1NormalizedForDesire.arff");
		
		Instances desireCalibratedForAll = Optimizer.getInstancesFromFile("res/normalizedV2/desireCalibratedForAll.arff");
		Instances desireCalibratedForG1 = Optimizer.getInstancesFromFile("res/normalizedV2/desireCalibratedForG1.arff");
//		Instances desireCalibratedForNexus = Optimizer.getInstancesFromFile("res/normalizedV2/desireCalibratedForNexus.arff");
		Instances desireCalibratedForNexus = Optimizer.getInstancesFromFile("res/normalizedSingleMP/desireCalibratedForNexus.arff");

		
		Instances g1CalibratedForAll = Optimizer.getInstancesFromFile("res/normalizedV2/g1CalibratedForAll.arff");
//		Instances g1CalibratedForDesire = Optimizer.getInstancesFromFile("res/normalizedV2/g1CalibratedForDesire.arff");
//		Instances g1CalibratedForNexus = Optimizer.getInstancesFromFile("res/normalizedV2/g1CalibratedForNexus.arff");
		Instances g1CalibratedForDesire = Optimizer.getInstancesFromFile("res/normalizedSingleMP/g1CalibratedForDesire.arff");
		Instances g1CalibratedForNexus = Optimizer.getInstancesFromFile("res/normalizedSingleMP/g1CalibratedForNexus.arff");
		
		Instances nexusCalibratedForAll = Optimizer.getInstancesFromFile("res/normalizedV2/nexusCalibratedForAll.arff");
//		Instances nexusCalibratedForDesire = Optimizer.getInstancesFromFile("res/normalizedV2/nexusCalibratedForDesire.arff");
		Instances nexusCalibratedForDesire = Optimizer.getInstancesFromFile("res/normalizedSingleMP/nexusCalibratedForDesire.arff");
		Instances nexusCalibratedForG1 = Optimizer.getInstancesFromFile("res/normalizedV2/nexusCalibratedForG1.arff");
		
//		classify(all_mp, all_mp, "all","all");
//		classify(all_mp, desireCalibratedForAll, "all","desire calibrated for all");
//		classify(all_mp, nexusCalibratedForAll, "all","nexus calibrated for all");
//		classify(all_mp, g1CalibratedForAll, "all","g1 calibrated for all");
		
		
//		classify(nexus_mp, nexus_mp, "nexus", "nexus");
//		classify(nexus_mp, desire_mp, "nexus" , "desire");
		classifyMP(nexus_mp, desireCalibratedForNexus, "nexus", "desire calibrated for nexus");
//		classify(nexus_mp, g1_mp, "nexus","g1");
		classifyMP(nexus_mp, g1CalibratedForNexus, "nexus","g1 calibrated for nexus");
		
//		classify(desire_mp, desire_mp, "desire","desire");
//		classify(desire_mp, nexus_mp, "desire","nexus");
		classifyMP(desire_mp, nexusCalibratedForDesire, "desire","nexus calibrated for desire");
//		classify(desire_mp, g1_mp, "desire","g1");
		classifyMP(desire_mp, g1CalibratedForDesire, "desire","g1 calibrated for desire");
		System.out.println("Finished batch classification");
	}
	
	private void batchClassifyRoom()
	{
		Instances allRoom = Optimizer.getInstancesFromFile("res/all/allDataForWekaTraining_Room.arff");
		Instances desireRoom = Optimizer.getInstancesFromFile("res/desire/desireData_20_07_rooms.arff");
		Instances nexusRoom = Optimizer.getInstancesFromFile("res/nexus/nexusData_20_07_rooms.arff");
		Instances g1Room = Optimizer.getInstancesFromFile("res/g1/g1Data_21_07_rooms.arff");
		
		Instances desireCalibratedForAll = Optimizer.getInstancesFromFile("res/normalizedRoomV2/desireCalibratedForAll.arff");
		Instances desireCalibratedForNexus = Optimizer.getInstancesFromFile("res/normalizedRoomV2/desireCalibratedForNexus.arff");
		
		Instances nexusCalibratedForAll = Optimizer.getInstancesFromFile("res/normalizedRoomV2/nexusCalibratedForAll.arff");
		Instances nexusCalibratedForDesire = Optimizer.getInstancesFromFile("res/normalizedRoomV2/nexusCalibratedForDesire.arff");
		
		Instances g1CalibratedForAll = Optimizer.getInstancesFromFile("res/normalizedRoomV2/g1CalibratedForAll.arff");
		Instances g1CalibratedForDesire = Optimizer.getInstancesFromFile("res/normalizedRoomV2/g1CalibratedForDesire.arff");
		Instances g1CalibratedForNexus = Optimizer.getInstancesFromFile("res/normalizedRoomV2/g1CalibratedForNexus.arff");
		
		classifyRoom(allRoom, allRoom, "all", "all");
		
		classifyRoom(allRoom, desireRoom, "all", "desire");
		classifyRoom(allRoom, desireCalibratedForAll, "all", "desire calibrated");
		
		classifyRoom(allRoom, nexusRoom, "all", "nexus");
		classifyRoom(allRoom, nexusCalibratedForAll, "all", "nexus calibrated");
		
		classifyRoom(allRoom, g1Room, "all", "g1");
		classifyRoom(allRoom, g1CalibratedForAll, "all", "g1 calibrated");
		
		
		classifyRoom(desireRoom, desireRoom, "desire", "desire");
		classifyRoom(desireRoom, nexusRoom, "desire", "nexus");
		classifyRoom(desireRoom, nexusCalibratedForDesire, "desire", "nexus calibrated");
		classifyRoom(desireRoom, g1Room, "desire", "g1");
		classifyRoom(desireRoom, g1CalibratedForDesire, "desire", "g1 calibrated");
		
		classifyRoom(nexusRoom, nexusRoom, "nexus", "nexus");
		classifyRoom(nexusRoom, desireRoom, "nexus", "desire");
		classifyRoom(nexusRoom, desireCalibratedForNexus, "nexus", "desire calibrated");
		classifyRoom(nexusRoom, g1Room, "nexus", "g1");
		classifyRoom(nexusRoom, g1CalibratedForNexus, "nexus", "g1 calibrated");
		
		System.out.println("batch classification for room finished");
	}
	
	private void classifyRoom(final Instances _train, final Instances _test, final String _trainingName, final String _testName)
	{
		for(IClassifier classifier:mClassifiers)
		{
			long trainingTime = 0 ;
			long classificationTime = 0;
			resetCountersRoom();
			System.out.println("--------------------");
			System.out.println("Classifier:"+classifier.getName());
			System.out.println("training set:"+_trainingName);
			System.out.println("test set    :"+_testName);
			System.out.println("--------------------");
			for(int i = 0;i<_test.numInstances();i++)
			{

				//						Instances train = new Instances(allMP);

				//						Instance inst = allMP.instance(i);
				//						train.delete(i);
				Instance inst = _test.instance(i);
				long trainingStart = System.currentTimeMillis();
				classifier.setTrainingData(_train);
				trainingTime+=(System.currentTimeMillis()-trainingStart);
				long classificationStart = System.currentTimeMillis();
				double[] dist = classifier.classify(inst);
				classificationTime+=(System.currentTimeMillis()-classificationStart);


				//			printDistribution(dist);

				postProcessRoom(_train, dist, inst.stringValue(inst.classIndex()));
			}
			printRoomStatistics();
		}
	}
	
	private void postProcessRoom(Instances _train, double[] _dist, String _correctRoom)
	{
		int matchedIdx = getMaxIdx(_dist);
		String matchedRoom = _train.classAttribute().value(matchedIdx);
		mNoMatchedRooms++;
		
		if(matchedRoom.equals(_correctRoom))
		{
			mNoCorrectMatchedRooms++;
		}
		else
		{
			mNoNotCorrectMatchedRooms++;
		}
	}
	
	private void printRoomStatistics()
	{
		double percentCorrect = ((double)mNoCorrectMatchedRooms)/((double)mNoMatchedRooms)*100;
		double percentIncorrect = ((double)mNoNotCorrectMatchedRooms)/((double)mNoMatchedRooms)*100;
		
		System.out.println("total number of matched rooms:"+mNoMatchedRooms);
		System.out.println("number of correctly matched rooms:"+mNoCorrectMatchedRooms);
		System.out.println("percentage of correctly matched rooms:"+percentCorrect);
		System.out.println("number of incorrectly matched rooms:"+mNoNotCorrectMatchedRooms);
		System.out.println("percentage of incorrectly matched rooms:"+percentIncorrect);
		System.out.println("\n");
	}
	
	/**
	 * starts classification with all classifiers to evaluate distance errors
	 */
	private void classifyMP(final Instances _train, final Instances _test, final String _trainingName, final String _testName)
	{
		//Instances allMP = Optimizer.getInstancesFromFile("res/all/allDataForWekaTraining_MP.arff");
		//IClassifierOptimizer knn = new KNNLocalizer();

		for(IClassifier classifier:mClassifiers)
		{
			long trainingTime = 0 ;
			long classificationTime = 0;
			resetCountersMP();
			System.out.println("--------------------");
			System.out.println("Classifier:"+classifier.getName());
			System.out.println("training set:"+_trainingName);
			System.out.println("test set    :"+_testName);
			System.out.println("--------------------");
			for(int i = 0;i<_test.numInstances();i++)
			{

				//						Instances train = new Instances(allMP);

				//						Instance inst = allMP.instance(i);
				//						train.delete(i);
				Instance inst = _test.instance(i);
				long trainingStart = System.currentTimeMillis();
				classifier.setTrainingData(_train);
				trainingTime+=(System.currentTimeMillis()-trainingStart);
				long classificationStart = System.currentTimeMillis();
				double[] dist = classifier.classify(inst);
				classificationTime+=(System.currentTimeMillis()-classificationStart);


				//			printDistribution(dist);

				postProcess(_train, dist, inst.stringValue(inst.classIndex()));
			}
			double trainingTimePerInstance = ((double)trainingTime)/_test.numInstances();
			double classificationTimePerInstance = ((double)classificationTime)/_test.numInstances();
			
			System.out.println("training time:"+trainingTime+" ms (~per Instance:"+trainingTimePerInstance+" ms)");
			System.out.println("classification time:"+classificationTime+" ms (~per Instance:"+classificationTimePerInstance+" ms)");
			printStatistics();

		}
	}
	
	/**
	 * prints all available statistics about the currently evaluated classifier
	 */
	private synchronized void printStatistics()
	{
		int noMPMatches = mNoCorrectMatchedMP+mNoNotCorrectMatchedMP;
		int noRoomMatches = mNoCorrectMatchedRoomMP+mNoNotCorrectMatchedRoomMP;
		int noRoomMatchesPP = mNoCorrectMatchedRoomAfterPP+mNoNotCorrectMatchedRoomAfterPP;
		if(	noMPMatches == 0 ||
			noRoomMatches == 0 ||
			noRoomMatchesPP == 0)
		{
			System.out.println("bug");
			return;
		}
		
		System.out.println("number of correct MP matches:"+mNoCorrectMatchedMP+"("+(double)100*mNoCorrectMatchedMP/noMPMatches+"%)");
		System.out.println("number of incorrect MP matches:"+mNoNotCorrectMatchedMP+"("+(double)100*mNoNotCorrectMatchedMP/noMPMatches+"%)");
		System.out.println("average error:"+(double)(mSumMatchedMPErrors/mNoNotCorrectMatchedMP)/100+"m");
		
		System.out.println("number of correct matched rooms:"+mNoCorrectMatchedRoomMP+"("+(double)100*mNoCorrectMatchedRoomMP/noRoomMatches+"%)");
		System.out.println("number of incorrect matched rooms:"+mNoNotCorrectMatchedRoomMP+"("+(double)100*mNoNotCorrectMatchedRoomMP/noRoomMatches+"%)");
		
		System.out.println("number of correct matched rooms after pp:"+mNoCorrectMatchedRoomAfterPP+"("+(double)100*mNoCorrectMatchedRoomAfterPP/noRoomMatchesPP+"%)");
		System.out.println("number of incorrect matched rooms after pp:"+mNoNotCorrectMatchedRoomAfterPP+"("+(double)100*mNoNotCorrectMatchedRoomAfterPP/noRoomMatchesPP+"%)");
		System.out.println("average error:"+(double)(mSumMatchedMPErrorsPP/(noRoomMatches))/100+"m");
		
		System.out.println("correct rooms (%):"+(double)100*mNoCorrectMatchedRoomMP/noRoomMatches);
		System.out.println("error (m):        "+(double)(mSumMatchedMPErrors/mNoNotCorrectMatchedMP)/100);
		System.out.println("error PP (m):     "+(double)(mSumMatchedMPErrorsPP/(noRoomMatches))/100);
		
		
		System.out.println("\n\n");
	}
	
	/**
	 * a very simple step of post processing. the 3 best results (matches to MP) of the classifier are
	 * averaged and that position is used for further evaluation
	 * @param _train the set of training-data that was used
	 * @param _dist a double[] containing the distribution of the currently evaluated classifier
	 * @param _correctMPName the name of the correct MP
	 */
	private synchronized void postProcess(Instances _train, double[] _dist, String _correctMPName)
	{	
		FingerPrintItem fpi = getFPIByName(_correctMPName);
		
		int maxIdx = getMaxIdx(_dist);
		if(maxIdx==-1)
		{
			return;
		}
		String matchedMPName = _train.classAttribute().value(maxIdx);
		FingerPrintItem matchedMP = getFPIByName(matchedMPName);
		
		String roomOfMatchedMP = matchedMP.getRoom();
		
		
		int[] highestIdx = getHighestIndices(_dist, 3);
		ArrayList<FingerPrintItem> items = getItemsByIdx(highestIdx, _train);
		double[]avgPos = averagePosition(items);
		Room roomAfterPostProcessing = getContainingRoom(avgPos[0], avgPos[1]);
		
		
		if(matchedMPName.equals(_correctMPName))
		{
			mNoCorrectMatchedMP++;
		}
		else
		{
			mNoNotCorrectMatchedMP++;
			mSumMatchedMPErrors+=getDistance(fpi.getX(), fpi.getY(), matchedMP.getX(), matchedMP.getY());
		}
		
		if(fpi.getRoom().equals(roomOfMatchedMP))
		{
			mNoCorrectMatchedRoomMP++;
		}
		else
		{
			mNoNotCorrectMatchedRoomMP++;
		}
		
		
		String roomNameAfterPP = roomAfterPostProcessing==null?"no match found":roomAfterPostProcessing.getName();
		if(fpi.getRoom().equals(roomNameAfterPP))
		{
			mNoCorrectMatchedRoomAfterPP++;
			mSumMatchedMPErrorsPP+=getDistance(fpi.getX(), fpi.getY(), avgPos[0], avgPos[1]);
		}
		else
		{
			mNoNotCorrectMatchedRoomAfterPP++;
			mSumMatchedMPErrorsPP+=getDistance(fpi.getX(), fpi.getY(), avgPos[0], avgPos[1]);
		}		
	}
	
	/**
	 * resets all counters used for statistical evaluation based on MP
	 */
	private synchronized void resetCountersMP()
	{
		mNoCorrectMatchedMP = 0;
		mNoCorrectMatchedRoomMP = 0;
		mNoCorrectMatchedRoomAfterPP = 0;
		
		mNoNotCorrectMatchedMP = 0;
		mNoNotCorrectMatchedRoomMP = 0;
		mNoNotCorrectMatchedRoomAfterPP = 0;
		
		mSumMatchedMPErrors = 0;
		mSumMatchedMPErrorsPP = 0;
	}
	
	/**
	 * resets all counters used for statistical evaluation based on room
	 */
	private void resetCountersRoom()
	{
		mNoMatchedRooms = 0;
		mNoCorrectMatchedRooms = 0;
		mNoNotCorrectMatchedRooms = 0;
	}
	
	/**
	 * computes the distance between two passed points
	 * @param _x1 the x-coordinate of the first point
	 * @param _y1 the y-coordinate of the first point
	 * @param _x2 the x-coordinate of the second point
	 * @param _y2 the y-coordinate of the second point
	 * @return
	 */
	private double getDistance(double _x1, double _y1, double _x2, double _y2)
	{
		return Math.sqrt(Math.pow(_x2-_x1, 2)+Math.pow(_y2-_y1, 2));
	}
	
	/**
	 * computes the index of the maximal value in the passed array and returns it
	 * @param _vals the array containing the values
	 * @return the index of the maximal value
	 */
	private int getMaxIdx(double[] _vals)
	{
		if(null==_vals)
		{
			return -1;
		}
		double max = Double.MIN_VALUE;
		int maxIdx = -1;
		
		for(int i = 0;i<_vals.length;i++)
		{
			if(_vals[i]>max)
			{
				max = _vals[i];
				maxIdx = i;
			}
		}
		return maxIdx;
	}
	
	/**
	 * maps the passed indices of MPs to actual FingerPrintItems and returns the list
	 * @param _idx the indices of matching results
	 * @param _data the dataset containing the actual name of the matched MP
	 * @return a list of FingerPrintItems represented by the passed indices
	 */
	private ArrayList<FingerPrintItem> getItemsByIdx(int[] _idx, Instances _data)
	{
		ArrayList<FingerPrintItem> res = new ArrayList<FingerPrintItem>();
		for(int i = 0;i<_idx.length;i++)
		{
			String name = _data.classAttribute().value(_idx[i]);
			res.add(getFPIByName(name));
		}
		
		return res;
	}
	
	/**
	 * computes a new position consisting of the average of all positions of the passed list
	 * @param _items a list of FingerPrintItems which's positions have to be averaged
	 * @return the averaged position of all passed FingerPrintItems
	 */
	private double[] averagePosition(ArrayList<FingerPrintItem> _items)
	{
		double sumX = 0;
		double sumY = 0;
		
		for(FingerPrintItem fpi:_items)
		{
			sumX+=fpi.getX();
			sumY+=fpi.getY();
		}
		return new double[]{sumX/_items.size(),sumY/_items.size()};
	}
	
	/**
	 * returns the room a point specified by X and Y is in
	 * @param _x the x-coordinate of the point
	 * @param _y the y-coordinate of the point
	 * @return the room the point is in or null if the passed point is in none of the rooms
	 */
	private Room getContainingRoom(double _x, double _y)
	{
		for(Room r:mRooms)
		{
			if(r.contains(_x, _y))
			{
				return r;
			}
		}
		return null;
	}
	
	/**
	 * computes the indices of the n highest values in the array. 
	 * @param _vals the array containing the values
	 * @param _numBest the number (n) of highest indices
	 * @return an array containing the indices
	 */
	private int[]getHighestIndices(double[]_vals, int _numBest)
	{
		int[] res = new int[_numBest];
		ArrayList<Integer> idx2skip = new ArrayList<Integer>();
		double max = Double.MIN_VALUE;
		int maxIdx = -1;
		
		for(int i = 0;i<res.length;i++)
		{
			
			for(int j = 0;j<_vals.length;j++)
			{
				if(!hasToBeSkipped(idx2skip, j))
				{
					if(_vals[j]>max)
					{
						max = _vals[j];
						maxIdx = j;
					}
				}
			}
			max = Double.MIN_VALUE;
			res[i] = maxIdx;
			idx2skip.add(new Integer(maxIdx));
		}
		return res;
	}
	
	/**
	 * checks if the passed list contains the passed value (workaround, because ArrayList.conains did not
	 * work as expected.
	 * @param _list the list of Integers to check
	 * @param _val the value to compare
	 * @return true, if the value is contained in the list, false otherwise
	 */
	private boolean hasToBeSkipped(ArrayList<Integer> _list, int _val)
	{
		for(Integer i:_list)
		{
			if(i.intValue()==_val)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * searches for a certain FingerPrintItem specified by it's name and returns it
	 * @param _name name of the FingerPrintItem
	 * @return the FingerPrintItem with the passed name, or null if no FingerPrintItem was found
	 */
	private FingerPrintItem getFPIByName(String _name)
	{
		for(FingerPrintItem fpi:mFPIs)
		{
			if(fpi.getName().equals(_name))
			{
				return fpi;
			}
		}
		return null;
	}
	
	
	/**
	 * starts the optimization process for all available datasets and all classifiers in the list
	 * of classifiers
	 */
	public void optimizeParameters()
	{
		System.out.println("generating instances....");
		Instances all_room = Optimizer.getInstancesFromFile("res/all/allDataForWekaTraining_Room.arff");
		Instances desire_room = Optimizer.getInstancesFromFile("res/desire/desireData_20_07_rooms.arff");
		Instances nexus_room = Optimizer.getInstancesFromFile("res/nexus/nexusData_20_07_rooms.arff");
		Instances g1_room = Optimizer.getInstancesFromFile("res/g1/g1Data_21_07_rooms.arff");
		
		Instances all_mp = Optimizer.getInstancesFromFile("res/all/allDataForWekaTraining_MP.arff");
		Instances desire_mp = Optimizer.getInstancesFromFile("res/desire/desireData_20_07_mp.arff");
		Instances nexus_mp = Optimizer.getInstancesFromFile("res/nexus/nexusData_20_07_mp.arff");
		Instances g1_mp = Optimizer.getInstancesFromFile("res/g1/g1Data_21_07_mp.arff");
		
		Instances desire_mp_normalized_all = Optimizer.getInstancesFromFile("res/normalized/desireNormalizedForAll.arff");
		Instances nexus_mp_normalized_all = Optimizer.getInstancesFromFile("res/normalized/nexusNormalizedForAll.arff");
		Instances g1_mp_normalized_all = Optimizer.getInstancesFromFile("res/normalized/g1NormalizedForAll.arff");
		
		Instances desire_mp_normalized_nexus = Optimizer.getInstancesFromFile("res/normalized/desireNormalizedForNexus.arff");
		Instances g1_mp_normalized_nexus = Optimizer.getInstancesFromFile("res/normalized/g1NormalizedForNexus.arff");
		
		Instances nexus_mp_normalized_desire = Optimizer.getInstancesFromFile("res/normalized/nexusNormalizedForDesire.arff");
		Instances g1_mp_normalized_desire = Optimizer.getInstancesFromFile("res/normalized/g1NormalizedForDesire.arff");

		final Instances[] training = new Instances[]{	//all_room, all_room, all_room, all_room,
//														desire_room, desire_room, desire_room, desire_room,
//														nexus_room, nexus_room, nexus_room, nexus_room,
//														g1_room, g1_room, g1_room, g1_room,
														nexus_mp, nexus_mp, nexus_mp, nexus_mp/*,
														desire_mp, desire_mp, desire_mp, desire_mp,
														nexus_mp, nexus_mp, nexus_mp, nexus_mp,
														g1_mp, g1_mp, g1_mp, g1_mp*/};
		
		final Instances[]test = new Instances[]{//all_room, desire_room, nexus_room, g1_room,
//												all_room, desire_room, nexus_room, g1_room,
//												all_room, desire_room, nexus_room, g1_room,
//												all_room, desire_room, nexus_room, g1_room,
//												all_mp, desire_mp, nexus_mp, g1_mp,
//												all_mp, desire_mp, nexus_mp, g1_mp,
//												all_mp, desire_mp, nexus_mp, g1_mp,
//												all_mp, desire_mp, nexus_mp, g1_mp
												desire_mp, desire_mp_normalized_nexus, g1_mp_normalized_nexus, nexus_mp
												};
		
		final String[] trainingNames = new String[]{//"all_room","all_room","all_room","all_room",
//													"desire_room","desire_room","desire_room","desire_room",
//													"nexus_room","nexus_room","nexus_room","nexus_room",
//													"g1_room","g1_room","g1_room","g1_room",
													"nexus_mp","nexus_mp","nexus_mp","nexus_mp"
//													"desire_mp","desire_mp","desire_mp","desire_mp",
//													"nexus_mp","nexus_mp","nexus_mp","nexus_mp",
//													"g1_mp","g1_mp","g1_mp","g1_mp"
													};
		
		final String[] testNames = new String[]{//"all_room", "desire_room", "nexus_room", "g1_room",
//												"all_room", "desire_room", "nexus_room", "g1_room",
//												"all_room", "desire_room", "nexus_room", "g1_room",
//												"all_room", "desire_room", "nexus_room", "g1_room",
//												"all_mp", "desire_mp", "nexus_mp", "g1_mp",
//												"all_mp", "desire_mp", "nexus_mp", "g1_mp",
//												"all_mp", "desire_mp", "nexus_mp", "g1_mp",
//												"all_mp", "desire_mp", "nexus_mp", "g1_mp"
												"desire", "desire (normalized nexus)", "g1 (normalized nexus)", "nexus"
												};
		
		
//		final Instances[] training = new Instances[]{all_mp, all_mp};
//		final Instances[] test = new Instances[]{desire_mp, desire_mp_normalized};
//		final String[] trainingNames = new String []{"all", "all"};
//		final String[] testNames = new String []{"desire", "desire(normalized)"};
//		final Instances[] training = new Instances[]{all_mp};
//		final Instances[] test = new Instances[]{g1_mp_normalized};
//		final String[] trainingNames = new String []{"all"};
//		final String[] testNames = new String []{"g1"};
		mNumCombinations = training.length;
		System.out.println("starting evaluation...");
		
		for(final IClassifier classifier:mClassifiers)
		{
			for(int i = 0;i<training.length;i++)
			{
				final int idx = i;
				new Thread(new Runnable() 
				{	
					@Override
					public void run() 
					{	
						String optimal = classifier.findOptimalParameters(training[idx], test[idx]);
						
//						System.out.println("Classifier: "+classifier.getName());
						String result = classifier.getName()+": "+trainingNames[idx]+"/"+testNames[idx]+": "+optimal;
						mResults.add(result);
						notifySetFinished();
					}
				}).start();	
			}
		}
	}
	
	/**
	 * parses instanes from a file and returns them
	 * @param _path the path to the file containing the instances
	 * @return the object representations of the instances contained in the file
	 */
	public static Instances getInstancesFromFile(String _path)
    {
        Instances training = null;
        try 
        {
        	File f = new File(_path);
			BufferedReader brTrain = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			training = new Instances(brTrain);			
			training.setClassIndex(0);	
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
        if(null==training)
        {
        	System.out.println("error parsing file");
        }

        return training;
    }
	
	/**
	 * extracts the optimal parameters based on highest probability in the passed distribution
	 * @param _res the distribution of probabilities of results
	 * @param _idxOffset offset used, if the range of the optimal parameter does not start with 0
	 * @param _parameterLabel a name describing the parameter (only needed for output)
	 * @return a string containing the list of optimal parameters
	 */
	public static  String extractBestResults(double[] _res,  int _idxOffset, String _parameterLabel)
	{
		double max = Double.MAX_VALUE*-1;
		ArrayList<Integer> maxIdx = new ArrayList<Integer>();
		for(int i = 0;i<_res.length;i++)
		{
			if(_res[i]>max)
			{
				
				max = _res[i];
			}
		}
		for(int i = 0;i<_res.length;i++)
		{
			if(_res[i]==max)
			{
				maxIdx.add(i+_idxOffset);
			}
		}
		StringBuffer sb = new StringBuffer();
		for(int ind:maxIdx)
		{
			sb.append(ind);
			sb.append(", ");
		}
		String res = sb.toString().substring(0, sb.toString().length()-2);
		return "Hitrate= "+max+"% ("+_parameterLabel+res+")";
	}
	
	/**
	 * called when one combination of datasets for one classifier has finished optimization
	 */
	public static void notifySetFinished()
	{
		int absoluteResults = mNumClassifiers*mNumCombinations;
		mNumCurResults++;
		if(mNumCurResults==absoluteResults)
		{	
			String[] resArr = new String[mResults.size()];
			resArr = mResults.toArray(resArr);
			Arrays.sort(resArr);
			
			for(String s:resArr)
			{
				System.out.println(s);
			}
		}
		else
		{
			System.out.println("sets finished:"+mNumCurResults+"/"+absoluteResults);
		}
	}
	
	/**
	 * creates and initializes an empty double array of specified length
	 * @param _len the length of the array to be created
	 * @return the empty array
	 */
	public static double[] generateEmptyDoubleArray(int _len)
	{
		double[] res = new double[_len];
		for(int i = 0;i<res.length;i++)
		{
			res[i]=0.0;
		}
		return res;
	}
}
