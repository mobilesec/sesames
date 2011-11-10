package at.sesame.fhooe.localizationservice;

interface ILocalizationService
{
	/**
	* queries OSM for IndoorLocalizationNodes in the vicinity and sets data accordingly
	* @return true if indoor localization data are available, false otherwise
	*/
	boolean queryAvailableLocalizationDataSources();
	
	/**
	* sets the path to the set of used training data
	*/
	void setTrainingSetPath(String _path);
	
	/**
	* sets the parameters for linear calibration
	* @param _a the intercept for calibration
	* @param _b the slope for calibration
	*/
	void setLinearCalibrationParameters(double _a, double _b);
	
	/**
	* returns a list of all available classifiers
	* @return a list of all available classifiers
	*/
	List<String> getAvailableClassifiers();
	
	/**
	* sets a the current classifier for the classificator
	* @param _classifierName the name of the classifier to set
	*/
	void setClassifier(String _classifierName);
	
	/**
	* returns the current location as String
	*/
	String getLocation();
	
}