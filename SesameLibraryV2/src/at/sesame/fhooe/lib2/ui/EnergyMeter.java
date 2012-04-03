package at.sesame.fhooe.lib2.ui;

import java.text.NumberFormat;

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
import at.sesame.fhooe.lib2.R;

public class EnergyMeter extends View {
	private static final String TAG = "EnergyMeter";
	
	EnergyMeterRenderer mRenderer = new EnergyMeterRenderer();
	
	private static Bitmap mBackground;
	private static Bitmap mCase;
	
	private float mBackgroundRatio = 0.0f;

	private float mMinValue = 0;
	private float mMaxValue = 100;

	private float mFullAngle = 80;

	// Parameter for Unit
	private boolean mDrawUnit = false;
	private String mUnit = "W";

	// Parameters for current value
	private boolean mDrawCurrentValue = true;
	private float mCurrentValueX = 0.5f;
	private float mCurrentValueY = 0.25f;

	// Parameters for ticks & labels
	private int mTickColor = Color.BLACK;
	private boolean mDrawTickLabelOnPath = true;

	private int mMinorTickSpacing = 5;
	private float mMinorTickLength = 20;

	private int mMajorTickSpacing = 25;
	private float mMajorTickLength = 30;

	private float mTickTextSize = 16.0f;

	private double mDisplayedValue;

	private int mPointerBaseWidth = 15;
	private Path mPointerPath = null;

	private int mMaxRadius;

	// Displaying parameter relative to max radius
	private float mRelativePointerLength = 1.00f;
	private float mRelativeTickRadius = 1.10f;
	private float mRelativePointerBaseY = 0.34f;

	float centerX = 0.0f;
	float centerY = 0.0f;

	int parentWidth = 0;
	int parentHeight = 0;

	// Parameters for drawing color labels
	boolean mDrawColorLabes = true;
	float[] mColorLabelRange = { 0.6f, 0.8f, 1.0f };
	int[] mColorLabels = { 0xff40c200, 0xffffae00, 0xff9e0e0e };
	float mColorLabelWidth = 10.0f;
	private double mRadius;

	private Paint paintPointer;
	private Paint paintColorLabels;
	private Paint paintCurrentValue;
	private Paint paintTicks;
	private boolean doScaleBackgroundImages = true;

	public EnergyMeter(Context context, AttributeSet attrs, int _id) {
		super(context, attrs, _id);
		initEnergyMeter();
	}

