package at.fhooe.facedetectionview.view;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * UI element that shows a preview of the device's current camera image.
 * 
 * @author Rainhard Findling
 * @date 04.01.2012
 * @version 1
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private static final Logger		LOGGER				= LoggerFactory.getLogger(CameraPreview.class);

	// ================================================================================================================
	// MEMBERS

	private SurfaceHolder			mHolder;
	/** link to the camera the UI element is getting the picture from */
	private Camera					mCamera;
	/**
	 * the FaceView which markes detected faces. we update it with the current
	 * camera image continuously.
	 */
	private Camera.PreviewCallback	mPreviewCallback	= null;

	// ================================================================================================================
	// ACTIVITY METHODS and CALLBACKS

	public CameraPreview(Context context, Camera camera, Camera.PreviewCallback _previewCallback) {
		super(context);
		mCamera = camera;
		mPreviewCallback = _previewCallback;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			LOGGER.debug("Error setting camera preview: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// make any resize, rotate or reformatting changes here

		// start preview with new settings
		try {

			// set the faceview as cameracallback = make it a listener
			Camera.Parameters parameters = mCamera.getParameters();
			mCamera.setParameters(parameters);
			if (mPreviewCallback != null) {
				mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
				Camera.Size size = parameters.getPreviewSize();
				byte[] data = new byte[size.width * size.height * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8];
				mCamera.addCallbackBuffer(data);
			}

			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
			LOGGER.debug("Error starting camera preview: " + e.getMessage());
		}
	}
}
