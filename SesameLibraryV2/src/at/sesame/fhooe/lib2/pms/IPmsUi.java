package at.sesame.fhooe.lib2.pms;

import android.os.Handler;
import at.sesame.fhooe.lib2.pms.model.ControllableDevice;


public interface IPmsUi 
extends IPMSUpdateListener 
{
	public int getNumSelectedDevices();
//	public boolean handleMultipleSelectionAttempt(ListType _type, boolean _isChecked)
	public void markDirty(ControllableDevice _cd);
	public void deselectAll();
//	public void updateUi();
	public void notifySelectionFail();
	public void notifyDevicesLoaded();
	public void handlePowerClick(ControllableDevice _cd);
//	public Handler getHandler();
}
