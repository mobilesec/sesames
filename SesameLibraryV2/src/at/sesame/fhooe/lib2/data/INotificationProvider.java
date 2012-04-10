package at.sesame.fhooe.lib2.data;

import java.util.ArrayList;

public interface INotificationProvider
{
	public void registerNotificationreceiver(INotificationListener _recv);
	public void unregisterNotificationreceiver(INotificationListener _recv);
	
	public void updateNotificationReceivers(ArrayList<SesameNotification> _notifications);

}
