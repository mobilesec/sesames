package at.sesame.fhooe.lib2.pms.hosts;

import java.util.ArrayList;
import java.util.HashMap;

public class HostList 
{
	protected HashMap<String,String>mHosts = new HashMap<String, String>();
	
	public ArrayList<String> getMacList()
	{
		return new ArrayList<String>(mHosts.keySet());
	}
	
	public String getHostNameForMac(String _mac)
	{
		return mHosts.get(_mac);
	}

	public void addAll(HashMap<String, String> _hosts)
	{
		mHosts.putAll(_hosts);
	}
	
	public HashMap<String, String> getHosts()
	{
		return mHosts;
	}
}
