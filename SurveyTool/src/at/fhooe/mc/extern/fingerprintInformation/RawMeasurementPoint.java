package at.fhooe.mc.extern.fingerprintInformation;

import java.io.Serializable;

public class RawMeasurementPoint 
extends MeasurementPoint
implements Serializable
{
	public RawMeasurementPoint(String _name, String _room)
	{
		super(Type.RAW_MEASUREMENT_POINT);
		setName(_name);
		setRoom(_room);
	}
}
