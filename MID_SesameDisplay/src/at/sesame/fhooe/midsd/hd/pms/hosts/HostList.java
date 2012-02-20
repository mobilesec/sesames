package at.sesame.fhooe.midsd.hd.pms.hosts;

import java.util.ArrayList;
import java.util.HashMap;

public class HostList 
{
	protected HashMap<String,String>mHosts = new HashMap<String, String>();
	
	public ArrayList<String> getAllMacs()
	{
		return new ArrayList<String>(mHosts.keySet());
	}
	
	public String getHostNameForMac(String _mac)
	{
		return mHosts.get(_mac);
	}

}
