package at.sesame.fhooe.viewexperiments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
