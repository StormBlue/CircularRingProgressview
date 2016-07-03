package com.bluestrom.gao.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bluestrom.gao.circularringpercentageview.R;

public class CircularRingPercentageView extends View {
    private final String TAG = this.getClass().getSimpleName();

    private Paint paint;
    private int progress = 0;
    private int finishedStrokeColor;
    private int unfinishedStrokeColor;
    private int rotateX, rotateY;// 圆心位置
    private int width, height;// view的宽与高
    private int lengthDValue;// view宽高差值
    private float strokeWidth;// 刻度的宽度
    private float ringWidth;// 刻度的长度
    private float ringOutRadius;// 圆环外层半径
    private int max;// 最大值
    private int scaleNumbers;// 刻度
    private DrawMode drawMode;// 绘制模式

    // 参数默认值
    private final int default_max = 100;
    private final int default_finished_color = Color.rgb(33, 228, 116);
    private final int default_unfinished_color = getResources().getColor(R.color.defaultUnfinish);
    private final float default_stroke_width = 2;
    private final float default_ring_width = 40;
    private final int default_scale_numbers = 200;
    private final DrawMode default_draw_mode = DrawMode.QUARTER;

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_STROKE_WIDTH = "stroke_width";
    private static final String INSTANCE_RING_WIDTH = "ring_width";
    private static final String INSTANCE_SCALE_NUMBERS = "scale_numbers";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_DRAW_MODE = "draw_mode";

    public CircularRingPercentageView(Context context) {
        this(context, null);
    }

    public CircularRingPercentageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CircularRingPercentageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularRing);
        initByAttributes(mTypedArray);
        initView();
        mTypedArray.recycle();
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedStrokeColor = attributes.getColor(R.styleable.CircularRing_arc_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.CircularRing_arc_unfinished_color, default_unfinished_color);
        setScaleNumbers(attributes.getInt(R.styleable.CircularRing_arc_scale_numbers, default_scale_numbers));
        setMax(attributes.getInt(R.styleable.CircularRing_arc_max, default_max));
        setProgress(attributes.getInt(R.styleable.CircularRing_arc_progress, progress));
        drawMode = DrawMode.valueOf(attributes.getInt(R.styleable.CircularRing_arc_draw_mode, default_draw_mode.nativeInt));
        strokeWidth = attributes.getDimension(R.styleable.CircularRing_arc_stroke_width, default_stroke_width);
        ringWidth = attributes.getDimension(R.styleable.CircularRing_arc_ring_width, default_ring_width);
    }

    public void initView() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        lengthDValue = (width - height) / 2;
        rotateX = width / 2;
        rotateY = height / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 圆环左端坐标与view最左侧的间距
        float leftD = lengthDValue > 0f ? lengthDValue : 0f;
        ringOutRadius = rotateX - leftD;

        for (int j = 0; j < scaleNumbers; j++) {
            canvas.save();// 保存当前画布
            paint.setColor(finishedStrokeColor);
            if (!checkColour(j)) {
                paint.setColor(unfinishedStrokeColor);
            }
            canvas.rotate(360f / scaleNumbers * j, rotateX, rotateY);
            canvas.drawLine(leftD, rotateY, leftD + ringWidth, rotateY, paint);
            canvas.restore();
        }

    }

    private boolean checkColour(int i) {
        boolean result = false;
        switch (getDrawMode()) {
            case COMPLETE:
                if (progress > 0 && i <= ((float) progress / max * scaleNumbers)) {
                    result = true;
                }
                break;
            case QUARTER:
                if (progress > 0) {
                    result = true;
                    float progressPercent = (float) progress / max;
                    int quraterScaleNumbers = scaleNumbers / 4;
                    // 上部空白
                    if (i > quraterScaleNumbers * progressPercent && i < (scaleNumbers / 2 - progressPercent * quraterScaleNumbers)) {
                        result = false;
                    }
                    // 下部空白
                    if (i > (scaleNumbers / 2 + progressPercent * quraterScaleNumbers) && i < (scaleNumbers - progressPercent * quraterScaleNumbers)) {
                        result = false;
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        this.invalidate();
    }

    public int getFinishedStrokeColor() {
        return finishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        this.finishedStrokeColor = finishedStrokeColor;
        this.invalidate();
    }

    public int getUnfinishedStrokeColor() {
        return unfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        this.unfinishedStrokeColor = unfinishedStrokeColor;
        this.invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getRingWidth() {
        return ringWidth;
    }

    public void setRingWidth(float ringWidth) {
        if (ringWidth > 0) {
            this.ringWidth = ringWidth;
            invalidate();
        }
    }

    public int getScaleNumbers() {
        return scaleNumbers;
    }

    public void setScaleNumbers(int scale) {
        if (scale > 0) {
            this.scaleNumbers = scale;
            invalidate();
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (this.progress < 0) {
            this.progress = 0;
        }
        if (this.progress > getMax()) {
            this.progress = getMax();
        }
        invalidate();
    }

    public enum DrawMode {

        COMPLETE(0),

        QUARTER(1);

        DrawMode(int ni) {
            nativeInt = ni;
        }

        final int nativeInt;

        public static DrawMode valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return values()[ordinal];
        }
    }

    public DrawMode getDrawMode() {
        return drawMode;
    }

    public void setDrawMode(DrawMode drawMode) {
        if (getDrawMode().equals(drawMode)) {
            return;
        }
        this.drawMode = drawMode;
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_STROKE_WIDTH, getStrokeWidth());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());
        bundle.putFloat(INSTANCE_RING_WIDTH, getRingWidth());
        bundle.putInt(INSTANCE_SCALE_NUMBERS, getScaleNumbers());
        bundle.putInt(INSTANCE_DRAW_MODE, getDrawMode().nativeInt);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            strokeWidth = bundle.getFloat(INSTANCE_STROKE_WIDTH);
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            setMax(bundle.getInt(INSTANCE_MAX));
            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            ringWidth = bundle.getFloat(INSTANCE_RING_WIDTH);
            scaleNumbers = bundle.getInt(INSTANCE_SCALE_NUMBERS);
            setDrawMode(DrawMode.valueOf(bundle.getInt(INSTANCE_DRAW_MODE)));
            initView();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
