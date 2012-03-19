package at.fhooe.facedetectionview.model;

import android.graphics.Point;

/**
 * Util for (cheap) estimations of distances. Some ideas taken from {@link http
 * ://stackoverflow
 * .com/questions/8698889/how-to-measure-height-width-and-distance
 * -of-object-using-camera}.
 * 
 * @author Rainhard Findling
 * @date 19.03.2012
 * @version 1
 */
public class DistanceMetricUtil {

	// ================================================================================================================
	// MEMBERS

	/**
	 * the optical angle of the device's camera, measured on the longer side of
	 * the picture. in rad.
	 */
	public static float	CAMERA_ANGLE_LONG_SIDE_DEFAULT	= (float) Math.PI / 180f * 62f;
	/** the average detected face height in cm. */
	public static float	AVERAGE_DETECTED_FACE_HEIGHT	= 16f;

	// ================================================================================================================
	// METHODS

	/**
	 * Calculates a rough distance metric about what distance the face has to
	 * the device. <b>This is neither an exact, nor a proportional
	 * calculation!</b>
	 * 
	 * @param _faceDimension
	 * @param _cameraPictureSize
	 * @param _subsamplingFactor
	 * @param _cameraAngleLongSide
	 * @return
	 */
	public static float calculateDistanceMetric(Point _faceDimension, Point _cameraPictureSize, float _subsamplingFactor,
			float _cameraAngleLongSide, float _agerageDetectedFaceHeight) {
		// the longer side of the camera picture is our reference length
		int camLength = Math.max(_cameraPictureSize.x, _cameraPictureSize.y);
		// use the height of the face for reference length
		int faceLength = (int) (_faceDimension.x * _subsamplingFactor);
		// return (float) faceLength / (float) camLength;
		return (float) (_agerageDetectedFaceHeight / Math.atan(_cameraAngleLongSide / camLength * faceLength));
	}

	/**
	 * See {@link #calculateDistanceMetric(Point, Point, float, float, float)}
	 * with {@link #CAMERA_ANGLE_LONG_SIDE_DEFAULT} and
	 * {@link #CAMERA_ANGLE_LONG_SIDE_DEFAULT}.
	 * 
	 * @param _faceDimension
	 * @param _cameraPictureSize
	 * @param _subsamplingFactor
	 * @return
	 */
	public static float calculateDistanceMetric(Point _faceDimension, Point _cameraPictureSize, float _subsamplingFactor) {
		return calculateDistanceMetric(_faceDimension, _cameraPictureSize, _subsamplingFactor, CAMERA_ANGLE_LONG_SIDE_DEFAULT,
				AVERAGE_DETECTED_FACE_HEIGHT);
	}
}
