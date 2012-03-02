package at.fhooe.test;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import at.fhooe.facedetectionview.R;
import at.fhooe.facedetectionview.model.FacesDetectedEvent;
import at.fhooe.facedetectionview.model.ImageNormalizerUtil;
import at.fhooe.facedetectionviewcomponent.FaceDetectionViewComponent;
import at.fhooe.mc.genericobserver.GenericObservable;
import at.fhooe.mc.genericobserver.GenericObserver;

/**
 * @author Rainhard Findling
 * @date 02.03.2012
 * @version 1
 */
public class FaceDetectionViewComponentActivity extends Activity implements GenericObserver<FacesDetectedEvent> {

	/** shows the frontal camera, detects faces periodically. */
	private FaceDetectionViewComponent	mFaceViewComponent	= new FaceDetectionViewComponent();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mFaceViewComponent.addObserver(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// FIXME getting startup-orientation is obviously not working this way
		setProcessingOrientation(getWindowManager().getDefaultDisplay().getOrientation());
	}

	private void configureFaceViewComponent() {
		// add faceview to viewgroup
		Object o = findViewById(R.id.camera_preview);
		FrameLayout preview = (FrameLayout) o;
		mFaceViewComponent.onResume(this, preview, true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// remove faceview from viewgroup
		mFaceViewComponent.onPause();
	}

	@Override
	public void update(GenericObservable<FacesDetectedEvent> _arg0, FacesDetectedEvent _arg1) {
		// update came from faceviewcomponent
		FacesDetectedEvent e = (FacesDetectedEvent) _arg1;
		Log.e("FACES", "totalFoundFaces=" + e.amountOfFaces());
		// debug: show camera image
		// BitmapView bitmapView = (BitmapView) findViewById(R.id.bitmapview);
		// if (bitmapView == null) {
		// Log.e("FACES", "mBitmapView is null!");
		// return;
		// }
		// if (bitmapView.getBitmap() != null) {
		// bitmapView.getBitmap().recycle();
		// }
		// bitmapView.setBitmap(_arg1.getScreenBitmap());
		// bitmapView.invalidate();
		// bitmapView.requestLayout();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setProcessingOrientation(newConfig.orientation);
	}

	private void setProcessingOrientation(int _orientation) {
		setContentView(R.layout.main);
		mFaceViewComponent.onPause();
		configureFaceViewComponent();
		switch (_orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				Log.e("FACES", "orientation changed to portrait");
				mFaceViewComponent.setOrientation(ImageNormalizerUtil.Orientation.Portrait);
				break;
			case Configuration.ORIENTATION_LANDSCAPE:
				Log.e("FACES", "orientation changed to landscape");
				mFaceViewComponent.setOrientation(ImageNormalizerUtil.Orientation.Landscape);
			default:
				Log.e("FACES", "orientation changed to UNKOWN");
				mFaceViewComponent.setOrientation(ImageNormalizerUtil.Orientation.Landscape);
				break;
		}
	}

	// public void surfaceCreated(SurfaceHolder holder) {
	// // The Surface has been created, now tell the camera where to draw the
	// // preview.
	// try {
	// mCamera.setPreviewDisplay(holder);
	// mCamera.startPreview();
	// } catch (IOException e) {
	// LOGGER.debug("Error setting camera preview: " + e.getMessage());
	// }
	// }
	//
	// public void surfaceChanged(SurfaceHolder holder, int format, int width,
	// int height) {
	// }
	//
	// public void surfaceDestroyed(SurfaceHolder holder) {
	// }
	//
	// public void setBitmapViewBitmap(Bitmap _b) {
	// BitmapView b = (BitmapView) findViewById(R.id.bitmapview);
	// if (b == null) {
	// LOGGER.error("b is null!");
	// return;
	// }
	// b.setBitmap(_b);
	// b.postInvalidate();
	// b.requestLayout();
	// }
}