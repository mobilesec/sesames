package at.sesame.fhooe.localizationservice.xml.lm.model;

public class RelativePosition 
{
	private double mX;
	private double mY;
	
	public double getX() {
		return mX;
	}
	public void setX(double _x) {
		this.mX = _x;
	}
	public double getY() {
		return mY;
	}
	public void setY(double _y) {
		this.mY = _y;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RelativePosition [mX=");
		builder.append(mX);
		builder.append(", mY=");
		builder.append(mY);
		builder.append("]");
		return builder.toString();
	}
	
	

}
