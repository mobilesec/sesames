package at.sesame.fhooe;

public class Util 
{
	private static final String SEPARATOR = "-";
	public static String createTimeString(int _year, int _month, int _day)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(_year));
		sb.append(SEPARATOR);
		sb.append(String.valueOf(_month));
		sb.append(SEPARATOR);
		sb.append(String.valueOf(_day));
		return sb.toString();
	}

}
