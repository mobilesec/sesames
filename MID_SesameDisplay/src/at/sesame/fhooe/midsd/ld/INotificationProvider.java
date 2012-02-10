package at.sesame.fhooe.midsd.ld;

public interface INotificationProvider
{
	public void registerNotificationreceiver(INotificationReceiver _recv);
	public void unregisterNotificationreceiver(INotificationReceiver _recv);
	
	public void update(String _msg);

}
