/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 07/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.wifi.recorder.model;

/**
 * subclass of FingerPrintItem specifying all Access Point specific
 * information
 * @author Peter Riedl
 *
 */
public class AccessPoint 
extends FingerPrintItem 
{

	/**
	 * creates an empty AccesPoint
	 */
	public AccessPoint()
	{
		super(Type.ACCESS_POINT);
	}
	
	/**
	 * creates a new AccessPoint
	 * @param _name the name of the AccessPoint
	 * @param _room the room the AccessPoint is in
	 * @param _x the x-coordinate of the AccessPoint
	 * @param _y the y-coordinate of the AccessPoint
	 */
	public AccessPoint(String _name, String _room, double _x,
			double _y) {
		super(Type.ACCESS_POINT, _name, _room, _x, _y);
	}
	
}
