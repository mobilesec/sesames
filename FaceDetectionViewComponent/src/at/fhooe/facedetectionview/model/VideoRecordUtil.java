package at.fhooe.facedetectionview.model;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

/**
 * Encapsulates video recording details.
 * 
 * @author Rainhard Findling
 * @date 04.01.2012
 * @version 1
 */
public class VideoRecordUtil {

	// ================================================================================================================
	// MEMBERS

	private static final Logger	LOGGER				= LoggerFactory.getLogger(VideoRecordUtil.class);

	// which type of media we want to tread (generally video)
	public static final int		MEDIA_TYPE_IMAGE	= 1;
	public static final int		MEDIA_TYPE_VIDEO	= 2;

	// ================================================================================================================
	// METHODS

	/**
	 * preprares the media recorder for recording videos. call only if you want
	 * do start recording *now*. hint: changing this method will most likely
	 * brake your code ;)
	 * 
	 * @param _directoryName
	 * 
	 * @see <br>
	 *      http ://developer.android.com/guide/topics/media/camera.html#capture
	 *      -video
	 * 
	 * @return true if preparing was successful.
	 */
	public static MediaRecorder prepareVideoRecorder(Camera _camera, Surface _surface, String _directoryName) {
		// mCamera = getCameraInstance();
		MediaRecorder mediaRecorder = new MediaRecorder();
		// Step 1: Unlock and set camera to MediaRecorder
		_camera.unlock();
		mediaRecorder.setCamera(_camera);
		// Step 2: Set sources
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// mMediaRecorder.setVideoSize(720, 480);
		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
		// Step 4: Set output file
		mediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO, _directoryName).toString());
		// Step 5: Set the preview output
		mediaRecorder.setPreviewDisplay(_surface);
		// Step 6: Prepare configured MediaRecorder
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			LOGGER.error("IllegalStateException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder(_camera, mediaRecorder);
			return null;
		} catch (IOException e) {
			LOGGER.error("IOException preparing MediaRecorder: " + e.getMessage());
			releaseMediaRecorder(_camera, mediaRecorder);
			return null;
		}
		return mediaRecorder;
	}

	/**
	 * Create a File for saving an image or video
	 * 
	 * @param _directoryName
	 */
	public static File getOutputMediaFile(int type, String _directoryName) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				_directoryName);
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}
		return mediaFile;
	}

	/**
	 * Call to release the media recorder formerly prepared by
	 * {@link #prepareVideoRecorder()}. Call this method when you want to stop
	 * an active recording or when any error occurs during preparation of the
	 * recording or the recording itself (otherwise cam remains locked).
	 */
	public static void releaseMediaRecorder(Camera _camera, MediaRecorder _mediaRecorder) {
		if (_mediaRecorder != null) {
			_mediaRecorder.reset(); // clear recorder configuration
			_mediaRecorder.release(); // release the recorder object
			_camera.lock(); // lock camera for later use
		}
	}

	/**
	 * Call to release the camera and set the link {@link #mCamera} to null.
	 * Call this method when the app loses focus.
	 */
	public static void releaseCamera(Camera _camera) {
		if (_camera != null) {
			_camera.release(); // release the camera for other applications
		}
	}

	/**
	 * @return instance of an android hw camera we're going to work with. Call
	 *         this when the app goes to foreground.
	 */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			LOGGER.error("Cam not available!");
			e.printStackTrace();
		}
		return c; // returns null if camera is unavailable
	}

	/**
	 * More advanced version of {@link #getCameraInstance()}: searches for the
	 * correct camera.
	 * 
	 * @return
	 */
	public static Camera getCameraInstance(int _CameraInfo_CameraType) {
		int numberOfCameras = Camera.getNumberOfCameras();
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == _CameraInfo_CameraType) {
				try {
					return Camera.open(i); // attempt to get a Camera instance
				} catch (Exception e) {
					// Camera is not available (in use or does not exist)
					LOGGER.error("Cam not available!");
					e.printStackTrace();
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * Sets the camera's display orientation in a way what it will always show a
	 * correct rotated image.
	 * 
	 * @param activity
	 * @param cameraId
	 * @param camera
	 */
	public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}
}
