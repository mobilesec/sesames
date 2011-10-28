package at.sesame.fhooe.pms;

import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import at.sesame.fhooe.lib.pms.PMSProvider;
import at.sesame.fhooe.lib.pms.model.ControllableDevice;
import at.sesame.fhooe.lib.pms.model.ControllableDevice.PowerOffState;

public class FancyPMSClientActivity 
extends ListActivity 
implements OnItemClickListener, Runnable
{
	private static final String TAG = "FancyPMSClientActivity";

	private static final int ACTIVE_DEVICE_ACTION_DIALOG = 0;
	private static final int INACTIVE_DEVICE_ACTION_DIALOG = 1;

	private ArrayList<ControllableDevice> mAllDevices = new ArrayList<ControllableDevice>();
	//	private ArrayList<ControllableDevice> mAliveDevices = new ArrayList<ControllableDevice>();
	//	private ArrayList<ControllableDevice> mNonAliveDevices = new ArrayList<ControllableDevice>();
	private ArrayList<IListEntry> mEntries = new ArrayList<IListEntry>();

	//	private ListView mAliveListView;
	//	private ListView mNonAliveListView;

	//	private ControllableDeviceAdapter mAliveAdapter;
	//	private ControllableDeviceAdapter mNonAliveAdapter;

	private ControllableDeviceAdapter mAdapter;

//	private TextView mActiveLabel;
//	private TextView mInactiveLabel;

	private Thread mDeviceStateRefreshThread = new Thread(this);
	private boolean mUpdating = true;
	private int mUpdatePeriod = 5000;

	private ControllableDevice mSelectedDevice;

	/**
	 * the ProgressDialog to indicate networking
	 */
	ProgressDialog mNetworkingDialog;

	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
		//		setContentView(R.layout.fancy_pms);

		setupNetworkingDialog();

		queryControllableDevices();

		//		mActiveLabel = (TextView)findViewById(R.id.activeDeviceLabel);
		//		mInactiveLabel = (TextView)findViewById(R.id.inactiveDeviceLabel);

		//		splitAndSortDevices();

		//		mAliveAdapter = new ControllableDeviceAdapter(getApplicationContext(), R.layout.controllable_device_listitem, mAliveDevices);
		//		mAliveListView = (ListView)findViewById(R.id.listView1);
		//		mAliveListView.setAdapter(mAliveAdapter);
		//		mAliveListView.setOnItemClickListener(this);
		//
		//		mNonAliveAdapter = new ControllableDeviceAdapter(getApplicationContext(), R.layout.controllable_device_listitem, mNonAliveDevices);
		//		mNonAliveListView = (ListView)findViewById(R.id.listView2);
		//		mNonAliveListView.setAdapter(mNonAliveAdapter);
		//		mNonAliveListView.setOnItemClickListener(this);
		refreshListEntries();
		mAdapter = new ControllableDeviceAdapter(getApplicationContext(), mEntries);
		setListAdapter(mAdapter);
		this.getListView().setOnItemClickListener(this);


		//		ListView lv3 = (ListView)findViewById(R.id.listView3);
		//		lv3.setAdapter(adapter);
		//		lv3.setOnItemClickListener(this);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mUpdating = false;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mUpdating = false;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.e(TAG, "onResume");
		mUpdating = true;
		mDeviceStateRefreshThread.start();
	}

	@Override
	public Dialog onCreateDialog(int _id)
	{
		AlertDialog.Builder builder  = new AlertDialog.Builder(this);
		builder.setTitle("TITLE");
//		builder.setMessage("MESSAGE");
		switch(_id)
		{
		case ACTIVE_DEVICE_ACTION_DIALOG:
			final CharSequence[] activeItems = {"Shutdown", "Sleep", "Cancel"};

			builder.setItems(activeItems, new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					Log.e(TAG, (String) activeItems[which]);
					switch (which) {
					case 0:
						mSelectedDevice.powerOff(PowerOffState.shutdown);
						dialog.cancel();
						break;
					case 1:
						mSelectedDevice.powerOff(PowerOffState.sleep);
						dialog.cancel();
						break;
					case 2:
						dialog.cancel();
						break;

					default:
						break;
					}
				}
			});
			
			break;
//			return builder.create();
			
		case INACTIVE_DEVICE_ACTION_DIALOG:
			
			
			final CharSequence[] inactiveItems = {"Wake up", "Cancel"};

			builder.setItems(inactiveItems, new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					Log.e(TAG, (String) inactiveItems[which]);
					switch (which) {
					case 0:
						mSelectedDevice.wakeUp();
						dialog.cancel();
						break;
					case 1:
						dialog.cancel();
					default:
						break;
					}
				}
			});
