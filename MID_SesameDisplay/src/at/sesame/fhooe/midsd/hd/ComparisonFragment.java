package at.sesame.fhooe.midsd.hd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import at.sesame.fhooe.midsd.R;

public class ComparisonFragment 
extends Fragment
implements IComparisonSelectionListener, OnCheckedChangeListener
{
	private FrameLayout mChartFrame;
	
	private static final String DAY_CB1_TEXT = " vor 1 Woche";
	private static final String DAY_CB2_TEXT = " vor 2 Wochen";
	private static final String DAY_CB3_TEXT = " vor 3 Wochen";
	private static final String DAY_CB4_TEXT = " vor 4 Wochen";
	
	private static final String WEEK_CB1_TEXT = "1 Woche zuvor";
	private static final String WEEK_CB2_TEXT = "2 Wochen zuvor";
	private static final String WEEK_CB3_TEXT = "3 Wochen zuvor";
	private static final String WEEK_CB4_TEXT = "4 Wochen zuvor";
	
	private RadioGroup mDayWeekGroup;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.hd_comparison_layout, null);
		mChartFrame = (FrameLayout)v.findViewById(R.id.hd_comparison_layout_chartFrame);
		mDayWeekGroup = (RadioGroup)v.findViewById(R.id.hd_comparison_layout_day_week_group);
		mDayWeekGroup.setOnCheckedChangeListener(this);
		addCorrectComparisonSelectionFragment();
		return v;
	}
	
	private void addCorrectComparisonSelectionFragment()
	{
		ComparisonSelectionFragment csf = null;
		switch(mDayWeekGroup.getCheckedRadioButtonId())
		{
		case R.id.hd_comparison_layout_dayRadioButt:
			csf = createDayCSF();
			break;
		case R.id.hd_comparison_layout_weekRadioButt:
			csf = createWeekCSF();
			break;
		}
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.hd_comparison_layout_comparisonFrame, csf);
		
		ft.commit();
	}
	
	private ComparisonSelectionFragment createDayCSF()
	{
		return new ComparisonSelectionFragment(this, DAY_CB1_TEXT, DAY_CB2_TEXT, DAY_CB3_TEXT, DAY_CB4_TEXT);
	}
	
	private ComparisonSelectionFragment createWeekCSF()
	{
		return new ComparisonSelectionFragment(this, WEEK_CB1_TEXT, WEEK_CB2_TEXT, WEEK_CB3_TEXT, WEEK_CB4_TEXT);
	}

	@Override
	public void notifyRoomSelection(ComparisonRoom _room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyComparisonSelection(boolean[] _selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		addCorrectComparisonSelectionFragment();
		
	}
}
