/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.charts;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import at.sesame.fhooe.R;
import at.sesame.fhooe.charts.rating.RatingFragment;

/**
 * this class shows one simple diagram to give an example of a possible
 * implementation of the aChartEngine
 * @author Peter Riedl
 *
 */
public class ChartView
extends Activity
{
	public boolean onPrepareOptionsMenu(Menu _menu)
	{
//		super.onCreateOptionsMenu(_menu);
//		getMenuInflater().inflate(R.menu.menu, _menu);
		_menu.add("test");
		Log.e("ChartView", "onPrepareOptionsMenu");
		
//		new ChartView().onCreateOptionsMenu(_menu);
		return true;
	}
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem _item)
//	{
//		Log.e("ChartView", "onContextItemSelected called");
//		switch (_item.getItemId()) 
//		{
//		case R.id.exit:
//			Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
//			break;
//
//		default:
//			return super.onOptionsItemSelected(_item);
//		}
//		return true;
//	}
	
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		setContentView(R.layout.chart);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
//		ft.add(R.id.ratingFrame, new RatingFragment(), "rating");
		ft.add(R.id.navigationFrame, new ChartNavigationFragment(), "navigation");
		ft.add(R.id.ratingFrame, new RatingFragment(), "rating");
//		ft.add(R.id.ratingfra, fragment, tag)
		ft.add(R.id.lineChartFrame, new LineChartViewFragment(), "lineChart");
		ft.add(R.id.barChartFrame, new BarChartViewFragment(), "barChart");
		ft.commit();
		Log.e("ChartView", "onCreate called");
		
	}
}
