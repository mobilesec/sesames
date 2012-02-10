package at.sesame.fhooe.midsd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import at.sesame.fhooe.midsd.hd.HD_Fragment;
import at.sesame.fhooe.midsd.ld.LD_Fragment;
import at.sesame.fhooe.midsd.md.MD_Fragment;

public class MID_SesameDisplayActivity 
extends FragmentActivity 
{
	private static final String TAG = "MID_SesameDisplay";
	
	private Fragment mLdFrag;
	private Fragment mMdFrag;
	private Fragment mHdFrag;
	
	private Fragment mCurFrag;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLdFrag = new LD_Fragment(getApplicationContext());
        mMdFrag = new MD_Fragment(getApplicationContext());
        mHdFrag = new HD_Fragment(getApplicationContext());
        setContentView(R.layout.main);
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