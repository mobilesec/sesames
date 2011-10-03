/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

import at.sesame.fhooe.R;
import at.sesame.fhooe.calendar.CalendarView;
import at.sesame.fhooe.charts.ChartView;
import at.sesame.fhooe.location.LocationView;
import at.sesame.fhooe.misc.MiscView;
import at.sesame.fhooe.vpn.VpnView;
import at.sesame.fhooe.wifi.WifiView;
import at.sesame.fhooe.wifi.recorder.WifiRecorderView;

/**
 * this class is the TabHost for all activities the application consists of
 * @author Peter Riedl
 *
 */
public class Main 
extends TabActivity
{	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, CalendarView.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("calendar").setIndicator(getString(R.string.main_calendarTabTitle),
                          res.getDrawable(R.drawable.ic_tab_artists_grey))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, LocationView.class);
        spec = tabHost.newTabSpec("location").setIndicator(getString(R.string.main_locationTabTitle),
                          res.getDrawable(R.drawable.ic_tab_artists_grey))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, WifiView.class);
        spec = tabHost.newTabSpec("wifi").setIndicator(getString(R.string.main_wifiTabTitle),
                          res.getDrawable(R.drawable.ic_tab_artists_grey))
                      .setContent(intent);
        tabHost.addTab(spec);
        intent = new Intent().setClass(this, VpnView.class);
        spec = tabHost.newTabSpec("vpn").setIndicator(getString(R.string.main_vpnTabTitle),
                          res.getDrawable(R.drawable.ic_tab_artists_grey))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, MiscView.class);
        spec = tabHost.newTabSpec("misc").setIndicator(getString(R.string.main_miscTabTitle),
                          res.getDrawable(R.drawable.ic_tab_artists_grey))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, ChartView.class);
        spec = tabHost.newTabSpec("chart").setIndicator(getString(R.string.main_chartsTabTitle),
                          res.getDrawable(R.drawable.ic_tab_artists_grey))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, WifiRecorderView.class);
        spec = tabHost.newTabSpec("wifiRec").setIndicator(getString(R.string.main_wifiRecorderTabTitle),
                          res.getDrawable(R.drawable.ic_tab_artists_grey))
                      .setContent(intent);
        tabHost.addTab(spec);
        
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu _menu)
    {
    	MenuInflater mi = getMenuInflater();
    	mi.inflate(R.menu.menu, _menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) 
        {
        case R.id.exit:
        	finish();
            return true; 	
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}