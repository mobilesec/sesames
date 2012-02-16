package at.sesame.fhooe.midsd.data;

public interface ISesameDataProvider 
{
	
	public void addEsmartDataListener(ISesameDataListener _listener, int _id);
	public void removeEsmartDataListener(ISesameDataListener _listener, int _id);
	public void updateEsmartData();
	
	public void addEzanDataListener(ISesameDataListener _listener, int _id);
	public void removeEzanDataListener(ISesameDataListener _listener, int _id);
	public void updateEzanData();

}
