package at.sesame.fhooe;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;
import at.sesame.fhooe.calendar.CalendarView;
import at.sesame.fhooe.charts.ChartView;
import at.sesame.fhooe.location.LocationView;
import at.sesame.fhooe.misc.MiscView;
import at.sesame.fhooe.vpn.VpnView;
import at.sesame.fhooe.wifi.WifiView;

public class SesameTabletActivity 
extends TabActivity 
{
    @SuppressWarnings("unused")
	private static final String TAG = "SesameTabletActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Light);
        
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

//        // Do the same for the other tabs
        intent = new Intent().setClass(this, LocationView.class);
        spec = tabHost.newTabSpec("location").setIndicator(getString(R.string.main_locationTabTitle),
                          res.getDrawable(R.drawable.ic_tab_artists_grey))
                      .setContent(intent);
        tabHost.addTab(spec);
//
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
    }
    

    
    @Override
	public boolean onCreateOptionsMenu(Menu _menu)
	{
		getMenuInflater().inflate(R.menu.menu, _menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem _item)
	{
		Log.e("SesameTabletActivity", "onContextItemSelected called");
		switch (_item.getItemId()) 
		{
		case R.id.exit:
			Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
			break;

		default:
			return super.onOptionsItemSelected(_item);
		}
		return true;
	}
    public String toString()
    {
    	return "SesameTabletActivity";
    }
}