package at.sesame.fhooe.fingerprintInformation;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Room 
{
	/**
	 * the name of the room
	 */
	private String mName;
	
	/**
	 * the outline of the room (only exists if the number of points
	 * used for constructing the room != 4)
	 */
	private Path2D.Double mPath = null;
	
	/**
	 * the outline of the room (only exists if the number of points
	 * used for constructing the room == 4)
	 */
	private Rectangle2D.Double mRect = null;
	
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
	public Room(String _name, ArrayList<Point2D.Double> _points)
	{
		mName = _name;
		if(_points.size()==4)
		{
			mType = ShapeType.RECTANGLE;
			mRect = createRect2DFromPoints(_points);
		}
		else
		{
			mType = ShapeType.POLYGON;
			mPath = createPath2DFromPoints(_points);
		}
	}

	/**
	 * creates a Rectangle2D.Double from the passed points
	 * @param _points a double[][] containing all points of the outline
	 * @return a Rectangle2D.Double consisting of the four points
	 */
	private Rectangle2D.Double createRect2DFromPoints(ArrayList<Point2D.Double> _points) 
	{
		double[] xPoints = new double[_points.size()];
		double[] yPoints = new double[_points.size()];
		for(int i = 0;i<_points.size();i++)
		{
			xPoints[i]=_points.get(i).x;
			yPoints[i]=_points.get(i).y;
		}
		double[] minXmaxX = getMinMax(xPoints);
		double[] minYmaxY = getMinMax(yPoints);
		
		return new Rectangle2D.Double(	minXmaxX[0], 
										minYmaxY[0], 
										minXmaxX[1]-minXmaxX[0],
										minYmaxY[1]-minYmaxY[0]);
	}
	
	/**
	 * creates a Path2D.Double from the passed points
	 * @param _points a double[][] containing all points of the outline
	 * @return a Path2D.Double consisting of all the points
	 */
	private Path2D.Double createPath2DFromPoints(ArrayList<Point2D.Double> _points)
	{
		Path2D.Double p = new Path2D.Double();
		for(int i = 0;i<_points.size();i++)
		{
			Point2D.Double point = _points.get(i);
			p.lineTo(point.x, point.y);
		}
		return p;
	}
	
	/**
	 * computes the minimum and maximum of the passed double[]
	 * @param _vals the double[] to compute min and max from
	 * @return an double[] of size 2 (minimum @ index 0, maximum @ index 1)
	 */
	private double[] getMinMax(double[] _vals)
	{
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
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
		return new double[]{min,max};
	}
	
	/**
	 * checks whether a passed point is contained in this room
	 * @param _x the x coordinate of the point
	 * @param _y the y coordinate of the point
	 * @return true, if the point is contained, false otherwise
	 */
	public boolean contains(double _x, double _y)
	{
		if(mType==ShapeType.RECTANGLE)
		{
			return mRect.contains(_x, _y);
		}
		else if(mType==ShapeType.POLYGON)
		{
			return mPath.contains(_x, _y);
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
