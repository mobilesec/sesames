package at.sesame.fhooe.viewexperiments;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import at.fhooe.uef.webserver.client.helper.ColorInterpolator;
import at.fhooe.uef.webserver.client.helper.GWTColor;

public class ColorView 
extends View 
{
	private InterpolationThread mInterThread;
	private ArrayList<Integer> mColors;
	
	private int mCol;
	
	public ColorView(Context context, ArrayList<Integer> _colors) 
	{
		super(context);
		mColors = _colors;
		setColor2Draw(_colors.get(0));
		mInterThread = new InterpolationThread(this);
		mInterThread.startInterpolating();
	}

	public int getColor2Draw() {
		return mCol;
	}

	private GWTColor[] getColors(int _idx)
	{
		int first = _idx%mColors.size();
		int second = (first+1)%mColors.size();
		
		return new GWTColor[]{convertColorIntToGWTColor(mColors.get(first)), convertColorIntToGWTColor(mColors.get(second))};
	}

	public void setColor2Draw(int mCol) {
		this.mCol = mCol;
	}

	
	public void cleanUp()
	{
		if(null!=mInterThread)
		{
			mInterThread.stopInterpolating();
		}
	}

	public void init()
	{
//		mInterThread.startInterpolating();
	}


	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		canvas.drawColor(mCol);
	}
	
	private int getColor(float _fraction, int _idx)
	{
		GWTColor[] colors = getColors(_idx);
		return  ColorInterpolator.interpolate(colors[0], colors[1], _fraction).getRGB();
//		col.g
//		return Color.argb(col.getAlpha(), col.getRed(), col.getGreen(), col.getBlue());
	}
	
	
	
	
	
	private static class InterpolationThread extends Thread
	{
		private boolean mRunning = false;
		private long mTimeout = 100;
		private float mFractionStep = 0.01f;
		private ColorView mView;
		private int mColIdx = 0;
		
		public InterpolationThread(ColorView _view)
		{
			mView = _view;
		}
		
		@Override
		public void run()
		{
			float curFrac = 0;
			while(mRunning)
			{
				
				int col = mView.getColor(curFrac, mColIdx);
				mView.setColor2Draw(col);
				mView.postInvalidate();
				curFrac+=mFractionStep;
				if(curFrac>=1)
				{
					curFrac = 0;
					mColIdx++;
				}
				try {
					Thread.sleep(mTimeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void startInterpolating()
		{
//			this.stop();
			mRunning = true;
			this.start();
		}
		public void stopInterpolating()
		{
			mRunning = false;
		}
	}
	
	public static int convertGWTColorToAndroidColor(GWTColor _gwtCol)
	{
		return Color.argb(_gwtCol.getAlpha(), _gwtCol.getRed(), _gwtCol.getGreen(), _gwtCol.getBlue());
	}
	
	public static GWTColor convertColorIntToGWTColor(int _col)
	{
		return new GWTColor(Color.red(_col), Color.green(_col), Color.blue(_col), Color.alpha(_col));
	}

}
