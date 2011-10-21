package at.sesame.fhooe.lib.fingerprintInformation;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

public class Room 
implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1811273500447655266L;

	/**
	 * the name of the room
	 */
	private String mName;
	
	/**
	 * the outline of the room (only exists if the number of points
	 * used for constructing the room != 4)
	 */
	@SuppressWarnings("unused")
	private Path mPath = null;
	
	/**
	 * the outline of the room (only exists if the number of points
	 * used for constructing the room == 4)
	 */
	private RectF mRect = null;
	
	/**
	 * enumeration of possible shape types
	 */
	private enum ShapeType
	{
		RECTANGLE,
		POLYGON
	}
	
	/**
	 * the actual shape type of this room
	 */
	private ShapeType mType;
	
	/**
	 * creates a new room
	 * @param _name the name of the room
	 * @param _points points describing the outline of the room
	 */
	public Room(String _name, ArrayList<PointF> _points)
	{
		mName = _name;
		if(_points.size()==4)
		{
			mType = ShapeType.RECTANGLE;
			mRect = createRectFFromPoints(_points);
		}
		else
		{
			mType = ShapeType.POLYGON;
			mPath = createPathFromPoints(_points);
		}
	}
	
	/**
	 * creates a new room without any position information
	 * @param _name the name of the room
	 */
	public Room(String _name)
	{
		this(_name, new ArrayList<PointF>());
	}

	/**
	 * creates a Rectangle2D.Double from the passed points
	 * @param _points a double[][] containing all points of the outline
	 * @return a Rectangle2D.Double consisting of the four points
	 */
	private RectF createRectFFromPoints(ArrayList<PointF> _points) 
	{
		float[] xPoints = new float[_points.size()];
		float[] yPoints = new float[_points.size()];
		for(int i = 0;i<_points.size();i++)
		{
			xPoints[i]=_points.get(i).x;
			yPoints[i]=_points.get(i).y;
		}
		float[] minXmaxX = getMinMax(xPoints);
		float[] minYmaxY = getMinMax(yPoints);
		
		return new RectF(	minXmaxX[0], 
										minYmaxY[0], 
										minXmaxX[1]-minXmaxX[0],
										minYmaxY[1]-minYmaxY[0]);
	}
	
	/**
	 * creates a Path2D.Double from the passed points
	 * @param _points a double[][] containing all points of the outline
	 * @return a Path2D.Double consisting of all the points
	 */
	private Path createPathFromPoints(ArrayList<PointF> _points)
	{
		Path p = new Path();
		for(int i = 0;i<_points.size();i++)
		{
			PointF point = _points.get(i);
			p.lineTo(point.x, point.y);
		}
		return p;
	}
	
	/**
	 * computes the minimum and maximum of the passed double[]
	 * @param _vals the double[] to compute min and max from
	 * @return an double[] of size 2 (minimum @ index 0, maximum @ index 1)
	 */
	private float[] getMinMax(float[] _vals)
	{
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		
		for(int i = 0;i<_vals.length;i++)
		{
			if(_vals[i]<min)
			{
				min = _vals[i];
			}
			if(_vals[i]>max)
			{
				max = _vals[i];
			}
		}
		return new float[]{min,max};
	}
	
	/**
	 * checks whether a passed point is contained in this room
	 * @param _x the x coordinate of the point
	 * @param _y the y coordinate of the point
	 * @return true, if the point is contained, false otherwise
	 */
	public boolean contains(float _x, float _y)
	{
		if(mType==ShapeType.RECTANGLE)
		{
			return mRect.contains(_x, _y);
		}
		else if(mType==ShapeType.POLYGON)
		{
			//TODO implement
//			return mPath.contains(_x, _y);
		}
		return false;
	}
	
	/**
	 * returns the name of the room
	 * @return the name of the room
	 */
	public String getName() 
	{
		return mName;
	}
}
