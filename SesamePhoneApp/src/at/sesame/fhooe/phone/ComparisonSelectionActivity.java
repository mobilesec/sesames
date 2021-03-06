package at.sesame.fhooe.phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import at.sesame.fhooe.lib2.pms.IComparisonSelectionListener;

public class ComparisonSelectionActivity
extends Activity
implements OnCheckedChangeListener, android.widget.RadioGroup.OnCheckedChangeListener
{
	@SuppressWarnings("unused")
	private static final String TAG = "ComparisonSelectionFragment";
	
	public static final String BUNDLE_ROOM_NAME_KEY = "at.sesame.fhooe.roomname";
	public static final String BUNDLE_TIMES_KEY = "at.sesame.fhooe.times";
	public static final String BUNDLE_MODE_KEY = "at.sesame.fhooe.mode";
	
	private ImageButton mBackButt;
	
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
	
	private String mSelectedRoomName = "";

	private RadioGroup mRg;
	
//	private Context mCtx;
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
	
//	public void setContext(Context _ctx)
//	{
//		mCtx = _ctx;
//	}

	public void setDisplayMode(DisplayMode _mode) 
	{
		mCurMode = _mode;
		updateCheckBoxes();
	}
	
	
	
	@Override
	public void onBackPressed() 
	{
		createResult();
		super.onBackPressed();
	}

	private void createResult() {
		Intent data = new Intent();
		data.putExtra(BUNDLE_TIMES_KEY, mCheckedFilters);
		data.putExtra(BUNDLE_ROOM_NAME_KEY, mSelectedRoomName);
		setResult(Activity.RESULT_OK, data);
	}

	private void updateCheckBoxes()
	{
		switch(mCurMode)
		{
		case day:
			mCb1.setText(getString(R.string.hd_comparison_day_cb1_text));
			mCb2.setText(getString(R.string.hd_comparison_day_cb2_text));
			mCb3.setText(getString(R.string.hd_comparison_day_cb3_text));
			mCb4.setText(getString(R.string.hd_comparison_day_cb4_text));
//			mCb1Text = getActivity().getString(R.string.hd_comparison_day_cb1_text);
//			mCb2Text = getActivity().getString(R.string.hd_comparison_day_cb2_text);
//			mCb3Text = getActivity().getString(R.string.hd_comparison_day_cb3_text);
//			mCb4Text = getActivity().getString(R.string.hd_comparison_day_cb4_text);
			break;
		case week:
			mCb1.setText(getString(R.string.hd_comparison_week_cb1_text));
			mCb2.setText(getString(R.string.hd_comparison_week_cb2_text));
			mCb3.setText(getString(R.string.hd_comparison_week_cb3_text));
			mCb4.setText(getString(R.string.hd_comparison_week_cb4_text));
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
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
//		LayoutInflater inflater = null!=_li?_li:LayoutInflater.from(mCtx);
		setContentView(R.layout.hd_comparison_selection_layout);
		
		mRg = (RadioGroup)findViewById(R.id.hd_comparison_selection_room_group);
		mRg.setOnCheckedChangeListener(this);
		
		mCb1 = (CheckBox)findViewById(R.id.hd_comparison_selection_box1);
//		cb1.setText(R.string.gl);
		mCb1.setOnCheckedChangeListener(this);
		
		mCb2 = (CheckBox)findViewById(R.id.hd_comparison_selection_box2);
//		cb2.setText(mCb2Text);
		mCb2.setOnCheckedChangeListener(this);
		
		mCb3 = (CheckBox)findViewById(R.id.hd_comparison_selection_box3);
//		cb3.setText(mCb3Text);
		mCb3.setOnCheckedChangeListener(this);
		
		mCb4 = (CheckBox)findViewById(R.id.hd_comparison_selection_box4);
//		cb4.setText(mCb4Text);
		
		mCb4.setOnCheckedChangeListener(this);
		
		mBackButt = (ImageButton)findViewById(R.id.comparisonSelectionBackButton);
		mBackButt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createResult();
				finish();
			}
		});
		
		Bundle extras = getIntent().getExtras();
		
		if(null!=extras)
		{
			String modeName = extras.getString(BUNDLE_MODE_KEY);
			
			mSelectedRoomName = extras.getString(BUNDLE_ROOM_NAME_KEY);
			Log.e(TAG, "roomName="+mSelectedRoomName+" ("+getRadioButtonIndexForRoomName()+")");
			mRg.check(getRadioButtonIndexForRoomName());
			
			mCheckedFilters = extras.getBooleanArray(BUNDLE_TIMES_KEY);
			
			mCb1.setChecked(mCheckedFilters[0]);
			mCb2.setChecked(mCheckedFilters[1]);
			mCb3.setChecked(mCheckedFilters[2]);
			mCb4.setChecked(mCheckedFilters[3]);
			
			setDisplayMode(DisplayMode.valueOf(modeName));
		}
	}
	
	private int getRadioButtonIndexForRoomName()
	{
		if(mSelectedRoomName.equals(getString(R.string.global_Room1_name)))
		{
			return mRg.getChildAt(0).getId();
		}
		else if(mSelectedRoomName.equals(getString(R.string.global_Room3_name)))
		{
			return mRg.getChildAt(1).getId();
		}
		else if(mSelectedRoomName.equals(getString(R.string.global_Room6_name)))
		{
			return mRg.getChildAt(2).getId();
		}
		else
		{
			return -1;
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putSerializable(BUNDLE_MODE_KEY, mCurMode.toString());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) 
	{
		switch(checkedId)
		{
		case R.id.hd_comparison_selection_room1RadioButt:
			mSelectedRoomName = getString(R.string.global_Room1_name);
			
			break;
		case R.id.hd_comparison_selection_room3RadioButt:
			mSelectedRoomName = getString(R.string.global_Room3_name);
			break;
		case R.id.hd_comparison_selection_room6RadioButt:
			mSelectedRoomName = getString(R.string.global_Room6_name);
			break;
		}	
		if(null!=mListener)
		{
			mListener.notifyRoomSelection(mSelectedRoomName);			
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		mCheckedFilters[getIndexForCheckBox(buttonView)]=isChecked;
		if(null!=mListener)
		{
			mListener.notifyComparisonSelection(mCheckedFilters);			
		}
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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
