package at.fhooe.mc.graphical;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import at.fhooe.mc.extern.fingerprintInformation.MeasurementPoint;

public class MpSelectionView 
extends View 
{
	private static final String TAG = "MpSelectionView";
	
	private Bitmap mPlan = null;
	
	private ArrayList<MeasurementPoint> mMps = new ArrayList<MeasurementPoint>();
	
	private float mXoff = 0;
	private float mYoff = 0;
	
	private float mTouchStartX = 0;
	private float mTouchStartY = 0;
	
	private float mSelectorSize = 20;
	
	private float mMpSize = 10;
	
	private ISelectionListener mSelectionListener;

	public MpSelectionView(Context context)
	{
		super(context);
	}
	
	

	public MpSelectionView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
	}



	public MpSelectionView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		drawPlan(canvas);
		drawMps(canvas);
		drawSelector(canvas);
	}
	
	private void drawPlan(Canvas _c) 
	{
		if(null!=mPlan)
		{	
			_c.drawBitmap(mPlan, mXoff, mYoff, null);	
		}	
	}
	
	private void drawSelector(Canvas _c)
	{
		Paint selectorPaint = new Paint();
		selectorPaint.setStrokeWidth(5.0f);
		selectorPaint.setStyle(Paint.Style.STROKE);

		_c.drawCircle(getWidth()/2, getHeight()/2, mSelectorSize, selectorPaint);
		_c.drawLine(getWidth()/2-mSelectorSize, getHeight()/2-mSelectorSize, getWidth()/2+mSelectorSize, getHeight()/2+mSelectorSize, selectorPaint);
		_c.drawLine(getWidth()/2-mSelectorSize, getHeight()/2+mSelectorSize, getWidth()/2+mSelectorSize, getHeight()/2-mSelectorSize, selectorPaint);
	}
	
	private void drawMps(Canvas _c)
	{
		Paint mpCirclePaint = new Paint();
		mpCirclePaint.setStrokeWidth(3);
		mpCirclePaint.setColor(Color.GREEN);
		mpCirclePaint.setStyle(Paint.Style.STROKE);
		
		Paint mpTextPaint = new Paint();
		mpTextPaint.setStrokeWidth(3);
		mpTextPaint.setTextSize(30);
		mpTextPaint.setColor(Color.BLACK);
//		mpTextPaint.setStyle(Paint.Style.STROKE);
		for(MeasurementPoint mp:mMps)
		{
			
			_c.drawCircle(getMpDrawingX(mp), getMpDrawingY(mp), mMpSize, mpCirclePaint);
//			_c.drawText(mp.getName(), getMpDrawingX(mp), getMpDrawingY(mp), p);
			String txt = ""+mp.getFingerPrints().size();
			Log.e(TAG, mp.getName()+": "+txt);
			Rect bounds  = new Rect();
			mpTextPaint.getTextBounds(txt, 0, txt.length(), bounds);
			_c.drawText(txt, getMpDrawingX(mp)-bounds.width()/2, getMpDrawingY(mp), mpTextPaint);
		}
	}
	
	private float getMpDrawingX(MeasurementPoint _mp)
	{
		return (float)_mp.getX()/7+mXoff;
	}
	
	private float getMpDrawingY(MeasurementPoint _mp)
	{
		return (float)_mp.getY()/7.2f+mYoff;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			mTouchStartX = event.getX()-mXoff;
			mTouchStartY = event.getY()-mYoff;
			break;
		case MotionEvent.ACTION_MOVE:
			mXoff = event.getX()-mTouchStartX;
			mYoff = event.getY()-mTouchStartY;
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			snapToNextMp();
			if(null!=mSelectionListener)
			{
				mSelectionListener.notifyMpSelected(getClosestMp());
			}
			break;
		}
		return true;
	}

	private void snapToNextMp()
	{
		MeasurementPoint mp = getClosestMp();
		PointF center = new PointF(getWidth()/2, getHeight()/2);
		mXoff += center.x-getMpDrawingX(mp);
		mYoff += center.y-getMpDrawingY(mp);
		invalidate();
	}
	
	private MeasurementPoint getClosestMp()
	{
		float minDist = Float.MAX_VALUE;
		MeasurementPoint closestMp = null;
		
		for(MeasurementPoint mp:mMps)
		{
			float curDist = getDistanceToCenter(mp);
			if(curDist<minDist)
			{
				minDist = curDist;
				closestMp = mp;
			}
		}
		return closestMp;
	}
	
	private float getDistanceToCenter(MeasurementPoint _mp)
	{
		PointF center = new PointF(getWidth()/2, getHeight()/2);
		PointF mpPos = new PointF(getMpDrawingX(_mp), getMpDrawingY(_mp));
		return (float)Math.sqrt(Math.pow(center.x-mpPos.x,2)+Math.pow(center.y-mpPos.y,2));
	}

	public void setPlan(Bitmap _plan, ArrayList<MeasurementPoint> _mps)
	{
		mPlan = _plan;
		mMps = _mps;
		invalidate();
	}

	public void resetOffsets()
	{
		mXoff = 0;
		mYoff = 0;
		invalidate();
	}
	
//	public MeasurementPoint getSelectedMp()
//	{
//		return getClosestMp();
//	}
	
	public void setSelectionListener(ISelectionListener _listener)
	{
		mSelectionListener = _listener;
	}
}
