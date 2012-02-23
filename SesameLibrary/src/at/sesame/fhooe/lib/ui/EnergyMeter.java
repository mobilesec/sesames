package at.sesame.fhooe.lib.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import at.sesame.fhooe.lib.R;

public class EnergyMeter extends View {
	private static final String TAG = "EnergyMeter";
	private Bitmap mBackground;
	private Bitmap mCase;

	private float mMinValue = 0;
	private float mMaxValue = 100;

	private float mFullAngle = 84;
	
	// Parameter for Unit
	private boolean mDrawUnit = true;
	private String mUnit = "W"; 

	// Parameters for ticks & labels
	private int mTickColor = Color.BLACK;

	private int mMinorTickSpacing = 5;
	private float mMinorTickLength = 20;

	private int mMajorTickSpacing = 25;
	private float mMajorTickLength = 30;
	
	private float mTickTextSize = 16.0f;

	private double mDisplayedValue;

	private int mPointerBaseWidth = 15;
	private Path mPointerPath = null;

	private int mMaxRadius;

	int parentWidth = 0;
	int parentHeight = 0;

	// Displaying parameter relative to max radius
	private float mRelativePointerLength = 1.1f;
	private float mRelativeTickRadius = 1.2f;
	private float mRelativePointerBaseY = 0.46f;

	float centerX = 0.0f;
	float centerY = 0.0f;

	// Parameters for drawing color labels
	boolean mDrawColorLabes = true;
	float[] mColorLabelRange = { 0.6f, 0.8f, 1.0f };
	int[] mColorLabels = { 0xff40c200, 0xffffae00, 0xff9e0e0e };
	float mColorLabelWidth = 10.0f;

	public EnergyMeter(Context context, AttributeSet attrs, int _id) {
		super(context, attrs, _id);
		loadGraphics();
	}

	public EnergyMeter(Context context, AttributeSet attrs) {
		super(context, attrs);
		loadGraphics();
	}

	public EnergyMeter(Context context) {
		super(context);
		loadGraphics();
	}

	public boolean isDrawColorLabes() {
		return mDrawColorLabes;
	}

	public void setDrawColorLabes(boolean mDrawColorLabes) {
		this.mDrawColorLabes = mDrawColorLabes;
	}

	public float[] getColorLabelRange() {
		return mColorLabelRange;
	}

	public void setColorLabelRange(float[] mColorLabelRange) {
		this.mColorLabelRange = mColorLabelRange;
	}

	public int[] getColorLabels() {
		return mColorLabels;
	}

	public void setColorLabels(int[] mColorLabels) {
		this.mColorLabels = mColorLabels;
	}

	public float getColorLabelWidth() {
		return mColorLabelWidth;
	}

	public void setColorLabelWidth(float mColorLabelWidth) {
		this.mColorLabelWidth = mColorLabelWidth;
	}

	public float getMinValue() {
		return mMinValue;
	}

	public void setMinValue(float mMinValue) {
		this.mMinValue = mMinValue;
	}

	public float getMaxValue() {
		return mMaxValue;
	}

	public void setMaxValue(float mMaxValue) {
		this.mMaxValue = mMaxValue;
	}

	public float getFullAngle() {
		return mFullAngle;
	}

	public void setFullAngle(float mFullAngle) {
		this.mFullAngle = mFullAngle;
	}

	public int getMinorTickSpacing() {
		return mMinorTickSpacing;
	}

	public void setMinorTickSpacing(int mMinorTickSpacing) {
		this.mMinorTickSpacing = mMinorTickSpacing;
	}

	public float getMinorTickLength() {
		return mMinorTickLength;
	}

	public void setMinorTickLength(float mMinorTickLength) {
		this.mMinorTickLength = mMinorTickLength;
	}

	public int getMajorTickSpacing() {
		return mMajorTickSpacing;
	}

	public void setMajorTickSpacing(int mMajorTickSpacing) {
		this.mMajorTickSpacing = mMajorTickSpacing;
	}

	public float getMajorTickLength() {
		return mMajorTickLength;
	}

	public void setMajorTickLength(float mMajorTickLength) {
		this.mMajorTickLength = mMajorTickLength;
	}

	public float getRelativePointerLength() {
		return mRelativePointerLength;
	}

	public void setRelativePointerLength(float mRelativePointerLength) {
		this.mRelativePointerLength = mRelativePointerLength;
	}

	public float getRelativeTickRadius() {
		return mRelativeTickRadius;
	}

	public void setRelativeTickRadius(float mRelativeTickRadius) {
		this.mRelativeTickRadius = mRelativeTickRadius;
	}

	public float getRelativePointerBaseY() {
		return mRelativePointerBaseY;
	}

