package at.sesame.fhooe.lib.fingerprintInformation;

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
	
	protected MeasurementPoint(Type _t)
	{
		super(_t);
	}

	/**
	 * returns a CSV representation of the MeasurementPoint
	 * @return a CSV representation of the MeasurementPoint
	 */
	public String toCSVString() 
	{
		String ownCSVString = createOwnCSVString();
		StringBuffer sb = new StringBuffer();
		for(FingerPrint fp:mPrints)
		{
			sb.append(ownCSVString);
			sb.append(CSV_SEPERATOR);
			sb.append(fp.toCSVString());
		}
		return sb.toString();
	}
	
	/**
	 * creates the portion of the CSV entry that is
	 * independent of stored FingerPrints
	 * @return part of CSV entry
	 */
	private String createOwnCSVString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		sb.append(CSV_SEPERATOR);
		sb.append(getRoom());
		sb.append(CSV_SEPERATOR);
		sb.append(getX());
		sb.append(CSV_SEPERATOR);
		sb.append(getY());
		return sb.toString();
	}
}
