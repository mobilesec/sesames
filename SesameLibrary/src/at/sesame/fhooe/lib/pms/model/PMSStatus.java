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
 * result of a status call
 *
 */
public class PMSStatus 
{
	/**
	 * contains 0 or 1 for not alive/alive
	 */
	@JsonProperty("alive")
	private String alive;
	
	/**
	 * the ip contained in the status response
	 */
	@JsonProperty("ip")
	private String ip;
	
	/**
	 * the os contained in the status response
	 */
	@JsonProperty("os")
	private String os;
	
	/**
	 * the hostname contained in the status response
	 */
	@JsonProperty("hostname")
	private String hostname;
	
	public String getAlive() {
		return alive;
	}
	public void setAlive(String alive) {
		this.alive = alive;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	@Override
	public String toString() {
		return "PMSStatus [alive=" + alive + ", ip=" + ip + ", os=" + os
				+ ", hostname=" + hostname + "]";
	}

	
}
