/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 10/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.pms.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * this class represents a container for all information contained in the
 * result of an extended status call
 *
 */
public class ExtendedPMSStatus 
extends PMSStatus 
{
	/**
	 * number of seconds the device has been idle
	 */
	@JsonProperty("idle-since")
	private int idleSince;

	public int getIdleSince() {
		return idleSince;
	}

	public void setIdleSince(int idle_since) {
		this.idleSince = idle_since;
	}
	
	@JsonProperty("mac")
	private String mac;
	
	
	public String getMac()
	{
		return mac;
	}
	
	public void setMac(String _mac)
	{
		mac = _mac;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("\nExtendedPMSStatus [idleSince=");
		builder.append(idleSince);
		builder.append(", mac=");
		builder.append(mac);
		builder.append("]");
		return builder.toString();
	}
	
	

}
