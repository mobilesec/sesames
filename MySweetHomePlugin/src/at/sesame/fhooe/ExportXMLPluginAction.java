package at.sesame.fhooe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.plugin.PluginAction;

public class ExportXMLPluginAction 
extends PluginAction 
{
	/**
	 * the home containing all rooms, MPs and APs
	 */
	private Home mHome;
	
	/**
	 * the header for every XML file
	 */
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"; 
	
	/**
	 * the buffer where the xml file is written to before it is exported.
	 */
	private StringBuffer mExportBuffer = new StringBuffer(XML_HEADER);
	
	/**
	 * the naming prefix describing measurement points
	 */
	private static final String MEASUREMENT_POINT_PREFIX = "MP";
	
	/**
	 * the naming prefix describing access points
	 */
	private static final String ACCESS_POINT_PREFIX = "AP";
	
	/**
	 * here the menu entries for sweet home are defined
	 * @param _h a object oriented representation of the floor plan
	 */
	public ExportXMLPluginAction(Home _h)
	{
		mHome = _h;
		

		putPropertyValue(Property.NAME, "export XML");
        putPropertyValue(Property.MENU, "Sesame");
        setEnabled(true);
	}

	/**
	 * called when the user clicks the according menu in sweet home
	 */
	@Override
	public void execute() 
	{
		List<HomePieceOfFurniture> fpis = mHome.getFurniture();
		HomePieceOfFurniture hpof =(HomePieceOfFurniture) fpis.toArray()[0];
		String msg = "width="+mHome.getEnvironment().getPhotoWidth()+"\nheight="+mHome.getEnvironment().getPhotoHeight();
		msg+="\nHPOF_x="+hpof.getX()+", HPOF_y="+hpof.getY();
		JOptionPane.showMessageDialog(null, msg);
		mExportBuffer.append(getXmlTag("Sesame", true));
		writeRoomsToExportBuffer();
		
		writeFPIToExportBuffer();
		mExportBuffer.append(getXmlTag("Sesame", false));
		JFileChooser chooser = new JFileChooser();
	    chooser.setFileFilter(new XmlFileFilter());
	    int returnVal = chooser.showSaveDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       writeXmlFile(chooser.getSelectedFile(), mExportBuffer.toString());
	    }
	}
	
	
	/**
	 * writes all rooms to the export buffer
	 */
	private void writeRoomsToExportBuffer()
	{
		List<Room> rooms = mHome.getRooms();
		mExportBuffer.append(getXmlTag("Rooms", true));
		
		for(Room r:rooms)
		{
			writeRoomToExportBuffer(r);
		}
		
		mExportBuffer.append(getXmlTag("Rooms", false));
		mExportBuffer.append("\n\n");
	}
	
	/**
	 * writes one room to the export buffer
	 * @param _r the room to write to the buffer
	 */
	private void writeRoomToExportBuffer(Room _r)
	{
		mExportBuffer.append(getXmlTag("Room", true));
		mExportBuffer.append("\n");
		mExportBuffer.append(getXmlEntry("Name", _r.getName(), 1));
		
		
		for(int i = 0;i<_r.getPointCount();i++)
		{
			mExportBuffer.append("\t");
			mExportBuffer.append(getXmlTag("Point", true));
			mExportBuffer.append("\n");
			mExportBuffer.append(getXmlEntry("X",""+ _r.getPoints()[i][0], 2));
			mExportBuffer.append(getXmlEntry("Y",""+ _r.getPoints()[i][1], 2));
			mExportBuffer.append("\t");
			mExportBuffer.append(getXmlTag("Point", false));
			mExportBuffer.append("\n");
		}
		
		mExportBuffer.append(getXmlTag("Room", false));
		mExportBuffer.append("\n");
	}
	
	/**
	 * writes all FingerPrintItems to the export buffer
	 */
	private void writeFPIToExportBuffer()
	{
		List<HomePieceOfFurniture> fpis = mHome.getFurniture();
		if(fpis.isEmpty())
		{
			JOptionPane.showMessageDialog(null, "nothing to export");
			return;
		}
		mExportBuffer.append(getXmlTag("FingerPrintItems", true));
		mExportBuffer.append("\n");
		for(HomePieceOfFurniture hpof:fpis)
		{
			writeFurnitureToExportBuffer(hpof);
		}
		mExportBuffer.append(getXmlTag("FingerPrintItems", false));
	}
	
	/**
	 * computes the center coordinates of a HomePieceOfFurniture
	 * @param _hpof the HomePieceOfFurniture to compute the center from
	 * @return a float[] with length 2 containing center x and center y
	 */
	private float[] getFurnitureCenter(HomePieceOfFurniture _hpof)
	{
		float[][] points = _hpof.getPoints();
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
		float width = maxX-minX;
		float height = maxY-minY;
		
		res[0] = _hpof.getX()+width/2;
		res[1] = _hpof.getY()+height/2;
		return res;
	}
	
	/**
	 * creates the xml representation of one HomePieceOfFurniture and writes
	 * it to the export buffer.
	 * @param _hpof the HomePieceOfFurniture to export
	 */
	private void writeFurnitureToExportBuffer(HomePieceOfFurniture _hpof)
	{
		String type = "";
		if(_hpof.getName().startsWith(ACCESS_POINT_PREFIX))
		{
			type = ACCESS_POINT_PREFIX;
			
			 //when an access point is created by the user the room the AP is
			 //in can't be set at creation time, so it is set here
			_hpof.setDescription(getContainingRoom(_hpof));
		}
		else if(_hpof.getName().startsWith(MEASUREMENT_POINT_PREFIX))
		{
			type = MEASUREMENT_POINT_PREFIX;
		}
		else
		{
			return;
		}
		
		mExportBuffer.append(getXmlTag("FingerPrintItem", true));
		mExportBuffer.append("\n");
		mExportBuffer.append(getXmlEntry("Type", type, 1));
		
		//if the number of the furniture is smaller than 10 an additional blank
		//(" ") is inserted in the name
		String nameStr = _hpof.getName();
		String numStr = nameStr.substring(nameStr.lastIndexOf(" ")+1);
		int num = Integer.parseInt(numStr);
		String name = "";
		if(num<10)
		{
			name = "MP  "+num;
		}
		else
		{
			name = "MP "+num;
		}
		
		mExportBuffer.append(getXmlEntry("Name",name,1));
		mExportBuffer.append(getXmlEntry("Description", _hpof.getDescription(),1));
		mExportBuffer.append(getXmlEntry("X", ""+_hpof.getX(),1));
		mExportBuffer.append(getXmlEntry("Y", ""+_hpof.getY(),1));
		mExportBuffer.append(getXmlTag("FingerPrintItem", false));
		mExportBuffer.append("\n");
	}
	
	/**
	 * returns the room a passed HomePieceOfFurniture is in
	 * @param _hpof the HomePieceOfFurniture to check which room it is in
	 * @return the name of the room the HomePieceOfFurniture is in
	 */
	private String getContainingRoom(HomePieceOfFurniture _hpof) 
	{
		List<Room> rooms = mHome.getRooms();
		float[]center = getFurnitureCenter(_hpof);
		float centerX = center[0];
		float centerY = center[1];
		
		for(Room r:rooms)
		{
			if(r.containsPoint(centerX, centerY, 0))
			{
				return r.getName();
			}
		}
		return "";
	}

	/**
	 * creates a xml tag with specified tag name, value and number of tabs to 
	 * append before the tag (for human readability of the xml file)
	 * @param _tag the name of the tag
	 * @param _value the value inside the tag
	 * @param _level the number of tabs to enter
	 * @return a string containing the xml tag specified by the passed parameters
	 */
	private String getXmlEntry(String _tag, String _value, int _level)
	{
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i<_level;i++)
		{
			sb.append("\t");
		}
			
		sb.append(getXmlTag(_tag, true));
		sb.append(_value);
		sb.append(getXmlTag(_tag, false));
		sb.append("\n");
		return sb.toString();
		
	}
	
	/**
	 * creates a new XML Tag
	 * @param _tag the name of the tag
	 * @param _open boolean indicating whether it is 
	 * an opening tag or a closing tag.
	 * @return a string containing the tag
	 */
	private String getXmlTag(String _tag, boolean _open)
	{
		StringBuffer sb = new StringBuffer("<");
		if(!_open)
		{
			sb.append("/");
		}
		sb.append(_tag);
		sb.append(">");
		
		return sb.toString();
	}
	
	/**
	 * writes the passed content to the passed file
	 * @param _f the file specifying the location for the xml file
	 * @param _content the content of the xml file
	 */
	private void writeXmlFile(File _f, String _content)
	{
		if(!_f.getAbsolutePath().endsWith(".xml"))
		{
			_f = new File(_f.getAbsolutePath()+".xml");
		}
		if(!_f.exists())
		{
			_f.getParentFile().mkdirs();
			try {
				_f.createNewFile();
			} 
			catch (IOException e) 
			{
				JOptionPane.showMessageDialog(null, "could not create outputfile");
				return;
			}
			if(_f.exists())
			{
				System.out.println("file created successfully");
			}
			else
			{
				System.out.println("file creation failed");
			}
		}
		try 
		{
			PrintWriter pw = new PrintWriter(_f);
			pw.write(_content);
			pw.flush();
			pw.close();
		} 
		catch (FileNotFoundException e) 
		{
			JOptionPane.showMessageDialog(null, "could not create file outputstream");
			return;
		}
		
		JOptionPane.showMessageDialog(null, "Export successful.");
	}

}
