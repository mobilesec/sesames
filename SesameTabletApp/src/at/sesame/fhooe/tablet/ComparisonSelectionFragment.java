package at.sesame.fhooe.tablet;

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

public class ComparisonSelectionFragment
extends Fragment
implements OnCheckedChangeListener, android.widget.RadioGroup.OnCheckedChangeListener
{
	@SuppressWarnings("unused")
	private static final String TAG = "ComparisonSelectionFragment";
	
	public enum DisplayMode
	{
		day,
		week
	}
	
//	private String mCb1Text;
//	private String mCb2Text;
//	private String mCb3Text;
//	private String mCb4Text;
	private CheckBox mCb1;
	private CheckBox mCb2;
	private CheckBox mCb3;
	private CheckBox mCb4;
	
	private boolean[] mCheckedFilters = new boolean[]{false, false, false, false};
	
	private IComparisonSelectionListener mListener;
	
	private DisplayMode mCurMode = DisplayMode.day;
	
	private Context mCtx;
//	
//	private View mView;
	
//	public ComparisonSelectionFragment(	Context _ctx, IComparisonSelectionListener _listener, 
//										DisplayMode _mode)
//	{
//		mCtx = _ctx;
//		mListener = _listener;
//		setDisplayMode(_mode);
//	}
	


	public void setComparisonSelectionListener(IComparisonSelectionListener _listener)
	{
		mListener = _listener;
	}
	
	public void setContext(Context _ctx)
	{
		mCtx = _ctx;
	}

	public void setDisplayMode(DisplayMode _mode) 
	{
		mCurMode = _mode;
		updateCheckBoxes();
	}
	
	private void updateCheckBoxes()
	{
		switch(mCurMode)
		{
		case day:
			mCb1.setText(getActivity().getString(R.string.hd_comparison_day_cb1_text));
			mCb2.setText(getActivity().getString(R.string.hd_comparison_day_cb2_text));
			mCb3.setText(getActivity().getString(R.string.hd_comparison_day_cb3_text));
			mCb4.setText(getActivity().getString(R.string.hd_comparison_day_cb4_text));
//			mCb1Text = getActivity().getString(R.string.hd_comparison_day_cb1_text);
//			mCb2Text = getActivity().getString(R.string.hd_comparison_day_cb2_text);
//			mCb3Text = getActivity().getString(R.string.hd_comparison_day_cb3_text);
//			mCb4Text = getActivity().getString(R.string.hd_comparison_day_cb4_text);
			break;
		case week:
			mCb1.setText(getActivity().getString(R.string.hd_comparison_week_cb1_text));
			mCb2.setText(getActivity().getString(R.string.hd_comparison_week_cb2_text));
			mCb3.setText(getActivity().getString(R.string.hd_comparison_week_cb3_text));
			mCb4.setText(getActivity().getString(R.string.hd_comparison_week_cb4_text));
//			mCb1Text = getActivity().getString(R.string.hd_comparison_week_cb1_text);
//			mCb2Text = getActivity().getString(R.string.hd_comparison_week_cb2_text);
//			mCb3Text = getActivity().getString(R.string.hd_comparison_week_cb3_text);
//			mCb4Text = getActivity().getString(R.string.hd_comparison_week_cb4_text);
			break;
		default:
			mCb1.setText("impossible1");
			mCb2.setText("impossible2");
			mCb3.setText("impossible3");
			mCb4.setText("impossible4");
		}
//		initializeView(null);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		
//		LayoutInflater inflater = null!=_li?_li:LayoutInflater.from(mCtx);
		View v = inflater.inflate(R.layout.hd_comparison_selection_layout, null);
		
		RadioGroup rg = (RadioGroup)v.findViewById(R.id.hd_comparison_selection_room_group);
		rg.setOnCheckedChangeListener(this);
		
		mCb1 = (CheckBox)v.findViewById(R.id.hd_comparison_selection_box1);
//		cb1.setText(mCb1Text);
		mCb1.setOnCheckedChangeListener(this);
		
		mCb2 = (CheckBox)v.findViewById(R.id.hd_comparison_selection_box2);
//		cb2.setText(mCb2Text);
		mCb2.setOnCheckedChangeListener(this);
		
		mCb3 = (CheckBox)v.findViewById(R.id.hd_comparison_selection_box3);
//		cb3.setText(mCb3Text);
		mCb3.setOnCheckedChangeListener(this);
		
		mCb4 = (CheckBox)v.findViewById(R.id.hd_comparison_selection_box4);
//		cb4.setText(mCb4Text);
		mCb4.setOnCheckedChangeListener(this);
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

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		return initializeView(inflater);
//	}	
	
	
	
//	public View getComparisonSelectionView()
//	{
//		return mView;
//	}
}
