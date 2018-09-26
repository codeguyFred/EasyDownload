package cn.codeguy.easyupdate.progressview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import static android.graphics.Paint.Join.ROUND;

/**
 * Created by fred
 * Date: 2018/9/21.
 * Time: 14:28
 * classDescription:√ x 动画显示这两个标记
 */
public class StateView extends View {
    Paint paint = new Paint();
    Path path = new Path();
    Path mDst = new Path();
    PathMeasure pathMeasure;
    //进度
    private float mAnimatorValue;
    //动画
    ValueAnimator valueAnimator;

    public StateView(Context context) {
        super(context);
        initPaint();
    }

    public StateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public StateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(DisplayUtil.dip2px(getContext(), 5));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
//      paint.setPathEffect(new CornerPathEffect(DisplayUtil.dip2px(getContext(), 5)));
        paint.setStrokeJoin(ROUND);
        //初始化测量工具传入完整路径
        path.addCircle(
                DisplayUtil.dip2px(getContext(), 50),
                DisplayUtil.dip2px(getContext(), 50),
                DisplayUtil.dip2px(getContext(), 50), Path.Direction.CW);
//        initPathYes();
        initPathNo();
        mDst.reset();
        mDst.lineTo(0, 0);
        pathMeasure = new PathMeasure(path, false);
        //实例化动画
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        //平滑过渡
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            /**
             *
             * @param valueAnimator  0~1
             */
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAnimatorValue = (float) valueAnimator.getAnimatedValue();
                //实时路径的百分百比长度0->pathMeasure.getLength()
                float currentLength = pathMeasure.getLength() * mAnimatorValue;
                //将对应长度currentLength的路径获取并传递给path
                pathMeasure.getSegment(0, currentLength, mDst, true);
                //重绘
                invalidate();
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                pathMeasure.getSegment(0, pathMeasure.getLength(), mDst, true);
                //获取下一段路径
                pathMeasure.nextContour();
               /* if (pathMeasure.getLength() == 0) {
                    //如果全部绘制完成则重置
                    mDst.reset();
                    mDst.lineTo(0, 0);
                    pathMeasure.setPath(path, false);
                }*/

            }
        });

        valueAnimator.setDuration(500);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawPath(mDst, paint);
    }

    private void initPathNo() {
        path.moveTo(DisplayUtil.dip2px(getContext(), 25),
                DisplayUtil.dip2px(getContext(), 25));
        path.lineTo(
                DisplayUtil.dip2px(getContext(), 75),
                DisplayUtil.dip2px(getContext(), 75)
        );

        path.moveTo(DisplayUtil.dip2px(getContext(), 75),
                DisplayUtil.dip2px(getContext(), 25));

        path.lineTo(
                DisplayUtil.dip2px(getContext(), 25),
                DisplayUtil.dip2px(getContext(), 75)
        );

    }

    private void initPathYes() {
        path.moveTo(DisplayUtil.dip2px(getContext(), 25), DisplayUtil.dip2px(getContext(), 50));
        path.lineTo(
                DisplayUtil.dip2px(getContext(), 50),
                DisplayUtil.dip2px(getContext(), 75)
        );

        path.lineTo(
                DisplayUtil.dip2px(getContext(), 75),
                DisplayUtil.dip2px(getContext(), 25)
        );

    }
}
