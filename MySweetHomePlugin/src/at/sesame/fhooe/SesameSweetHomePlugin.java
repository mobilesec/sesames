package at.sesame.fhooe;

import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.plugin.Plugin;
import com.eteks.sweethome3d.plugin.PluginAction;

public class SesameSweetHomePlugin extends Plugin 
{
	@Override
	public PluginAction[] getActions() 
	{
		return new PluginAction [] {new LayoutMPsPluginAction(getHome()), 
									new ExportXMLPluginAction(getHome()),
									new HelpPluginAction()}; 
	}

	public static String getDescriptionFromFurniture(HomePieceOfFurniture hpof) {
		StringBuffer sb = new StringBuffer();
		sb.append("Description:");
		sb.append(hpof.getDescription());
		sb.append("\n");
		sb.append("Name:");
		sb.append(hpof.getName());
		sb.append("\n");
		sb.append("X:");
		sb.append(hpof.getX());
		sb.append("\n");
		sb.append("Y:");
		sb.append(hpof.getY());
		sb.append("\n");
		return sb.toString();
	}

}
