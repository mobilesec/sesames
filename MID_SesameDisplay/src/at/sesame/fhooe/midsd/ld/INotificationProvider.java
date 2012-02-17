package at.sesame.fhooe.midsd.ld;

public interface INotificationProvider
{
	public void registerNotificationreceiver(INotificationListener _recv);
	public void unregisterNotificationreceiver(INotificationListener _recv);
	
	public void updateNotificationReceivers(String _notification);

}
