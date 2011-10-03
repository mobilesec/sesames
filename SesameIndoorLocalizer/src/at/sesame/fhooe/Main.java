package at.sesame.fhooe;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import at.sesame.fhooe.visualization.CalibrationView;

public class Main 
extends TabActivity 
{
	@Override
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.main);
		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab
        
        // Create an Intent to launch an Activity for the tab (to be reused)
//        intent = new Intent().setClass(this, SesameIndoorLocalizerActivity.class);
        intent = new Intent().setClass(this, SesameIndoorLocalizerServiceActivity.class);
//        // Initialize a TabSpec for each tab and add it to the TabHost
//    
        spec = tabHost.newTabSpec("localization").setIndicator(getString(R.string.main_localizationTabTitle),
        		res.getDrawable(R.drawable.ic_tab_artists_grey))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, CalibrationView.class);
        spec = tabHost.newTabSpec("calibration").setIndicator("Calibration", 
        		res.getDrawable(R.drawable.ic_tab_artists_grey))
        		.setContent(intent);
        tabHost.addTab(spec);
	}
}