//			return builder.create();
		}
		return builder.create();
	}
	
	@Override
	public void onPrepareDialog(int _id, Dialog _d)
	{
		String title = mSelectedDevice.getHostname();
		Log.e(TAG, "onPrepareDialog: id="+_id+" title="+title);
		
//		AlertDialog ad = (AlertDialog)_d;
//		ad.setTitle(title);
		_d.setTitle(title);
		
		if(_id==ACTIVE_DEVICE_ACTION_DIALOG)
		{
//			Toast.makeText(getApplicationContext(), , Toast.LENGTH_LONG).show();
			for (int i=0; i < 5; i++){ Toast.makeText(this, "idle since="+mSelectedDevice.getIdleSince(), Toast.LENGTH_LONG).show(); }
		}
	
	}

	/**
	 * creates the networking dialog
	 */
	private void setupNetworkingDialog()
	{
		mNetworkingDialog = new ProgressDialog(FancyPMSClientActivity.this);
		mNetworkingDialog.setMessage("Networking in progress, please wait...");
		mNetworkingDialog.setCancelable(false);
		mNetworkingDialog.setCanceledOnTouchOutside(false);
		mNetworkingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}

	/**
	 * shows the networking dialog
	 */
	private void showNetworkingDialog()
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				mNetworkingDialog.show();
				Looper.loop();
			}
		}).start();
	}

	/**
	 * dismisses the networking dialog if it is showing
	 */
	private void dismissNetworkingDialog()
	{
		if(mNetworkingDialog.isShowing())
		{
			mNetworkingDialog.dismiss();
		}
	}

	/**
	 * parses the list of all devices, divides them into devices that are alive and those
	 * that are not and sorts the alive devices by idle time
	 */
	private void refreshListEntries() 
	{
		ArrayList<ControllableDevice> activeDevs = new ArrayList<ControllableDevice>();
		ArrayList<ControllableDevice> inactiveDevs = new ArrayList<ControllableDevice>();

		for(ControllableDevice cd:mAllDevices)
		{
			if(cd.isAlive())
			{
				activeDevs.add(cd);
			}
			else
			{
				inactiveDevs.add(cd);
			}
		}
		Collections.sort(activeDevs, new ControllableDeviceComparator());
		Collections.sort(inactiveDevs, new ControllableDeviceComparator());
		mEntries.clear();
		mEntries.add(new SeparatorListEntry("Active ("+activeDevs.size()+")"));
		for(ControllableDevice cd:activeDevs)
		{
			mEntries.add(new ControllableDeviceListEntry(cd));
		}
		mEntries.add(new SeparatorListEntry("Inactive ("+inactiveDevs.size()+")"));
		for(ControllableDevice cd:inactiveDevs)
		{
			mEntries.add(new ControllableDeviceListEntry(cd));
		}

		//		updateNoActiveDevices(mAliveDevices.size());
		//		updateNoNonActiveDevices(mNonAliveDevices.size());
	}

	//	private void updateNoActiveDevices(final int _noDevs)
	//	{
	//		runOnUiThread(new Runnable() 
	//		{	
	//			@Override
	//			public void run() 
	//			{
	//				StringBuilder text = new StringBuilder(getString(R.string.activeDeviceLabelBaseText));
	//				text.append(getNumberSuffix(_noDevs));
	//				mActiveLabel.setText(text.toString());
	//			}
	//		});
	//		
	//	}
	//
	//	private void updateNoNonActiveDevices(final int _noDevs)
	//	{
	//		runOnUiThread(new Runnable() 
	//		{	
	//			@Override
	//			public void run() 
	//			{
	//				StringBuilder text = new StringBuilder(getString(R.string.nonActiveDeviceLabelBaseText));
	//				text.append(getNumberSuffix(_noDevs));
	//				mInactiveLabel.setText(text.toString());
	//			}
	//		});
	//		
	//	}

	//	private String getNumberSuffix(int _noDevs)
	//	{
	//		StringBuilder sb = new StringBuilder(" (");
	//		sb.append(_noDevs);
	//		sb.append(")");
	//		return sb.toString();
	//	}

	private void queryControllableDevices() 
	{
		showNetworkingDialog();

		//		new Thread(new Runnable() {
		//
		//			@Override
		//			public void run() {
		//				Looper.prepare();
		ArrayList<String> macs = PMSProvider.getDeviceList();
		synchronized(mAllDevices)
		{
			mAllDevices = new ArrayList<ControllableDevice>();
			for(int i = 0;i<macs.size();i++)
			{
				mAllDevices.add(new ControllableDevice(macs.get(i), "admin", "pwd", true));
			}
		}
		dismissNetworkingDialog();
		//				Looper.loop();
		//			}
		//		}).start();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.e(TAG, "item clicked:"+arg2);
		Log.e(TAG, arg0.getClass().toString());
		ListView lv = (ListView)arg0;
		IListEntry selectedEntry = (IListEntry)lv.getAdapter().getItem(arg2);
		if(!selectedEntry.isSeparator())
		{
			ControllableDeviceListEntry cdle = (ControllableDeviceListEntry)selectedEntry;
			mSelectedDevice = cdle.getControllableDevice();
			Log.e(TAG, mSelectedDevice.toString());
			if(mSelectedDevice.isAlive())
			{
				
				showDialog(ACTIVE_DEVICE_ACTION_DIALOG);
			}
			else
			{
				showDialog(INACTIVE_DEVICE_ACTION_DIALOG);
			}
		}



	}

	@Override
	public void run() 
	{
		while(mUpdating)
		{
			Log.e(TAG, "updating");
			for(ControllableDevice cd:mAllDevices)
			{
				cd.updateStatus();
			}

			refreshListEntries();

			runOnUiThread(new Runnable() 
			{	
				@Override
				public void run() 
				{
					mAdapter.notifyDataSetChanged();
					//					mAliveAdapter.notifyDataSetChanged();
					//					mNonAliveAdapter.notifyDataSetChanged();

				}
			});

			try 
			{
				Thread.sleep(mUpdatePeriod);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}

	}


}
