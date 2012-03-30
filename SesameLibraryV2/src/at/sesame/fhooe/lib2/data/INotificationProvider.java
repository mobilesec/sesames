package at.sesame.fhooe.lib2.data;

public interface INotificationProvider
{
	public void registerNotificationreceiver(INotificationListener _recv);
	public void unregisterNotificationreceiver(INotificationListener _recv);
	
	public void updateNotificationReceivers(String _notification);

}
