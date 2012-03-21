package at.sesame.fhooe.lib.pms.dialogs;

import at.sesame.fhooe.lib.pms.model.ControllableDevice;

public interface IPMSDialogActionHandler 
{
	public void handleDialogPowerOff(ControllableDevice _cd);
	public void handleDialogSleep(ControllableDevice _cd);
	public void handleDialogWakeUp(ControllableDevice _cd);

}
