package at.sesame.fhooe.midsd;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import at.sesame.fhooe.lib.ui.ProgressFragmentDialog;
import at.sesame.fhooe.midsd.data.ISesameDataListener;
import at.sesame.fhooe.midsd.data.SesameDataCache;
import at.sesame.fhooe.midsd.hd.HD_Fragment;
import at.sesame.fhooe.midsd.ld.INotificationListener;
import at.sesame.fhooe.midsd.ld.LD_Fragment;
import at.sesame.fhooe.midsd.md.MD_Fragment;

public class MID_SesameDisplayActivity 
extends FragmentActivity
{
	private static final String TAG = "MID_SesameDisplay";
	public static final int EDV_1_ID = 15;
	public static final int EDV_3_ID = 18;
	public static final int EDV_6_ID = 17;
	
	private Fragment mLdFrag;
	private Fragment mMdFrag;
	private Fragment mHdFrag;
	
	private Fragment mCurFrag;
	
	private SesameDataCache mDataCache;
	
	private DialogFragment mLoadingDialog;
	
	private Handler mUiHandler = new Handler();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLdFrag = new LD_Fragment(getApplicationContext(), mUiHandler);
        mMdFrag = new MD_Fragment(getSupportFragmentManager(),getApplicationContext(), mUiHandler);
        mHdFrag = new HD_Fragment(getApplicationContext(), getSupportFragmentManager());
        mLoadingDialog = ProgressFragmentDialog.newInstance("Bitte warten...", "daten werden geladen");
        mLoadingDialog.show(getSupportFragmentManager(), null);
        
        new Thread(new Runnable() 
        {	
			@Override
			public void run() 
			{	   
				mDataCache = SesameDataCache.getInstance();
				
				mDataCache.addNotificationListener((INotificationListener)mLdFrag);
				mDataCache.addEsmartDataListener((ISesameDataListener)mMdFrag, EDV_1_ID);
				mDataCache.addEsmartDataListener((ISesameDataListener)mMdFrag, EDV_3_ID);
				mDataCache.addEsmartDataListener((ISesameDataListener)mMdFrag, EDV_6_ID);
				mDataCache.addNotificationListener((INotificationListener)mMdFrag);
				mDataCache.startDeepEsmartUpdates();
				mLoadingDialog.dismiss();
				
			}
		}).start();
        
        setContentView(R.layout.main);
    }

    
    
	@Override
	protected void onDestroy() 
	{
		if(null!=mDataCache)
		{
			mDataCache.cleanUp();
		}
		super.onDestroy();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.mid_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.low_density:
			setShownFragment(mLdFrag);
			break;
		case R.id.medium_density:
			setShownFragment(mMdFrag);
			break;
		case R.id.high_density:
			setShownFragment(mHdFrag);
			break;
		}
		return true;
	}
	
	public void registerEsmartListener(ISesameDataListener _listener, int _id)
	{
		if(null!=mDataCache)
		{
			mDataCache.addEsmartDataListener(_listener, _id);
		}
	}
	
	
    private void setShownFragment(Fragment _frag)
    {
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
    	
    	if(null!=mCurFrag)
    	{
    		ft.remove(mCurFrag);
    	}
    	
    	ft.add(R.id.contentFrame, _frag);
    	ft.commit();
    	mCurFrag = _frag;
    }    
}