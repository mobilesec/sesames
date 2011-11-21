package at.sesame.fhooe.localizationservice.xml.lm.model;

public class Anchor 
{
	private RelativePosition mRelPos;
	private AbsolutePosition mAbsPos;
	
	public RelativePosition getRelativePosition() {
		return mRelPos;
	}
	public void setRelativePosition(RelativePosition _relPos) {
		this.mRelPos = _relPos;
	}
	public AbsolutePosition getAbsolutePosition() {
		return mAbsPos;
	}
	public void setAbsolutePosition(AbsolutePosition _absPos) {
		this.mAbsPos = _absPos;
	}
	
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Anchor [mRelPos=");
		builder.append(mRelPos);
		builder.append(", mAbsPos=");
		builder.append(mAbsPos);
		builder.append("]");
		return builder.toString();
	}
}
