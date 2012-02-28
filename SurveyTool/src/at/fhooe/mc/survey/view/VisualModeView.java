package at.fhooe.mc.survey.view;

import java.util.ArrayList;

import weka.filters.unsupervised.attribute.FirstOrder;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.fhooe.mc.R;
import at.fhooe.mc.consts.ConstParameters;
import at.fhooe.mc.extern.fingerprintInformation.MeasurementPoint;
import at.fhooe.mc.notification.Toasts;

/**
 * Class representing the VisualModeView
 * 
 * @author bernhard_stemmer
 * 
 */
public class VisualModeView extends RecorderView implements OnClickListener {

	/** LOG_TAG for easier Log-Management **/
	private static final String LOG_TAG = "VisualModeView";

	/** Array List with ArrayList of all Rooms and their MPs in it **/
	private ArrayList<ArrayList<MeasurementPoint>> m_MPinRooms = new ArrayList<ArrayList<MeasurementPoint>>();

	/** the Label of actual room **/
	private TextView m_actRoomLabel;

	/** the actual room number **/
	private int m_roomNumber;
	
	/** the first number of mp in room **/
	private int m_firstMPNumberInRoom;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param _savesInstanceStat
	 *            the saves State
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle _savedInstanceState) {

		Log.e(LOG_TAG, "in onCreate!");
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.visual_mode_view);

		m_actRoomLabel = (TextView) findViewById(R.id.visual_mode_view_ActRoomLabel);

		Bundle bundle = getIntent().getExtras();
		m_MPinRooms = (ArrayList<ArrayList<MeasurementPoint>>) bundle
				.getSerializable(RecorderView.RESULT_BUNDLE_MP_IN_ROOM_KEY);

		m_roomNumber = (int) bundle
				.getInt(RecorderView.RESULT_BUNDLE_ROOM_NUMBER_KEY);
		createRoomWithButtons();

	}

	/**
	 * Method creates Button in View dynamically
	 */
	private void createRoomWithButtons() {

		LinearLayout layoutbase = (LinearLayout) findViewById(R.id.visual_mode_view_Layout);
		layoutbase.setOrientation(LinearLayout.VERTICAL);
		layoutbase.setBackgroundColor(Color.GRAY);

		m_actRoomLabel.setText(R.string.VisualModeView_room
				+ m_MPinRooms.get(m_roomNumber).get(0).getRoom());

		// layoutbase.setGravity(Gravity.FILL_VERTICAL);

		// get the number of MP in row
		int COLUMNS = ConstParameters.numberOfMPInRow;

		// get the size if the first room
		int SIZE = m_MPinRooms.get(m_roomNumber).size();

		// number of mp in row in settings to big
		if (ConstParameters.numberOfMPInRow > SIZE) {
			Toasts.wrongMpInRow(getApplicationContext());
			return;
		}

	    m_firstMPNumberInRoom = 0;
		String[] help = m_MPinRooms.get(m_roomNumber).get(0).getName()
				.split(" ");
		m_firstMPNumberInRoom = Integer.parseInt(help[help.length - 1]);
		Log.e(LOG_TAG, "First MP in Room : " + m_firstMPNumberInRoom);

		// create the buttons for the first room
		for (int i = 0; i < SIZE; i++) {
			if (i % COLUMNS == 0) {
				// new row
				// check if row is possible with number of columns
				// if not fill only with rest of it
				if ((SIZE - i) < COLUMNS) {
					layoutbase.addView(generateHoriLayoutWithButtons(SIZE - i,
							m_firstMPNumberInRoom + i));
				} else {
					layoutbase.addView(generateHoriLayoutWithButtons(COLUMNS,
							m_firstMPNumberInRoom + i));
				}
			}
		}

	}

	/**
	 * Return a horizontal LinarLayout with buttons in it
	 * 
	 * @param _columns
	 *            number of the buttons in row
	 * @param _numStartButton
	 *            number of the first button
	 * @return
	 */
	LinearLayout generateHoriLayoutWithButtons(int _columns, int _numStartButton) {

		LinearLayout horiLayout = new LinearLayout(this);
		horiLayout.setOrientation(LinearLayout.HORIZONTAL);
		// horiLayout.setGravity(Gravity.FILL_HORIZONTAL);
		horiLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		for (int j = 0; j < _columns; j++) {
			horiLayout.addView(generateButton(_numStartButton + j));

		}

		return horiLayout;
	}

	/**
	 * Returns a button for dynamically button creation
	 * 
	 * @param _num
	 *            the number of the button
	 * @return the generated button
	 */
	Button generateButton(int _num) {

		Button button = new Button(this);
		// button.setGravity(Gravity.FILL_HORIZONTAL);
		if (_num < 10) {
			button.setText("MP " + _num);
		} else {
			button.setText("MP" + _num);
		}
		button.setTag("visual_mode_view_MP" + _num + "_button");
		button.setId(_num);
		button.setOnClickListener(this);

		return button;
	}

	/**
	 * Called when button is clicked
	 * 
	 * @param _agr0
	 *            the argument of pressed button
	 */
	@Override
	public void onClick(View _v) {

		Log.e(LOG_TAG, "Button pressed: " + _v.getId());

		if (_v.getId() <= m_MPinRooms.get(m_roomNumber).size() + m_firstMPNumberInRoom) {
			MeasurementPoint mp = m_MPinRooms.get(m_roomNumber).get(_v.getId()-m_firstMPNumberInRoom);
			super.setCurrentMP(mp);
			super.startMeasureWithMP();
		}

	}

}
