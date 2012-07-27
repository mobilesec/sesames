package at.sesame.fhooe;

import javax.swing.JOptionPane;

import com.eteks.sweethome3d.plugin.PluginAction;

public class HelpPluginAction 
extends PluginAction 
{
	public HelpPluginAction()
	{
		putPropertyValue(Property.NAME, "Help");
        putPropertyValue(Property.MENU, "Sesame");
        setEnabled(true);
	}
	@Override
	public void execute() 
	{
		JOptionPane.showMessageDialog(null, "to add an accesspoint to the floor plan, just add any furniture to the plan, position it correctly and give it a name starting with \"AP\".");
	}

}
