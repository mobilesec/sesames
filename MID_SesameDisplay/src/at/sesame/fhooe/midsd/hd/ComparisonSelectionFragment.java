package at.sesame.fhooe.midsd.hd;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import at.sesame.fhooe.midsd.R;
import at.sesame.fhooe.midsd.hd.ComparisonFragment.DisplayMode;

public class ComparisonSelectionFragment 
extends Fragment
implements OnCheckedChangeListener, android.widget.RadioGroup.OnCheckedChangeListener
{
	@SuppressWarnings("unused")
	private static final String TAG = "ComparisonSelectionFragment";
	
	private final String mCb1Text;
	private final String mCb2Text;
	private final String mCb3Text;
	private final String mCb4Text;
	
	private boolean[] mCheckedFilters = new boolean[]{false, false, false, false};
	
	private IComparisonSelectionListener mListener;
	
	private Context mCtx;
	
	public ComparisonSelectionFragment(	Context _ctx, IComparisonSelectionListener _listener, 
										DisplayMode _mode)
	{
		mCtx = _ctx;
		mListener = _listener;
		switch(_mode)
		{
		case day:
			mCb1Text = mCtx.getString(R.string.hd_comparison_day_cb1_text);
			mCb2Text = mCtx.getString(R.string.hd_comparison_day_cb2_text);
			mCb3Text = mCtx.getString(R.string.hd_comparison_day_cb3_text);
			mCb4Text = mCtx.getString(R.string.hd_comparison_day_cb4_text);
			break;
		case week:
			mCb1Text = mCtx.getString(R.string.hd_comparison_week_cb1_text);
			mCb2Text = mCtx.getString(R.string.hd_comparison_week_cb2_text);
			mCb3Text = mCtx.getString(R.string.hd_comparison_week_cb3_text);
			mCb4Text = mCtx.getString(R.string.hd_comparison_week_cb4_text);
			break;
		default:
			mCb1Text = "";
			mCb2Text = "";
			mCb3Text = "";
			mCb4Text = "";
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.hd_comparison_selection_layout, null);
		
		RadioGroup rg = (RadioGroup)v.findViewById(R.id.hd_comparison_selection_room_group);
		rg.setOnCheckedChangeListener(this);
		
		CheckBox cb1 = (CheckBox)v.findViewById(R.id.hd_comparison_selection_box1);
		cb1.setText(mCb1Text);
		cb1.setOnCheckedChangeListener(this);
		
		CheckBox cb2 = (CheckBox)v.findViewById(R.id.hd_comparison_selection_box2);
		cb2.setText(mCb2Text);
		cb2.setOnCheckedChangeListener(this);
		
		CheckBox cb3 = (CheckBox)v.findViewById(R.id.hd_comparison_selection_box3);
		cb3.setText(mCb3Text);
		cb3.setOnCheckedChangeListener(this);
		
		CheckBox cb4 = (CheckBox)v.findViewById(R.id.hd_comparison_selection_box4);
		cb4.setText(mCb4Text);
		cb4.setOnCheckedChangeListener(this);
		
		return v;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) 
	{
		switch(checkedId)
		{
		case R.id.hd_comparison_selection_room1RadioButt:
			mListener.notifyRoomSelection(mCtx.getString(R.string.global_Room1_name));
			break;
		case R.id.hd_comparison_selection_room3RadioButt:
			mListener.notifyRoomSelection(mCtx.getString(R.string.global_Room3_name));
			break;
		case R.id.hd_comparison_selection_room6RadioButt:
			mListener.notifyRoomSelection(mCtx.getString(R.string.global_Room6_name));
			break;
		}	
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		mCheckedFilters[getIndexForCheckBox(buttonView)]=isChecked;
		mListener.notifyComparisonSelection(mCheckedFilters);
	}
	
	private int getIndexForCheckBox(CompoundButton _cb)
	{
		switch(_cb.getId())
		{
		case R.id.hd_comparison_selection_box1:
			return 0;
		case R.id.hd_comparison_selection_box2:
			return 1;
		case R.id.hd_comparison_selection_box3:
			return 2;
		case R.id.hd_comparison_selection_box4:
			return 3;
		default:
			return -1;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
}
