package at.sesame.fhooe.proxy.rest.psm;


public class StatusResponse 
{
	int alive = -1;
	String ip = null;
	String os = null;
	
	public String toString()
	{
		return String.format("alive:%d\nip:%s\nos:%s", alive, ip, os);
	}
	
}
