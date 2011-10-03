/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.wifi.recorder.model;

/**
 * base class for all information extracted from sweet home 3d
 * @author Peter Riedl
 *
 */
public class FingerPrintItem 
{
	/**
	 *this enum specifies the two types
	 *of FingerprintItems that are recognized
	 */
	public enum Type
	{
		ACCESS_POINT,
		MEASUREMENT_POINT
	}
	
	/**
	 * the type of this FingerPrintItem
	 */
	private Type mType;
	
	/**
	 * the name of the FingerPrintItem
	 */
	private String mName = "";
	
	/**
	 * the name of the room the FingerPrintItem is in
	 */
	private String mRoom = "";
	
	/**
	 * the x-coordinate of the FingerPrintItem 
	 */
	private double mX = Double.MIN_VALUE;
	
	/**
	 * the y-coordinate of the FingerPrintItem 
	 */
	private double mY = Double.MIN_VALUE;
	
	
	/**
	 * creates a new FingerPrintItem with passed type
	 * @param _type the type of the FingerPrintItem
	 */
	protected FingerPrintItem(Type _type) 
	{
		this.mType = _type;
	}
	
	/**
	 * creates a new FingerPrintItem
	 * @param _type the type of the FingerPrintItem
	 * @param _name the name of the FingerPrintItem
	 * @param _room the room the FingerPritntItem is in
	 * @param _x the x-coordinate of the FingerPrintItem
	 * @param _y the y-coordinate of the FingerPrintItem
	 */
	protected FingerPrintItem(Type _type, String _name, String _room, double _x,
			double _y) {
		this.mType = _type;
		this.mName = _name;
		this.mRoom = _room;
		this.mX = _x;
		this.mY = _y;
	}
	
	public Type getType() {
		return mType;
	}
	public String getName() {
		return mName;
	}
	public void setName(String _name) {
		this.mName = _name;
	}
	public String getRoom() {
		return mRoom;
	}
	public void setRoom(String _room) {
		this.mRoom = _room;
	}
	public double getX() {
		return mX;
	}
	public void setX(double _x) {
		this.mX = _x;
	}
	public double getY() {
		return mY;
	}
	public void setY(double _y) {
		this.mY = _y;
	}
	
	
	
}
