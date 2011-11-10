package at.sesame.fhooe.lib.vpn;

import android.content.Context;
import at.sesame.fhooe.lib.exceptions.VpnException;
import at.sesame.fhooe.lib.vpn.VpnAccess.ConnectionMode;

public interface IVpnAccess
{
	
	/**
	 * first checks for mode conflicts, then initializes the AuthenticationActor
	 * according to the actual ConnectionMode
	 * @param c the Context for the AuthenticationActor
	 * @throws VpnException when the modes conflict
	 */
	public void initialize(Context _c) throws VpnException;
	
	/**
	 * sets the VpnSettings
	 * @param _setting the VpnSettings to set
	 */
	public void setVpnSetting(VpnSetting _setting);
	
	/**
	 * actually connects to the VPN
	 * @return true if connection is attempted, false if any preconditions
	 * for connection are not satisfied
	 */
	public boolean connect(ConnectionMode _mode);
	
	/**
	 * closes the VPN connection
	 */
	public void disconnect();
	
	/**
	 * creates a new VpnBroadcastReceiver for user notification
	 * @param _c the context to be used
	 */
	public void enableNotifications(Context _c);
}
