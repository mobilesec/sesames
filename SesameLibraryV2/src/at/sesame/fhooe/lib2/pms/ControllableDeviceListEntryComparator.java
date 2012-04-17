package at.sesame.fhooe.lib2.pms;

import java.util.Comparator;

public class ControllableDeviceListEntryComparator implements
		Comparator<ControllableDeviceListEntry> {

	private ControllableDeviceComparator mComparator = new ControllableDeviceComparator();
	@Override
	public int compare(ControllableDeviceListEntry lhs,
			ControllableDeviceListEntry rhs) {
		// TODO Auto-generated method stub
		return mComparator.compare(lhs.getControllableDevice(), rhs.getControllableDevice());
	}

}
