package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cetnaline.findproperty.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diaoqf on 2016/7/14.
 */
public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    //是否在绘制状态
    private boolean is_drawing;

    private SurfaceHolder surfaceHolder = null;

    private Paint paint;

    private List<PointF> screenPath;

    private OnDrawListener onDrawListener;

    public DrawView(Context context) {
        super(context);
        init(null, context);
    }


    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }



    private void init(AttributeSet attrs, Context context) {
        is_drawing = false;
        screenPath = new ArrayList<>();

        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(ContextCompat.getColor(context, R.color.btRed));
        paint.setAntiAlias(true);

//        setOnClickListener(view -> {
//            Toast.makeText(context, "123", Toast.LENGTH_SHORT).show();
//        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                is_drawing = true;
                clear();
                screenPath.add(new PointF(event.getX(),event.getY()));
                break;
            case MotionEvent.ACTION_MOVE:
                screenPath.add(new PointF(event.getX(),event.getY()));
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                is_drawing = false;
                if (onDrawListener != null) {
                    onDrawListener.finished();
                }
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (screenPath.size() > 1) {
            for (int i=1;i<screenPath.size();i++){
                canvas.drawLine(screenPath.get(i-1).x,screenPath.get(i-1).y,screenPath.get(i).x,screenPath.get(i).y,paint);
            }
        }else {
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
    }

    //清除绘图和坐标数组
    public void clear(){
        screenPath.clear();
        invalidate();
    }

    public List<PointF> getScreenPath() {
        return screenPath;
    }

    public void setOnDrawListener(OnDrawListener onDrawListener) {
        this.onDrawListener = onDrawListener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    public interface OnDrawListener{
        void finished();
    }
}
