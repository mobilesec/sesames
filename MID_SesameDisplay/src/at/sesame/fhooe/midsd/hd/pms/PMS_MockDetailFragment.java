package at.sesame.fhooe.midsd.hd.pms;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import at.sesame.fhooe.midsd.R;

public class PMS_MockDetailFragment 
extends DialogFragment 
{
	private int mImageId;
	public PMS_MockDetailFragment(int _imgId)
	{
		mImageId = _imgId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.pms_mock_detail_layout, null);
		ImageView iv = (ImageView)v.findViewById(R.id.pms_mock_detail_layout_image);
		iv.setImageResource(mImageId);
		Button ok = (Button)v.findViewById(R.id.pms_mock_detail_layout_ok_butt);
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				
			}
		});
		return v;
	}

	
	
}
