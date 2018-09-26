package cn.codeguy.easyupdate.progressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cn.codeguy.easyupdate.R;

import static android.graphics.Canvas.ALL_SAVE_FLAG;


/**
 * 圆环百分比
 * Created by fred on 2017/4/8.
 */

public class DKCircleView extends View {
    Paint paint;
    Paint paintArc;
    Paint paintText;
    Paint paintText2;
    boolean drawFg;//防止重复绘制，导致value增加两倍

    private int value;
    private boolean clockwise;
    private boolean useCenter;
    private boolean fill;
    private int valueChange;
    private int valueSize = 24;

    private int circleR = 45;
    private int paintWidth = 8;
    private int paintColor = Color.parseColor("#e5e5e5");
    private int paint1Color = Color.parseColor("#AA87F7");
    private int paddingTop;
    private int paddingBottom;
    private int paddingLeft;
    private int paddingRight;

    private int startAngle = 270;
    private RectF oval = new RectF();                     //圆圈的RectF对象
    private Rect rectText = new Rect();                     //文本的RectF对象
    private RectF rectFText = new RectF(rectText);
    private PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private String text;


    public DKCircleView(Context context) {
        this(context, null);
    }

    public DKCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DKCircleView);
        for (int i = 0; i < array.getIndexCount(); i++) {
            int i1 = array.getIndex(i);
            if (i1 == R.styleable.DKCircleView_value) {
                value = array.getInteger(R.styleable.DKCircleView_value, 0);

            } else if (i1 == R.styleable.DKCircleView_circleR) {
                circleR = array.getInteger(R.styleable.DKCircleView_circleR, 10);

            } else if (i1 == R.styleable.DKCircleView_paintWidth) {
                paintWidth = array.getInteger(R.styleable.DKCircleView_paintWidth, 10);

            } else if (i1 == R.styleable.DKCircleView_paintColor) {
                paint1Color = array.getColor(R.styleable.DKCircleView_paintColor, Color.parseColor("#666666"));
                paintColor = Color.parseColor("#FCFCFC");
            } else if (i1 == R.styleable.DKCircleView_valueSize) {
                valueSize = array.getInteger(R.styleable.DKCircleView_valueSize, 20);
            }
        }
        array.recycle();
        paint = new Paint();
        paintArc = new Paint();

        paintText = new Paint();
//        paintText2 = new Paint();


        init();
//        paintText2.setColorFilter(
//                new PorterDuffColorFilter(paint1Color,PorterDuff.Mode.SRC_ATOP));


