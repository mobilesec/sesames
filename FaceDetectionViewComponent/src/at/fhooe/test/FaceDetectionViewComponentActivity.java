package at.fhooe.test;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import at.fhooe.facedetectionview.R;
import at.fhooe.facedetectionview.model.FaceDetector;
import at.fhooe.facedetectionview.model.FacesDetectedEvent;
import at.fhooe.facedetectionview.model.ProcessImageTrigger;
import at.fhooe.facedetectionview.model.FaceDetector.Feature;
import at.fhooe.facedetectionviewcomponent.FaceDetectionViewComponent;

public class FaceDetectionViewComponentActivity extends Activity implements Observer {

	/** shows the frontal camera, detects faces periodically. */
	private FaceDetectionViewComponent	mFaceViewComponent	= new FaceDetectionViewComponent();
	/** tells the faceviewcomponent when to process the next image. */
	private ProcessImageTrigger			mTrigger			= new ProcessImageTrigger() {
																@Override
																public boolean processNextImage() {
																	// TODO
																	// implement
																	// power
																	// saving
																	// mechanism
																	// here if
																	// needed
																	return true;
																}
															};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected void onResume() {
		// add faceview to viewgroup
		Object o = findViewById(R.id.camera_preview);
		FrameLayout preview = (FrameLayout) o;
		mFaceViewComponent.onResume(this, preview, 4, new FaceDetector.Feature[] { Feature.FRONTALFACE_ALT2 }, mTrigger, true);
		mFaceViewComponent.addObserver(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// remove faceview from viewgroup
		int i = R.id.camera_preview;
		Object o = this.findViewById(i);
		FrameLayout preview = (FrameLayout) o;
		mFaceViewComponent.onPause(preview);
	}

	public void update(Observable _arg0, Object _arg1) {
		if (_arg1 instanceof FacesDetectedEvent) {
			// update came from faceviewcomponent
			FacesDetectedEvent e = (FacesDetectedEvent) _arg1;
			Log.e("FACES", "totalFoundFaces=" + e.amountOfFaces());
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