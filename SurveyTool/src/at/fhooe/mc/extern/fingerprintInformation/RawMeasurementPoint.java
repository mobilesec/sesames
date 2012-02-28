package at.fhooe.mc.extern.fingerprintInformation;

public class RawMeasurementPoint 
extends MeasurementPoint 
{
	public RawMeasurementPoint(String _name, String _room)
	{
		super(Type.RAW_MEASUREMENT_POINT);
		setName(_name);
		setRoom(_room);
	}
}
