package at.sesame.fhooe.midsd.data;

import at.sesame.fhooe.midsd.ld.INotificationListener;

public interface ISesameDataProvider 
{
	
	public void addEsmartDataListener(ISesameDataListener _listener, int _id);
	public void removeEsmartDataListener(ISesameDataListener _listener, int _id);
	public void updateEsmartData();
	
	public void addEzanDataListener(ISesameDataListener _listener, int _id);
	public void removeEzanDataListener(ISesameDataListener _listener, int _id);
	public void updateEzanData();
	
	public void addNotificationListener(INotificationListener _listener);
	public void removeNotificationListener(INotificationListener _listener);
	public void updateNotificationListener(String _msg);
	

}