	public void setRelativePointerBaseY(float mRelativePointerBaseY) {
		this.mRelativePointerBaseY = mRelativePointerBaseY;
	}

	public float getTickTextSize() {
		return mTickTextSize;
	}

	public void setTickTextSize(float mTickTextSize) {
		this.mTickTextSize = mTickTextSize;
	}

	public boolean isDrawUnit() {
		return mDrawUnit;
	}

	public void setDrawUnit(boolean mDrawUnit) {
		this.mDrawUnit = mDrawUnit;
	}

	public String getUnit() {
		return mUnit;
	}

	public void setUnit(String mUnit) {
		this.mUnit = mUnit;
	}

	private boolean checkColorLabelRange() {
		boolean ret = true;

		if (mColorLabelRange == null || mColorLabels == null
				|| mColorLabelRange.length != mColorLabels.length)
			ret = false;

		for (int i = 0; i < mColorLabelRange.length; i++) {
			if (mColorLabelRange[i] < 0.0f || mColorLabelRange[i] > 1.0f) {
				ret = false;
				break;
			}

			if (i < mColorLabelRange.length - 1) {
				if (mColorLabelRange[i] >= mColorLabelRange[i + 1]) {
					ret = false;
					break;
				}
			}
		}

		if (!ret)
			Log.e(TAG,
					"Cannot draw color labels, because parameters are set invalid.");

		return ret;
	}

