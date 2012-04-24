package at.sesame.fhooe.lib2.data;

public interface ISesameUpdateListener 
{
	public void notifyPmsUpdate(boolean _success);
	public void notifyEnergyUpdate(boolean _success);
	public void notifyConnectivityLoss();
//	public void finishBecauseDataFail();

}
