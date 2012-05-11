package at.sesame.fhooe.tablet;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import at.sesame.fhooe.lib2.pms.ControllableDeviceAdapter;
import at.sesame.fhooe.lib2.pms.IListEntry;
import at.sesame.fhooe.lib2.pms.PmsHelper;

public class DummyAdapterForAir extends ControllableDeviceAdapter {

	public DummyAdapterForAir(Context _ctx, List<IListEntry> objects,
			PmsHelper _uiHelper) {
		super(_ctx, objects, _uiHelper);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int _pos, View _convertView, ViewGroup _parent) {
		// TODO Auto-generated method stub
		return super.getView(_pos, _convertView, _parent);
	}



	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		super.onCheckedChanged(buttonView, isChecked);
	}

	
}
