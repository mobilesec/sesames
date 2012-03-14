package at.sesame.fhooe.phone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.sesame.fhooe.phone.R;

public class DummyFragment
extends Fragment
{
	private String mText;
	
	public DummyFragment(String _text)
	{
		mText = _text;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dummyfragmentlayout, null);
		TextView tv = (TextView)v.findViewById(R.id.textView1);
		tv.setText(mText);
		return v;
	}
	
	

}
