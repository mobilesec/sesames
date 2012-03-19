package at.sesame.fhooe.midsd.hd;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import at.sesame.fhooe.lib.ui.tabs.TabManager;
import at.sesame.fhooe.midsd.R;

public class HD_TabFragment extends Fragment {
	private static final String TAG = "HD_TabFragment";
	public static final String BUNDLE_NAME_KEY = "name";
	public static final String BUNDLE_NUMBER_KEY = "number";

	private Context mCtx;
	private FragmentManager mFragMan;

	private TabHost mTabHost;
	private TabManager mTabManager;

	public HD_TabFragment(Context _ctx, FragmentManager _fm) {
		mCtx = _ctx;
		mFragMan = _fm;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.hd_tab_fragment_layout, null);
		mTabHost = (TabHost) v
				.findViewById(R.id.hd_tab_fragment_layout_tabhost);
		mTabManager = new TabManager(mCtx, getFragmentManager(), mTabHost,
				android.R.id.tabcontent);
		createTabs();
		styleTabs();
		return v;
	}

	private void createTabs() {
		mTabHost.setup();
		Bundle args = new Bundle();
		args.putString(BUNDLE_NAME_KEY, "asdf");
		args.putInt(BUNDLE_NUMBER_KEY, 1);
		// args.putSerializable("", mCtx);
		mTabManager.addTab(
				mTabHost.newTabSpec("real").setIndicator("Echtzeit"),
				RealTimeChartFragment.class, args);
		mTabManager.addTab(mTabHost.newTabSpec("comp")
				.setIndicator("Vergleich"), ComparisonFragment.class, args);
		// mTabHost.
		// mTabHost.addTab(mTabHost.newTabSpec("").)
	}

	private void styleTabs() {
		TabWidget tw = mTabHost.getTabWidget();
		// Remove line under tab
		Field mBottomLeftStrip;
		Field mBottomRightStrip;

		try {
			mBottomLeftStrip = tw.getClass().getDeclaredField(
					"mBottomLeftStrip");
			mBottomRightStrip = tw.getClass().getDeclaredField(
					"mBottomRightStrip");

			if (!mBottomLeftStrip.isAccessible()) {
				mBottomLeftStrip.setAccessible(true);
			}

			if (!mBottomRightStrip.isAccessible()) {
				mBottomRightStrip.setAccessible(true);
			}

			mBottomLeftStrip.set(tw,
					getResources().getDrawable(R.drawable.blank));
			mBottomRightStrip.set(tw,
					getResources().getDrawable(R.drawable.blank));

		} catch (java.lang.NoSuchFieldException e) {
			// possibly 2.2
			try {
				Method stripEnabled = tw.getClass().getDeclaredMethod(
						"setStripEnabled", boolean.class);
				stripEnabled.invoke(tw, false);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
		}

		for (int i = 0; i < tw.getTabCount(); i++) {
			View v = tw.getChildAt(i);
			((RelativeLayout) tw.getChildTabViewAt(i)).removeViewAt(0);
			((TextView) ((RelativeLayout) tw.getChildTabViewAt(i))
					.getChildAt(0)).setHeight(30);
			tw.getChildAt(i).getLayoutParams().height = 38;
			v.setBackgroundResource(R.drawable.tab_selector);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
}
