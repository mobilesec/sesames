package at.sesame.fhooe.energydisplay;

import android.os.Bundle;
import android.util.Log;
import at.sesame.fhooe.energydisplay.ezan.ui.EzanFragment;
import at.sesame.fhooe.lib.ui.EnergyMeterFragment;
import at.sesame.fhooe.lib.ui.TabbedFragmentActivity;

public class SesameEnergyDisplay 
extends TabbedFragmentActivity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	setTheme(android.R.style.Theme_Holo_Light);
    	super.onCreate(savedInstanceState);
        
        try {
			addTab("energy meter", new EnergyMeterFragment(this));
			addTab("ezan", new EzanFragment());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}