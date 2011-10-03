package at.sesame.fhooe.charts.rating;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import at.sesame.fhooe.R;

public class RatingItemFragment 
extends Fragment 
{
	public enum Ranking
	{
		FIRST,
		SECOND,
		THIRD
	}
	
	private TextView mClassLabel;
	private TextView mTextLabel;
	private TextView mPriceLabel;
	
	private ImageView mTrohpyView;
	
//	private Drawable mFirstTrophy;
//	private Drawable mSecondTrophy;
//	private Drawable mThirdTrophy;
	
	
	public RatingItemFragment()
	{
		super();
	}
	
	public RatingItemFragment(String _class, String _text, String _price,Ranking _rank)
	{
		super();
		setData(_class, _text, _price, _rank);
	}
	public void onCreate(Bundle _savedInstance)
	{
		super.onCreate(_savedInstance);
//		mFirstTrophy = getResources().getDrawable(R.drawable.ic_menu_settings);
//		mSecondTrophy = getResources().getDrawable(R.drawable.ic_tab_artists_grey);
//		mThirdTrophy = getResources().getDrawable(R.drawable.icon);
	}
	
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstance)
	{
		View v = _inflater.inflate(R.layout.rating_item, _container);
		
		mClassLabel = (TextView)v.findViewById(R.id.ratingItemClass);
		mTextLabel = (TextView)v.findViewById(R.id.ratingItemText);
		mPriceLabel = (TextView)v.findViewById(R.id.ratingItemPrice);
		
		mTrohpyView = (ImageView)v.findViewById(R.id.ratingItemTrophy);
		return v;
	}
	
	public void setData(final String _class, final String _text, final String _price, final Ranking _rank)
	{
		if(null==mClassLabel || null==mTextLabel || null==mPriceLabel || null==mTextLabel)
		{
			return;
		}
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() 
			{
				mClassLabel.setText(_class);
				mTextLabel.setText(_text);
				mPriceLabel.setText(_price);
				
				switch(_rank)
				{
				case FIRST:
					mTrohpyView.setImageResource(R.drawable.ic_menu_trophy_first);
					break;
				case SECOND:
					mTrohpyView.setImageResource(R.drawable.ic_menu_trophy_second);
					break;
				case THIRD:
					mTrohpyView.setImageResource(R.drawable.ic_menu_trophy_third);
					break;
				}
			}
		});
		
	}
	

}
