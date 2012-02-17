package at.sesame.fhooe.midsd.ld;

import java.util.ArrayList;

import android.graphics.Color;
import at.fhooe.uef.webserver.client.helper.ColorInterpolator;
import at.fhooe.uef.webserver.client.helper.GWTColor;

public class ColorInterpolationManager 
{
	private ArrayList<Integer> mColors;
	private float mFractionStep = 0.1f;
	private float mFraction = 0.0f;
	private int mColIdx = 0;
	
	public ColorInterpolationManager(ArrayList<Integer> _colors)
	{
		mColors = _colors;
	}
	
	public ColorInterpolationManager(ArrayList<Integer> _colors, float _fractionStep)
	{
		mColors = _colors;
		mFractionStep = _fractionStep;
	}
	
	public int getNextColor()
	{
		int res = getColor(mFraction, mColIdx);
		
		if(incrementFraction())
		{
			mColIdx++;
		}
		return res;
		
	}
	
	/**
	 * increments the current fraction by mFractionStep. if the result is greater
	 * than 1 the fraction is set to 0
	 * @return false if no overflow happened, true otherwise
	 */
	private boolean incrementFraction()
	{
		mFraction+=mFractionStep;
		if(mFraction>=1)
		{
			mFraction=0;
			return true;
		}
		return false;
	}
	
	private int getColor(float _fraction, int _idx)
	{
		GWTColor[] colors = getColors(_idx);
		return  ColorInterpolator.interpolate(colors[0], colors[1], _fraction).getRGB();
	}
	
	private GWTColor[] getColors(int _idx)
	{
		int first = _idx%mColors.size();
		int second = (first+1)%mColors.size();
		
		return new GWTColor[]{convertColorIntToGWTColor(mColors.get(first)), convertColorIntToGWTColor(mColors.get(second))};
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
