package at.sesame.fhooe.ezan.ui.renderer;

import android.graphics.Color;

public class EzanRendererInfoProvider 
{
	private static final String PLACE_NAME_1 = "E010";
	private static final String PLACE_NAME_2 = "E012";
	private static final String PLACE_NAME_3 = "E014";
	private static final String PLACE_NAME_4 = "E015";
	
	public static int getHumiditySeriesColor(String _title)
	{
		int res = Color.WHITE;
		
		if(PLACE_NAME_1.equalsIgnoreCase(_title))
		{
			res = Color.MAGENTA;
		}
		else if(PLACE_NAME_2.equalsIgnoreCase(_title))
		{
			res = Color.BLUE;
		}
		else if(PLACE_NAME_3.equalsIgnoreCase(_title))
		{
			res = Color.GREEN;
		}
		else if(PLACE_NAME_4.equalsIgnoreCase(_title))
		{
			res = Color.RED;
		}
		return res;
	}
	
	public static int getTemperatureSeriesColor(String _title)
	{
		int res = Color.WHITE;
		
		if(PLACE_NAME_1.equalsIgnoreCase(_title))
		{
			res = Color.CYAN;
		}
		else if(PLACE_NAME_2.equalsIgnoreCase(_title))
		{
			res = Color.DKGRAY;
		}
		else if(PLACE_NAME_3.equalsIgnoreCase(_title))
		{
			res = Color.YELLOW;
		}
		else if(PLACE_NAME_4.equalsIgnoreCase(_title))
		{
			res = Color.BLUE;
		}
		return res;
	}
	
	public static int getGeneralSeriesColor(String _title)
	{
		int res = Color.WHITE;
		
		if(PLACE_NAME_1.equalsIgnoreCase(_title))
		{
			res = Color.MAGENTA;
		}
		else if(PLACE_NAME_2.equalsIgnoreCase(_title))
		{
			res = Color.BLUE;
		}
		else if(PLACE_NAME_3.equalsIgnoreCase(_title))
		{
			res = Color.GREEN;
		}
		else if(PLACE_NAME_4.equalsIgnoreCase(_title))
		{
			res = Color.RED;
		}
		return res;
	}
	
	public static int getLightSeriesColor(String _title)
	{
		int res = Color.WHITE;
		
		if(PLACE_NAME_1.equalsIgnoreCase(_title))
		{
			res = Color.MAGENTA;
		}
		else if(PLACE_NAME_2.equalsIgnoreCase(_title))
		{
			res = Color.BLUE;
		}
		else if(PLACE_NAME_3.equalsIgnoreCase(_title))
		{
			res = Color.GREEN;
		}
		else if(PLACE_NAME_4.equalsIgnoreCase(_title))
		{
			res = Color.RED;
		}
		return res;
	}
	
	public static int getVoltageSeriesColor(String _title)
	{
		int res = Color.WHITE;
		
		if(PLACE_NAME_1.equalsIgnoreCase(_title))
		{
			res = Color.MAGENTA;
		}
		else if(PLACE_NAME_2.equalsIgnoreCase(_title))
		{
			res = Color.BLUE;
		}
		else if(PLACE_NAME_3.equalsIgnoreCase(_title))
		{
			res = Color.GREEN;
		}
		else if(PLACE_NAME_4.equalsIgnoreCase(_title))
		{
			res = Color.RED;
		}
		return res;
	}

}