	public void setValue(double _val) throws Exception {
		mDisplayedValue = _val;
		postInvalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// scale view port, so it fits into width
		// View parent = (View) getParent();
		parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		if (mBackground != null && parentWidth != 0 && parentHeight != 0) {
			// int parentWidth = parent.getWidth();
			// int parentHeight = parent.getHeight();
			int meterHeight = mBackground.getHeight() * parentWidth
					/ mBackground.getWidth();
			this.setMeasuredDimension(parentWidth, meterHeight);
			// this.setLayoutParams(new LinearLayout.LayoutParams(parentWidth,
			// meterHeight));
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	private void loadGraphics() {
		System.gc();

		mBackground = BitmapFactory.decodeResource(getContext().getResources(),
				R.drawable.meter_background);
		mCase = BitmapFactory.decodeResource(getContext().getResources(),
				R.drawable.meter_case);
		if (parentWidth != 0 && parentHeight != 0) {
			int meterHeight = mBackground.getHeight() * parentWidth
					/ mBackground.getWidth();
			mBackground = Bitmap.createScaledBitmap(mBackground, parentWidth,
					meterHeight, true);
			mCase = Bitmap.createScaledBitmap(mCase, parentWidth, meterHeight,
					true);
		}

		centerX = mBackground.getWidth() / 2;
		centerY = mBackground.getHeight() + mBackground.getHeight()
				* mRelativePointerBaseY;

		// calculate max radius
		// mMaxRadius = (mBackground.getWidth() < mBackground.getHeight()) ?
		// mBackground
		// .getWidth() : mBackground.getHeight();
		mMaxRadius = mBackground.getWidth();
		mMaxRadius /= 2;

		// set pointer path
		mPointerPath = new Path();
		float pointerTop = centerY - (mMaxRadius * mRelativePointerLength);

		mPointerPath.moveTo(centerX - mPointerBaseWidth / 2, centerY);
		mPointerPath.lineTo(centerX, pointerTop);
		mPointerPath.lineTo(centerX + mPointerBaseWidth / 2, centerY);
		mPointerPath.lineTo(centerX - mPointerBaseWidth / 2, centerY);

		if (mDrawColorLabes)
			mDrawColorLabes = checkColorLabelRange();
	}

	@Override
	protected void onAttachedToWindow() {
		checkParent();
		super.onAttachedToWindow();
	}

	private void checkParent() {
		View parent = (View) getParent();
		if (null != parent) {
			Log.e(TAG, "parentwidth=" + parent.getWidth() + ", parentheight="
					+ parent.getHeight());
		} else {
			Log.e(TAG, "parent was null");
		}
	}

	// private float convertPxToDp(float px) {
	// // DisplayMetrics metrics = new DisplayMetrics();
	// // Display display = ((WindowManager)
	// //
	// getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	// // display.getMetrics(metrics);
	//
	// // TODO: check how to convert, metrics.density always returns 1.0
	// // float logicalDensity = metrics.density;
	// float logicalDensity = 1.5f;
	// return px / logicalDensity;
	//
	// // final float scale = getResources().getDisplayMetrics().density;
	// // return px * scale + 0.5f;
	// }

	@Override
	protected void onDraw(Canvas _c) {
		super.onDraw(_c);
		checkParent();
		loadGraphics();
		double radius = mMaxRadius * mRelativeTickRadius;

		// draw dial
		_c.drawBitmap(mBackground, 0, 0, null);

		// draw color labels
		if (mDrawColorLabes)
			drawColorLabels(_c, (float) radius);

		// draw ticks

		drawTicks(_c, radius, radius - mMinorTickLength, mMinorTickSpacing,
				false);
		drawTicks(_c, radius, radius - mMajorTickLength, mMajorTickSpacing,
				true);

		// draw pointer
		drawPointer(_c);

		// draw case
		_c.drawBitmap(mCase, 0, 0, null);
	}

	private void drawColorLabels(Canvas _c, float radius) {
		Paint p = new Paint();
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(mColorLabelWidth);
		p.setAntiAlias(true);

		// compensate stroke width
		radius -= mColorLabelWidth / 2;

		float startValue = mMinValue;
		float endValue;

		for (int i = 0; i < mColorLabelRange.length; i++) {
			p.setColor(mColorLabels[i]);

			endValue = mMaxValue * mColorLabelRange[i];

			double minAngle;
			double maxAngle;

			try {
				minAngle = convertValueToAngle(startValue);
				maxAngle = convertValueToAngle(endValue);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return;
			}

			_c.drawArc(new RectF((float) (centerX - radius),
					(float) (centerY - radius), (float) (centerX + radius),
					(float) (centerY + radius)), (float) minAngle - 90,
					(float) (maxAngle - minAngle), false, p);

			startValue = endValue;
		}
	}

	private void drawPointer(Canvas _c) {
		try {
			// give it a more natural look
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Style.FILL);
			paint.setShadowLayer(3.0f, 0, 0, 0x30000000);
			paint.setShadowLayer(5, 0, 0, 0x7f000000);
			paint.setColor(Color.BLACK);

			// rotate pointer according to value
			float angle = (float) (convertValueToAngle(mDisplayedValue));

			_c.save(Canvas.MATRIX_SAVE_FLAG);
			_c.rotate(angle, centerX, centerY);
			_c.drawPath(mPointerPath, paint);
			_c.restore();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

	}

	private void drawTicks(Canvas canvas, double longRadius,
			double shortRadius, int ticks, boolean labels) {
		double minAngle;
		double maxAngle;
		try {
			minAngle = convertValueToAngle(mMinValue);
			maxAngle = convertValueToAngle(mMaxValue);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return;
		}

		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setColor(mTickColor);
		p.setStyle(Style.STROKE);
		p.setShadowLayer(3.0f, 0, 0, 0x30000000);

		if (!labels) {
			p.setStrokeWidth(3.0f);
			canvas.drawArc(new RectF((float) (centerX - longRadius),
					(float) (centerY - longRadius),
					(float) (centerX + longRadius),
					(float) (centerY + longRadius)), (float) minAngle - 90,
					(float) (maxAngle - minAngle), false, p);
		}

		for (double i = mMinValue; i <= mMaxValue; i += ticks) {
			double angle;
			try {
				angle = (convertValueToAngle(i)) + 90;
				angle *= Math.PI;
				angle /= 180;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				continue;
			}
			double sinValue = Math.sin(angle);
			double cosValue = Math.cos(angle);
			int x1 = Math.round(centerX - (float) (shortRadius * cosValue));
			int y1 = Math.round(centerY - (float) (shortRadius * sinValue));
			int x2 = Math.round(centerX - (float) (longRadius * cosValue));
			int y2 = Math.round(centerY - (float) (longRadius * sinValue));

			try {
				p.setStrokeWidth(labels ? 3.0f : 1.0f);
				p.setStrokeCap(Cap.ROUND);

				canvas.drawLine(x1, y1, x2, y2, p);

				// draw labels
				if (labels) {
					p.setStrokeWidth(0.0f);
					// p.setTextAlign(Align.LEFT);
					// if (x1 <= x2) {
					// p.setTextAlign(Align.RIGHT);
					// }
					String text = i + "";
					if (Math.round(i) == (long) i) {
						text = (long) i + (mDrawUnit ? (" " + mUnit) : "");
					}
					p.setTextAlign(Align.CENTER);
					p.setTextSize(mTickTextSize);
					canvas.drawText(
							text,
							Math.round(centerX
									- (float) ((shortRadius - shortRadius*0.15f) * cosValue)),
							Math.round(centerY
									- (float) ((shortRadius - shortRadius*0.15f) * sinValue)),
							p);
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				continue;
			}
		}
	}

	private double convertValueToAngle(double _val) throws Exception {
		if (_val > mMaxValue) {
			throw new Exception("can not convert value greater than max ("
					+ mMaxValue + ")");
		}
		return _val / (mMaxValue - mMinValue)// normalization of value to
												// correct range
				* mFullAngle // using the full available angle
				- (mFullAngle / 2) + mMinValue; // producing correct values
												// around the center
	}

}