//        new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP))
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (!drawFg){
        drawFg = true;
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        int centreX = getWidth() / 2; //获取圆心的x坐标
        int centreY = getHeight() / 2; //获取圆心的y坐标

//      int radius = (int) (centre - roundWidth/2); //圆环的半径
        paint.setColor(paintColor);
        paint.setStrokeWidth(paintWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setDither(true);//防抖动
        canvas.drawCircle(centreX, centreY, circleR, paint);


        paintArc.setColor(paint1Color);
        paintArc.setStrokeWidth(paintWidth);
        paintArc.setStyle(fill ? Paint.Style.FILL : Paint.Style.STROKE);
        paintArc.setStrokeCap(Paint.Cap.ROUND);
        paintArc.setAntiAlias(true);
        paintArc.setDither(true);//防抖动
        oval.left = centreX - circleR;                              //左边
        oval.top = centreY - circleR;                                   //上边
        oval.right = centreX + circleR;                             //右边
        oval.bottom = centreY + circleR;                                //下边
        float v = Float.valueOf(valueChange);
        canvas.drawArc(oval, startAngle, (clockwise ? 3.6f : -3.6f) * v, true, paintArc);


        paintText.setColor(paint1Color);
        paintText.setStrokeWidth(paintWidth);
        paintText.setStyle(fill ? Paint.Style.FILL : Paint.Style.STROKE);
        paintText.setStrokeCap(Paint.Cap.ROUND);
        paintText.setAntiAlias(true);
        paintText.setDither(true);//防抖动

        paintText.setStrokeWidth(0);
        paintText.setTextSize(valueSize);
        paintText.setTypeface(Typeface.DEFAULT_BOLD);


        text = valueChange + "%";
        paintText.getTextBounds(text, 0, text.length(), rectText);


        int saveLayerCount = canvas.saveLayer(0, 0, 400, 400, paintText2, ALL_SAVE_FLAG);
        canvas.drawText(text, centreX - rectText.width() / 2, centreY + valueSize / 2, paintText);

//        paintText2.setColor();
//        paintText2.setStrokeWidth(paintWidth);
//        paintText2.setAntiAlias(true);
//        paintText2.setDither(true);//防抖动
//        paintText2.setStyle(Paint.Style.FILL);
//        paintText2.setFilterBitmap(true);
//
//
        rectText.left = centreX - 200;                              //左边
        rectText.right = centreX + 200;
        rectText.top = centreY - 200;                                   //上边//右边
        rectText.bottom = centreY + 200;
//      canvas.drawRect(rectText, paintText2);
        rectFText.set(rectText);
//      canvas.drawBitmap(backBitmap, 0, 0, paintText2);// 绘制目标图
        paintText2.setXfermode(mXfermode);
        canvas.drawArc(rectFText, startAngle, (clockwise ? 3.6f : -3.6f) * v, true, paintText2);
//      canvas.drawRect(mDynamicRect, paintText2);   //绘制源图
        paintText2.setXfermode(null);         //清除混排模式
        canvas.restoreToCount(saveLayerCount);    //恢复保存的图层
//        invalidate();    //重绘
        checkValue();

//      }
    }

    /* @Override
     protected void onDraw(Canvas canvas) {
         super.onDraw(canvas);
         int saveLayerCount = canvas.saveLayer(0, 0, 400, 400, paintText2, Canvas.ALL_SAVE_FLAG);
         canvas.drawBitmap(backBitmap, 0, 0, paintText2);// 绘制目标图
         paintText2.setXfermode(mXfermode);    //设置混排模式
         canvas.drawRect(mDynamicRect, paintText2);   //绘制源图
         paintText2.setXfermode(null);         //清除混排模式
         canvas.restoreToCount(saveLayerCount);    //恢复保存的图层
         // 改变Rect区域，假如
         mCurTop -= 2;
         if (mCurTop <= 0) {
             mCurTop = mBitH;
         }
         mDynamicRect.top = mCurTop;
         invalidate();    //重绘
     }*/
    private void init() {
        //画笔初始化：
        paintText2 = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paintText2.setFilterBitmap(true);
        paintText2.setDither(true);
        paintText2.setColor(Color.WHITE);
        //背部图片的初始化
    }


    private void checkValue() {
        if (valueChange == Integer.valueOf(value)) {
        } else {
            //如果是减去一般都是变化中的值大于最大值的
            if (valueChange > Integer.valueOf(value)) {
                if (valueChange - 1 == Integer.valueOf(value)) {
                    valueChange--;
                    invalidate();

                } else {
                    valueChange -= 2;
                    invalidate();

                }

            } else {
                if (valueChange + 1 == Integer.valueOf(value)) {
                    valueChange++;
                    invalidate();

                } else {
                    valueChange += value - valueChange;
                    invalidate();

                }
            }
        }
    }

    private void setPaintColor(int paintColor, int paintWidth) {
        this.paintColor = paintColor;
        this.paintWidth = paintWidth;

    }

    private void setCircleR(int circleR) {
        this.circleR = circleR;
    }

    public DKCircleView setValue(int value) {
        this.value = value;

        text = valueChange + "%";

        paintText.getTextBounds(text, 0, text.length(), rectText);


        return this;
    }

    public void setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
    }

    public void setUseCenter(boolean useCenter) {
        this.useCenter = useCenter;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public void restart(int value) {
        valueChange = this.value;
        this.value = value;
        invalidate();

    }

    public void clear() {
        value = 0;
        valueChange = 0;
        startAngle = 270;
        drawFg = true;
        postInvalidate();

    }


}
