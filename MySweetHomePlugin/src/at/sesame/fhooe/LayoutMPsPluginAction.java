package at.sesame.fhooe;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.eteks.sweethome3d.io.DefaultFurnitureCatalog;
import com.eteks.sweethome3d.model.CatalogPieceOfFurniture;
import com.eteks.sweethome3d.model.FurnitureCategory;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.plugin.PluginAction;

public class LayoutMPsPluginAction extends PluginAction 
{
	private double mGridSize = 250;
	private int mPointIdx = 0;
	private Home mHome;
	
	public LayoutMPsPluginAction(Home _h)
	{
		mHome = _h;
		putPropertyValue(Property.NAME, "layout MPs");
        putPropertyValue(Property.MENU, "Sesame");
           // Enables the action by default
        setEnabled(true);
	}
	@Override
	public void execute() 
	{
		mPointIdx = 0;
		String res = "";
		double gridSize = 0.0;
		while(res.equals(""))
		{
			res = JOptionPane.showInputDialog("Please enter GridSize in cm");
			try
			{
				gridSize = Double.parseDouble(res);
			}
			catch(Exception _e)
			{
				res = "";
			}
		}
		mGridSize = gridSize;

		List<Room> rooms = mHome.getRooms();
		if(rooms.isEmpty())
		{
			JOptionPane.showMessageDialog(null, "No rooms defined. Please define at least one room.");
			return;
		}

		System.out.println("number of rooms to process:"+rooms.size());
		
		for(Room r:rooms)
		{
			System.out.println("processing room:"+r.getName());
			ArrayList<HomePieceOfFurniture> zylinders = getMeasurementPointsInRoom(r);
			for(HomePieceOfFurniture hpof:zylinders)
			{
				mHome.addPieceOfFurniture(hpof);
			}
		}

	}
	
	private ArrayList<HomePieceOfFurniture> getMeasurementPointsInRoom(Room _r)
	{
		ArrayList<HomePieceOfFurniture> res = new ArrayList<HomePieceOfFurniture>();
		float[] size = getRoomSize(_r);
		float width = size[0];
		float height = size[1];
//		int noPoints = (int) ((Math.floor(width/GRID_SIZE)-2)*(Math.floor(height/GRID_SIZE))-2);
		System.out.println("dimension of room \""+_r.getName()+"\":"+width+", "+height);//+" ("+noPoints+" points)");
		float x0 = _r.getXCenter()-width/2;
		float y0 = _r.getYCenter()-height/2;
		
		HomePieceOfFurniture zylinder = getFurnitureByName("Zylinder");
		zylinder.setWidth(1);
		zylinder.setHeight(1);
		zylinder.setDepth(1);
		zylinder.setNameVisible(true);
		
		HomePieceOfFurniture currentZylinder;
		
		
		for(double i = mGridSize;i<height;i+=mGridSize)
		{
			for(double j = mGridSize; j<width;j+=mGridSize)
			{
				currentZylinder = zylinder.clone();
				currentZylinder.setX(x0 + ((int)j));
				currentZylinder.setY(y0 + ((int)i));
				currentZylinder.setName("MP "+mPointIdx);
				currentZylinder.setDescription(_r.getName());
				res.add(currentZylinder);
				mPointIdx++;
			}
		}
		System.out.println("done adding zylinders to collection");
		return res;
	}
	
	private float[] getRoomSize(Room _r)
	{
		System.out.println("calculating roomsize for room:"+_r.getName());
		float[][] points = _r.getPoints();
		float[] res = new float[2];
		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		
		float minY = Float.MAX_VALUE;
		float maxY = Float.MIN_NORMAL;

		for(int i = 0;i<points.length;i++)
		{
			float x = points[i][0];
			float y = points[i][1];
			if(x<minX)
			{
				minX = x;
			}
			if(x>maxX)
			{
				maxX = x;
			}
			if(y<minY)
			{
				minY = y;
			}
			if(y>maxY)
			{
				maxY = y;
			}
		}
		res[0] = maxX-minX;
		res[1] = maxY-minY;
		return res;
	}
	

	private HomePieceOfFurniture getFurnitureByName(String _name)
	{
		DefaultFurnitureCatalog dfc = new DefaultFurnitureCatalog(/*new File("C:\\Program Files\\Sweet Home 3D\\lib")*/);
		for(FurnitureCategory fc:dfc.getCategories())
		{
			for(CatalogPieceOfFurniture cpof:fc.getFurniture())
			{
				if(cpof.getName().equals(_name))
				{
					return new HomePieceOfFurniture(cpof);
				}
			}
		}
		return null;
	}
}

