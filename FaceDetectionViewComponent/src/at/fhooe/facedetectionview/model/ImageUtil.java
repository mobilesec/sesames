package at.fhooe.facedetectionview.model;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvGet2D;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageCOI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_core.cvTranspose;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Util that creates bitmaps out of yuv-encoded data.
 * 
 * @author Rainhard Findling
 * @date 04.01.2012
 * @version 1
 */
public class ImageUtil {

	// ================================================================================================================
	// MEMBERS

	private static final Logger	LOGGER	= LoggerFactory.getLogger(ImageUtil.class);

	// ================================================================================================================
	// METHODS

	/**
	 * decode a yuv-image to a rgb-image.
	 * 
	 * @param yuv420sp
	 * @param width
	 * @param height
	 * @return
	 */
	private static int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
		int[] rgb = new int[width * height];
		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
		return rgb;
	}

	/**
	 * decode a yuv-image to a rgb-image.
	 * 
	 * @param yuv420sp
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap createBitmapFromYUV420SP(byte[] _b, int width, int height) {
		return Bitmap.createBitmap(decodeYUV420SP(_b, width, height), width, height, Bitmap.Config.ARGB_8888);
	}

	/**
	 * Create a resized version of the given bitmap. Does not recycle _b.
	 * 
	 * @param _b
	 * @param _x
	 *            x in px of the resized bitmap.
	 * @param _y
	 *            y in px of the resized bitmap.
	 * @return
	 */
	public static Bitmap createResizedBitmap(Bitmap _b, int _x, int _y) {
		// actual width of the image (img is a Bitmap object)
		int width = _b.getWidth();
		int height = _b.getHeight();

		// new width / heigth
		int newWidth = _x;
		int newHeight = _y;

		// calculate the scale
		float scaleWidth = (float) newWidth / width;
		float scaleHeight = (float) newHeight / height;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();

		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap and set it back
		Bitmap resizedBitmap = Bitmap.createBitmap(_b, 0, 0, width, height, matrix, true);
		return resizedBitmap;
	}

	/**
	 * Extract a subarea from a given bitmap and resize it to a predefined size.
	 * 
	 * @param _source
	 *            bitmap from which the subarea should be extracted
	 * @param _x
	 *            the x coordinate in which the subarea starts in the source
	 * @param _y
	 *            the y coordinate in which the subarea starts in the source
	 * @param _width
	 *            the width of the subarea in the source
	 * @param _height
	 *            the height of the subarea in the source
	 * @param _targetWidth
	 *            the width of the bitmap to be returend
	 * @param _targetHeight
	 *            the height of the bitmap to be returend
	 * @return
	 */
	public static Bitmap extractSubBitmap(Bitmap _source, int _x, int _y, int _width, int _height, int _targetWidth,
			int _targetHeight) {
		// this is the subbitmap of the original one
		Bitmap b = Bitmap.createBitmap(_source, _x, _y, _width, _height);
		// resize to our target size
		Bitmap b2 = ImageUtil.createResizedBitmap(b, _targetWidth, _targetHeight);
		// we no longer need b
		b.recycle();
		return b2;
	}

	/**
	 * Extract all face bitmaps from: a given CvSeq and the current camera image
	 * as bitmap. The returned face bitmaps will be in the size of _targetWidth
	 * and _targetHeight.
	 * 
	 * @param _camview
	 *            current camera image as bitmap
	 * @param _cvToCamviewScale
	 *            if the CvSeq is in a smaller dimension (reduced for better
	 *            performance) this factor is to blow the dimension back up
	 *            again. must be >= 1 to make logical sense.
	 * @param _faces
	 *            the OpenCV faces.
	 * @param _targetWidth
	 * @param _targetHeight
	 * @return
	 */
	public static List<Bitmap> extractBitmapsFromFaces(Bitmap _camview, float _cvToCamviewScale, CvSeq _faces, int _targetWidth,
			int _targetHeight) {
		List<Bitmap> faces = new Vector<Bitmap>();
		int total = _faces.total();
		for (int i = 0; i < total; i++) {
			CvRect r = new CvRect(cvGetSeqElem(_faces, i));
			Bitmap b = extractSubBitmap(_camview, (int) (r.x() * _cvToCamviewScale), (int) (r.y() * _cvToCamviewScale), (int) (r
					.width() * _cvToCamviewScale), (int) (r.height() * _cvToCamviewScale), _targetWidth, _targetHeight);
			faces.add(b);
		}
		return faces;
	}

	/**
	 * Makes an existing bitmap gray by creating a new, gray copy of it - the
	 * original bitmap gets recycled!
	 * 
	 * @param _src
	 * @return
	 */
	public static Bitmap makeBitmapGray(Bitmap _src) {
		Bitmap bm = Bitmap.createBitmap(_src.getWidth(), _src.getHeight(), Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bm);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(_src, 0, 0, paint);
		_src.recycle();
		return bm;
	}

	/**
	 * Create an IplImage out of Android's data in {@link
	 * PreviewCallback#onPreviewFrame(final byte[] data, final Camera camera)}
	 * that is encoded correctly for conversion to Android's {@link Bitmap}s
	 * later.
	 * 
	 * @param _yuv420sp
	 * @param _width
	 * @param _height
	 * @return
	 */
	public static IplImage decodeYUV420SPToIplImage(byte[] _yuv420sp, int _width, int _height) {
		// decode data
		int[] rgb = decodeYUV420SP(_yuv420sp, _width, _height);
		// put in target image
		IplImage iplImage = IplImage.create(_width, _height, IPL_DEPTH_8U, 4);
		iplImage.getIntBuffer().put(rgb);
		return iplImage;
	}

	/**
	 * Creates a Bitmap out of the given IplImage with the exact same size.
	 * 
	 * @param _i
	 * @return
	 */
	public static Bitmap createBitmapOutOf4ChannelIplImages(IplImage _i) {
		// src: http://code.google.com/p/javacv/issues/detail?id=67

		// IplImage image = IplImage.create(width, height, IPL_DEPTH_8U, 4);
		// IplImage _3image = IplImage.create(width, height, IPL_DEPTH_8U, 3);
		// IplImage _1image = IplImage.create(width, height, IPL_DEPTH_8U, 1);
		Bitmap bitmap = Bitmap.createBitmap(_i.width(), _i.height(), Bitmap.Config.ARGB_8888);
		// 1. iplimage -> bitmap
		bitmap.copyPixelsFromBuffer(_i.getByteBuffer());
		// 2. bitmap -> iplimage
		// bitmap.copyPixelsToBuffer(_i.getByteBuffer());
		LOGGER.debug("createBitmapOutOfIplImages(): size=" + bitmap.getWidth() + "/" + bitmap.getHeight());
		return bitmap;
	}

	/**
	 * Method for scaling and rotating an IplImage. <b>Scaling has not been
	 * tested yet!</b>.
	 * 
	 * @param _src
	 * @param _scale
	 * @param _angle
	 * @return
	 */
	public static IplImage createRotatedImage(IplImage _src) {

		// 1. transpose
		IplImage r = IplImage.create(_src.height(), _src.width(), _src.depth(), _src.nChannels());
		cvTranspose(_src, r);

		// 2. flip x+y axis
		cvFlip(r, r, -1);

		// // cvFlip + cvTranspose do not work as there is no cvTranspose in
		// // javacv for an CvArr. use javacv_imagepro.cvWarpAffine instead
		// // cpp source: http://dasl.mem.drexel.edu/~noahKuntz/openCVTut5.html
		// IplImage rotatedPic = IplImage.create(_resizedPic.height(),
		// _resizedPic.width(), _resizedPic.depth(), _resizedPic
		// .nChannels());
		// // CvMat warp_mat = cvCreateMat(2, 3, CV_32FC1);
		// CvMat rot_mat = cvCreateMat(2, 3, CV_32FC1);
		// // Create angle and scale
		//
		// // Compute rotation matrix
		// CvPoint2D32f center = cvPoint2D32f(_resizedPic.width() / 2,
		// _resizedPic.height() / 2);
		// cv2DRotationMatrix(center, _angle, _scale, rot_mat);
		//
		// // Do the transformation
		// cvWarpAffine(_resizedPic, rotatedPic, rot_mat);

		return r;
	}

	/**
	 * Create a gray copy of a given RGB IplImage.
	 * 
	 * @param _source
	 * @return
	 */
	public static IplImage createGrayIplImage(IplImage _source) {
		/* create new image for the grayscale version */
		IplImage dst = cvCreateImage(cvSize(_source.width(), _source.height()), IPL_DEPTH_8U, 1);
		/* CV_RGB2GRAY: convert RGB image to grayscale */
		cvCvtColor(_source, dst, CV_RGB2GRAY);
		return dst;
	}

	/**
	 * Converts a given bitmap to an iplimage. TODO which channels must the
	 * bitmap have, which channels does the iplimage have?
	 * 
	 * @param _bitmap
	 * @return
	 */
	public static IplImage convertBitmapToIplImage(Bitmap _bitmap) {
		LOGGER.debug("convertBitmapToIplImage()");
		IplImage image = IplImage.create(_bitmap.getWidth(), _bitmap.getHeight(), IPL_DEPTH_8U, 4);
		_bitmap.copyPixelsToBuffer(image.getByteBuffer());
		LOGGER.debug("convertBitmapToIplImage() DONE");
		return image;
	}

	/**
	 * Makes a cut-out (crop, "region of interest") of the original image in
	 * native code. the returned image is still the same image, but it's content
	 * has been changed on the bit/byte-layer: it now only contains the part of
	 * the image that has been inside the given coordinates in the image before.
	 * Therefore the size of the image gets reduced to the given width/heigh by
	 * applying roi.
	 * 
	 * @param _source
	 * @param _x
	 * @param _y
	 * @param _width
	 * @param _height
	 * @return
	 */
	public static IplImage createCroppedIplImage(IplImage _source, int _x, int _y, int _width, int _height) {
		IplImage i = IplImage.create(_width, _height, _source.depth(), _source.nChannels());
		cvSetImageROI(_source, cvRect(_x, _y, _width, _height));
		cvCopy(_source, i);
		cvResetImageROI(_source);
		return i;
	}

	/**
	 * Resizes the given iplimage to a new iplimage with the specified width and
	 * height.
	 * 
	 * @param _resultFaceSize
	 * @param faceIplImage
	 * @return
	 */
	public static IplImage createResizedIplImage(Point _resultFaceSize, IplImage faceIplImage) {
		IplImage out = IplImage.create(_resultFaceSize.x, _resultFaceSize.y, faceIplImage.depth(), faceIplImage.nChannels());
		cvResize(faceIplImage, out, CV_INTER_LINEAR);
		return out;
	}

	/**
	 * Create a 4-channel iplimage out of a 1-channel (gray) iplimage.
	 * 
	 * @param _i
	 * @return
	 */
	public static IplImage create4ChannelIplImageOutOf1ChannelIplImage(IplImage _i) {
		// IplImage cImage = cvCreateImage(cvSize(faceIplImage.width(),
		// faceIplImage.height()), faceIplImage.depth(), 4);
		IplImage cImage = IplImage.create(_i.width(), _i.height(), _i.depth(), 4);
		cvSetImageCOI(cImage, 1);
		cvCopy(_i, cImage);
		cvSetImageCOI(cImage, 4);
		return cImage;
	}

	/**
	 * Flip = mirror the iplimage horizontally.
	 * 
	 * @param _i
	 */
	public static IplImage createHorizontallyFlippedIplImage(IplImage _i) {
		cvFlip(_i, _i, 1);
		return _i;
	}

	/**
	 * Write horizontal captured yuv-data into the given iplimage.
	 * 
	 * @param _i
	 *            image to write info to
	 * @param _data
	 *            yuv data
	 * @param _yuvImageWidth
	 *            width of the yuv image
	 * @param _subsamplingFactor
	 *            how much _i is smaller / should be smaller than the yuv-image.
	 * @return
	 */
	public static IplImage writeGrayPartOfYuvTo1ChannelIplimageHorizontal(IplImage _i, byte[] _data, int _yuvImageWidth,
			int _yuvImageHeigth, int _subsamplingFactor) {
		if (_i == null || _i.width() != _yuvImageWidth / _subsamplingFactor
				|| _i.height() != _yuvImageHeigth / _subsamplingFactor) {
			_i = IplImage.create((int) (_yuvImageWidth / _subsamplingFactor), (int) (_yuvImageHeigth / _subsamplingFactor),
					IPL_DEPTH_8U, 1);
		}
		int imageWidth = _i.width();
		int imageHeight = _i.height();
		int dataStride = (int) (_subsamplingFactor * _yuvImageWidth);
		int imageStride = _i.widthStep();
		ByteBuffer imageBuffer = _i.getByteBuffer();
		for (int y = 0; y < imageHeight; y++) {
			int dataLine = y * dataStride;
			int imageLine = y * imageStride;
			for (int x = 0; x < imageWidth; x++) {
				imageBuffer.put(imageLine + x, _data[(int) (dataLine + _subsamplingFactor * x)]);
			}
		}
		return _i;
	}

	/**
	 * Creates an gray bitmap out of a 1 channel IplImage directly and without
	 * conversion errors. <b>VERY SLOW</b>.
	 * 
	 * @param _i
	 * @return
	 */
	public static Bitmap createBitmapOutOf1ChannelIplImage(IplImage _i) {
		Bitmap bitmap = Bitmap.createBitmap(_i.width(), _i.height(), Bitmap.Config.ARGB_8888);
		for (int r = 0; r < _i.height(); r++) {
			for (int c = 0; c < _i.width(); c++) {
				int gray = (int) Math.floor(cvGet2D(_i, r, c).getVal(0));
				bitmap.setPixel(c, r, Color.argb(255, gray, gray, gray));
			}
		}
		return bitmap;
	}

	/**
	 * Write vertical captured yuv-data into the given iplimage.
	 * 
	 * @param _i
	 *            image to write info to
	 * @param _data
	 *            yuv data
	 * @param _yuvImageWidth
	 *            width of the yuv image
	 * @param _yuvImageHeigth
	 *            height of the yuv image
	 * @param _subsamplingFactor
	 *            how much _i is smaller / should be smaller than the yuv-image.
	 * @return
	 */
	public static IplImage writeGrayPartOfYuvTo1ChannelIplimageVertical(IplImage _i, byte[] _data, int _yuvImageWidth,
			int _yuvImageHeigth, int _subsamplingFactor) {
		return createRotatedImage(writeGrayPartOfYuvTo1ChannelIplimageHorizontal(_i, _data, _yuvImageWidth, _yuvImageHeigth,
				_subsamplingFactor));
	}
}
