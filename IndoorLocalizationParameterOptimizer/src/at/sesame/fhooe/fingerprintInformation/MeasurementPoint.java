package at.sesame.fhooe.fingerprintInformation;

/**
 * subclass of FingerPrintItem specifying all MeasurementPoint specific
 * information
 * @author Peter Riedl
 *
 */
public class MeasurementPoint 
extends FingerPrintItem 
{
	/**
	 * constant containing the separator used in the CSV file
	 */
	public static final String CSV_SEPERATOR = ";";
	
	
	/**
	 * creates an empty MeasurementPoint
	 */
	public MeasurementPoint()
	{
		super(Type.MEASUREMENT_POINT);
	}
	
	/**
	 * creates an MeasurementPoint
	 * @param _name the name of the MeasurementPoint
	 * @param _room the room the MeasurementPoint is in
	 * @param _x the x-coordinate of the MeasurementPoint
	 * @param _y the y-coordinate of the MeasurementPoint
	 */
	public MeasurementPoint(String _name, String _room,
			double _x, double _y) 
	{
		super(Type.MEASUREMENT_POINT, _name, _room, _x, _y);
	}	
}
