package at.fhooe.facedetectionview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * UI element that displays a bitmap.
 * 
 * @author Rainhard Findling
 * @date 04.01.2012
 * @version 1
 */
public class BitmapView extends View {

	// ================================================================================================================
	// MEMBERS

	/** bitmap to be shown. */
	private Bitmap	mBitmap	= null;

	// ================================================================================================================
	// METHODS

	public BitmapView(Context _context, AttributeSet _attrs, int _defStyle) {
		super(_context, _attrs, _defStyle);
	}

	public BitmapView(Context _context, AttributeSet _attrs) {
		super(_context, _attrs);
	}

	public BitmapView(Context _context) {
		super(_context);
	}

	@Override
	protected void onDraw(Canvas _canvas) {
		if (mBitmap == null) {
			super.onDraw(_canvas);
			return;
		}

		Paint paint = new Paint();
		paint.setFilterBitmap(true);
		// scale width while keeping aspect ratio
		// float aspectRatio = ((float) mBitmap.getWidth()) /
		// mBitmap.getHeight();
		// Rect dest = new Rect(0, 0, this.getWidth(), (int) (this.getHeight() /
		// aspectRatio));
		Rect dest = new Rect(0, 0, this.getWidth(), this.getHeight());
		_canvas.drawBitmap(mBitmap, null, dest, paint);
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public void setBitmap(Bitmap _bitmap) {
		mBitmap = _bitmap;
	}
}