	public EnergyMeter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initEnergyMeter();
	}

	public EnergyMeter(Context context) {
		super(context);
		initEnergyMeter();
	}
	
	private void initEnergyMeter() {
		loadGraphics();
		loadParameters();
	}

	private void loadParameters() {
		if (mDrawColorLabes)
			mDrawColorLabes = checkColorLabelRange();

		// paint for pointer
		paintPointer = new Paint();
		paintPointer.setAntiAlias(true);
		paintPointer.setStyle(Style.FILL);
		paintPointer.setShadowLayer(3.0f, 0, 0, 0x30000000);
		paintPointer.setShadowLayer(5, 0, 0, 0x7f000000);
		paintPointer.setColor(Color.BLACK);

		// paint for color labels
		paintColorLabels = new Paint();
		paintColorLabels.setStyle(Style.STROKE);
		paintColorLabels.setStrokeWidth(mColorLabelWidth);
		paintColorLabels.setAntiAlias(true);

		// paint for current value
		paintCurrentValue = new Paint();
		paintCurrentValue.setAntiAlias(true);
		paintCurrentValue.setTextAlign(Align.CENTER);
		paintCurrentValue.setTextSize(mTickTextSize);

		// paint for ticks
		paintTicks = new Paint();
		paintTicks.setAntiAlias(true);
		paintTicks.setColor(mTickColor);
		paintTicks.setStyle(Style.STROKE);
		paintTicks.setShadowLayer(3.0f, 0, 0, 0x30000000);
		paintTicks.setStrokeCap(Cap.ROUND);
		paintTicks.setTextAlign(Align.CENTER);
		paintTicks.setTextSize(mTickTextSize);
	}
	
	public void setEnergyMeterRenderer(EnergyMeterRenderer r) {
		mRenderer = r;
		
		// TODO: replace all occurrences by renderer calls
		mColorLabelRange = r.getColorLabelRange();
		mColorLabels = r.getColorLabels();
		mColorLabelWidth = r.getColorLabelWidth();
		mCurrentValueX = r.getCurrentValueX();
		mCurrentValueY = r.getCurrentValueY();
		mFullAngle = r.getFullAngle();
		mMajorTickLength = r.getMajorTickLength();
		mMajorTickSpacing = r.getMajorTickSpacing();
		mMaxValue = r.getMaxValue();
		mMinorTickLength = r.getMinorTickLength();
		mMinorTickSpacing = r.getMinorTickSpacing();
		mMinValue = r.getMinValue();
		mPointerBaseWidth = r.getPointerBaseWidth();
		mRelativePointerBaseY = r.getRelativePointerBaseY();
		mRelativePointerLength = r.getRelativePointerLength();
		mRelativeTickRadius = r.getRelativeTickRadius();
		mTickColor = r.getTickColor();
		mTickTextSize = r.getTickTextSize();
		mUnit = r.getUnit();
		
		// refresh everything
		loadParameters();
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

	private void loadGraphics() {
		// free ressources
		if (mBackground != null) {
			mBackground.recycle();
			mBackground = null;
		}

		if (mCase != null) {
			mCase.recycle();
			mCase = null;
		}

		mBackground = BitmapFactory.decodeResource(getContext().getResources(),
				R.drawable.meter_background);
		mCase = BitmapFactory.decodeResource(getContext().getResources(),
				R.drawable.meter_case);

		mBackgroundRatio = mBackground.getWidth() / mBackground.getHeight();
	}
	
	private void scaleBackgroundImages() {
		if (parentWidth != 0 && parentHeight != 0) {

			mBackground = Bitmap.createScaledBitmap(mBackground, parentWidth,
					(int) (parentWidth / mBackgroundRatio), true);
			mCase = Bitmap.createScaledBitmap(mCase, parentWidth,
					(int) (parentWidth / mBackgroundRatio), true);
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

		mRadius = mMaxRadius * mRelativeTickRadius;
	}

	private void loadPointerPath() {
		mPointerPath = new Path();
		float pointerTop = centerY - (mMaxRadius * mRelativePointerLength);

		mPointerPath.moveTo(centerX - mPointerBaseWidth / 2, centerY);
		mPointerPath.lineTo(centerX, pointerTop);
		mPointerPath.lineTo(centerX + mPointerBaseWidth / 2, centerY);
		mPointerPath.lineTo(centerX - mPointerBaseWidth / 2, centerY);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// scale view port, so it fits into width
		parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		parentHeight = MeasureSpec.getSize(heightMeasureSpec);

		if (mBackground != null && parentWidth != 0 && parentHeight != 0) {
			this.setMeasuredDimension(parentWidth,
					(int) (parentWidth / mBackgroundRatio));
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		loadGraphics();
		doScaleBackgroundImages = true;
	}

	@Override
	protected void onDraw(Canvas _c) {
		super.onDraw(_c);

		if (doScaleBackgroundImages) {
			scaleBackgroundImages();
			loadPointerPath();
			doScaleBackgroundImages = false;
		}

		// draw dial
		_c.drawBitmap(mBackground, 0, 0, null);

		// draw color labels
		if (mDrawColorLabes)
			drawColorLabels(_c, (float) mRadius);

		// draw current value
		if (mDrawCurrentValue)
			drawCurrentValue(_c);

		// draw ticks
		drawTicks(_c, mRadius, mRadius - mMinorTickLength, mMinorTickSpacing,
				false);
		drawTicks(_c, mRadius, mRadius - mMajorTickLength, mMajorTickSpacing,
				true);

		// draw pointer
		drawPointer(_c);

		// draw case
		_c.drawBitmap(mCase, 0, 0, null);
	}

	private void drawColorLabels(Canvas _c, float radius) {
		// compensate stroke width
		radius -= mColorLabelWidth / 2;

		float startValue = mMinValue;
		float endValue;

		for (int i = 0; i < mColorLabelRange.length; i++) {
			paintColorLabels.setColor(mColorLabels[i]);

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
					(float) (maxAngle - minAngle), false, paintColorLabels);

			startValue = endValue;
		}
	}

	private void drawPointer(Canvas _c) {
		try {
			// rotate pointer according to value
			float angle = (float) (convertValueToAngle(mDisplayedValue));
			_c.save(Canvas.MATRIX_SAVE_FLAG);
			_c.rotate(angle, centerX, centerY);
			_c.drawPath(mPointerPath, paintPointer);
			_c.restore();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

	}

	private void drawCurrentValue(Canvas canvas) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(0);
		nf.setGroupingUsed(true);
		String text = nf.format(mDisplayedValue) + (mDrawUnit ? mUnit : "");
		canvas.drawText(text, parentWidth * mCurrentValueX, parentHeight
				* mCurrentValueY, paintCurrentValue);
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

		if (!labels) {
			paintTicks.setStrokeWidth(mRenderer.getMajorTickLineWidth());
			canvas.drawArc(new RectF((float) (centerX - longRadius),
					(float) (centerY - longRadius),
					(float) (centerX + longRadius),
					(float) (centerY + longRadius)), (float) minAngle - 90,
					(float) (maxAngle - minAngle), false, paintTicks);
		}

		for (double i = mMinValue; i <= mMaxValue; i += ticks) {
			double angle;
			double degAngle;
			try {
				degAngle = convertValueToAngle(i);
				angle = degAngle + 90;
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
				paintTicks.setStrokeWidth(labels ? mRenderer.getMajorTickLineWidth() : mRenderer.getMinorTickLineWidth());
				canvas.drawLine(x1, y1, x2, y2, paintTicks);

				// draw labels
				if (labels) {
					paintTicks.setStrokeWidth(0.0f);
					// p.setTextAlign(Align.LEFT);
					// if (x1 <= x2) {
					// p.setTextAlign(Align.RIGHT);
					// }
					String text = i + "";
					if (Math.round(i) == (long) i) {
						text = (long) i + (mDrawUnit ? (" " + mUnit) : "");
					}

					if (mDrawTickLabelOnPath) {
						// float a = (float) (convertValueToAngle(i));
						canvas.save(Canvas.MATRIX_SAVE_FLAG);
						canvas.rotate((float) degAngle, centerX, centerY);
						canvas.drawText(
								text,
								centerX,
								(float) (centerY - (longRadius + mTickTextSize * 0.5f)),
								paintTicks);
						canvas.restore();
					} else {
						canvas.drawText(
								text,
								Math.round(centerX
										- (float) ((shortRadius - mTickColor * 0.5f) * cosValue)),
								Math.round(centerY
										- (float) ((shortRadius - mTickColor * 0.5f) * sinValue)),
								paintTicks);
					}
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
