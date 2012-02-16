package at.sesame.fhooe.midsd.md;

import java.util.ArrayList;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import at.sesame.fhooe.midsd.R;

public class EnergyWheelView extends View 
{
	private static final String TAG = "EnergyWheelView";
	private static final String[]mDigits = new String[]{"0","1","2","3","4","5","6","7","8","9"};
	
	private Context mCtx;

	private int mNumDigits;

	private ArrayWheelAdapter<String> mAdapter;

	private View mView;

	private LinearLayout mLinLayout;

	private ArrayList<WheelView> mWheels;

	private double mValue;


	
	public EnergyWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.e(TAG, "3 argument constructor");
		init(context);
	}

	public EnergyWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.e(TAG, "2 argument constructor");
		init(context);
	}

	public EnergyWheelView(Context context) 
	{
		super(context);
		Log.e(TAG, "1 argument constructor");
		init(context);
	}
	
	public void init(Context _ctx)
	{
		Log.e(TAG, "init");
		mCtx = _ctx;
		mNumDigits = 5;
		mAdapter =new ArrayWheelAdapter<String>(mCtx, mDigits);
		mAdapter.setTextSize(200);
		LayoutInflater li = (LayoutInflater)mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = li.inflate(R.layout.wheel_layout, null);
		//		mSeparatorView = li.inflate(R.layout.separator_layout, null);

		mLinLayout = (LinearLayout) mView.findViewById(R.id.wheel_lin_layout);


		setupWheels();
		forceLayout();
	}
	
	private void setupWheels()
	{
		mWheels = new ArrayList<WheelView>();
		for(int i = 0;i<mNumDigits;i++)
		{
			addDigit();
		}
	}

	private void addDigit()
	{
		WheelView wv = new WheelView(mCtx);

		wv.setViewAdapter(mAdapter);
		wv.setCurrentItem(0);
		wv.setVisibleItems(1);
		wv.setCyclic(true);
		wv.setInterpolator(new BounceInterpolator());
		//		wv.invalidateWheel(true);
		mWheels.add(wv);
	}
	
	public synchronized void setValue(double _value)
	{
		mValue = _value;
		displayValue();
	}
	
	private void displayValue()
	{
		int[] digits = getDigits(mValue);

		if(digits.length>mNumDigits)
		{	
			return;
		}

		for(int i = 0;i<digits.length;i++)
		{
			WheelView wv = mWheels.get(mWheels.size()-i-1);
			wv.setCurrentItem(digits[i], true);
		}

	}

	private int[] getDigits(double _val)
	{
		int n = (int) Math.floor(Math.log10(_val) + 1);
		int i;
		int[] res = new int[n];
		for ( i = 0; i < n; ++i, _val /= 10 )
		{
			res[i] =(int) _val % 10;
		}
		return res;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onDraw");
//		super.onDraw(canvas);
//		mView.forceLayout();
		mView.draw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onMeasure");
		WheelView wv = mWheels.get(0);
		wv.forceLayout();
		wv.measure(widthMeasureSpec, heightMeasureSpec);
		int width = mWheels.get(0).getWidth()*mNumDigits;
		int height = mWheels.get(0).getHeight();
		
//		mWheels.get(0).m
		Log.e(TAG, "width="+width+", height="+height);
		setMeasuredDimension(200, 100);
	}
	
	
}
