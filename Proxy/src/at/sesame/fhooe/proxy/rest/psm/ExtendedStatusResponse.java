package at.sesame.fhooe.proxy.rest.psm;

public class ExtendedStatusResponse 
extends StatusResponse 
{
	int idle_since = Integer.MIN_VALUE;
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(String.format("\nidle-since:%d", idle_since));
		return sb.toString();
	}
}
