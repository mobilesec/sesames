package at.sesame.fhooe.ui.energy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import at.sesame.fhooe.R;

public class EnergyMeter 
extends View 
{
	private static final String TAG = "EnergyMeter";
	private Bitmap mBackground;
	private Bitmap mPointer;
	private Matrix mPointerMatrix = new Matrix();

	private PointF mPointerAnchor;
	
	private float mDx = 0;
	private float mDy = 0;
	
	
	private float mMinValue =0;
	private float mMaxValue = 100;
	
	private float mFullAngle = 90;
	
	private int mMinorTickSpacing = 5;
	private float mMinorTickLength = 20;
	
	private int mMajorTickSpacing = 10;
	private float mMajorTickLength = 30;
	
	private double mValue;
	
	private int mPointerBaseWidth = 20;

	public EnergyMeter(Context context, AttributeSet attrs, int _id) {
		super(context, attrs, _id);
		loadGraphics();
	}
	
	public EnergyMeter(Context context, AttributeSet attrs) {
		super(context, attrs);
		loadGraphics();
	}
	
	public EnergyMeter(Context context) {
		super(context);
		loadGraphics();
	}
	
	private void initMatrix()
	{
		mPointerMatrix.reset();
		mPointerMatrix.postTranslate(mDx, mDy);//move the pointer to the lower center of the background;
	}
	
	public void setValue(double _val) throws Exception
	{
		double angle = convertValueToAngle(_val);
		mValue = _val;

		mPointerMatrix.reset();
		mPointerMatrix.setRotate((float)angle, mPointerAnchor.x, mPointerAnchor.y);
		mPointerMatrix.postTranslate(mDx, mDy);

		postInvalidate();
	}
	
	private void loadGraphics()
	{
		mBackground = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.meter_background);		
		mPointer = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.meter_pointer);
			
		mDx = mBackground.getWidth()/2-mPointer.getWidth()/2;
		mDy = mBackground.getHeight()-mPointer.getHeight();

		mPointerAnchor = new PointF(mPointer.getWidth()/2, mPointer.getHeight());
	}

	@Override
	protected void onDraw(Canvas _c) 
	{
		super.onDraw(_c);
		_c.drawBitmap(mBackground,0,0, null);
		_c.drawBitmap(mPointer, mPointerMatrix, null);
		
		drawTicks(_c);
		drawLowFiPointer(_c);
	}
	
	private void drawLowFiPointer(Canvas _c) 
	{

		float width = mBackground.getWidth();
		float height = mBackground.getHeight();

		Paint p = new Paint();
		p.setColor(Color.RED);

		try 
		{
			float left = width/2-mPointerBaseWidth/2;
			float right = left+mPointerBaseWidth;
			float top = height-mPointerBaseWidth;
			float bottom = height;
			PointF[] pos = getTickPositionFromValue(mValue, 0);
			_c.drawArc(new RectF(left, top, right, bottom), 0, 360, true, p);
			_c.drawLine(width/2-mPointerBaseWidth/2, height-mPointerBaseWidth/2, pos[1].x, pos[1].y, p);
			_c.drawLine(width/2, height, pos[1].x, pos[1].y, p);
			_c.drawLine(width/2+mPointerBaseWidth/2, height-mPointerBaseWidth/2, pos[1].x, pos[1].y, p);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void drawTicks(Canvas _c)
	{
		for(float i = mMinValue;i<=mMaxValue;i+=mMinorTickSpacing)
		{
			drawMinorTick(_c, i);
			if(i%mMajorTickSpacing==0)
			{
				drawMajorTick(_c, i);
			}
		}
	}
	
	private void drawMinorTick(Canvas _c, float _val)
	{
		drawTick(_c, _val, mMinorTickLength);
	}
	
	private void drawMajorTick(Canvas _c, float _val)
	{
		drawTick(_c, _val, mMajorTickLength);
	}
	
	private void drawTick(Canvas _c, float _val, float _len)
	{
		try {
			PointF[] pos = getTickPositionFromValue(_val, _len);
//			Log.e(TAG, "pos1:"+PointToString(pos[0])+", pos2:"+PointToString(pos[1]));
			Paint p = new Paint();
			p.setColor(Color.GREEN);
			_c.drawLine(pos[0].x, pos[0].y,pos[1].x, pos[1].y, p);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String PointToString(PointF _p)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("x:");
		sb.append(_p.x);
		sb.append(", ");
		sb.append("y:");
		sb.append(_p.y);
		return sb.toString();
		
	}
	
	private Bitmap multiplyBitmapWithMatrix(Bitmap _bm, Matrix _mat)
	{
		return Bitmap.createBitmap(_bm, 0, 0, _bm.getWidth(), _bm.getHeight(), _mat, true);
	}
	
	private PointF[] getTickPositionFromValue(double _val, float _len) throws Exception
	{
//		double angle = (convertValueToAngle(_val))+90;
		double angle = (convertValueToAngle(_val))+90;
		angle*=Math.PI;
		angle/=180;
//		Log.e(TAG, "angle="+angle);
		float width = mBackground.getWidth();
		float height = mBackground.getHeight();
		float lengthTop = mPointer.getHeight();
		float lengthBottom = lengthTop-_len;
		double xTop = width/2-Math.cos(angle)*lengthTop;
		double yTop = height-Math.sin(angle)*lengthTop;
		double xBottom = width/2-Math.cos(angle)*lengthBottom;
		double yBottom = height-Math.sin(angle)*lengthBottom;
		
		return new PointF[]{new PointF((float)xBottom, (float)yBottom), new PointF((float)xTop,(float)yTop)};
	}
	
	private double convertValueToAngle(double _val) throws Exception
	{
		if(_val>mMaxValue)
		{
			throw new Exception("can not convert value greater than max ("+mMaxValue+")");
		}
		return _val/(mMaxValue-mMinValue)//normalization of value to correct range
				*mFullAngle //using the full available angle
				-(mFullAngle/2)+mMinValue; //producing correct values around the center
	}
	
}
