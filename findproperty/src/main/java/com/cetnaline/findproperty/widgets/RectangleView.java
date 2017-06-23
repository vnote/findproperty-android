package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

public class RectangleView extends ImageView {
  
    private Paint paint;

    public RectangleView(Context context) {
        this(context, null);
    }    
    
    public RectangleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }    
    
    public RectangleView(Context context, AttributeSet attrs, int defStyle) {    
        super(context, attrs, defStyle);   
        paint  = new Paint();
    }
    
    /** 
     * 绘制圆角矩形图片 
     * @author se7en 
     */  
    @Override    
    protected void onDraw(Canvas canvas) {
    
        Drawable drawable = getDrawable();


        if (null != drawable) {
            Bitmap bitmap = null;
            if (drawable instanceof GlideBitmapDrawable){
                bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
            }else if (drawable instanceof BitmapDrawable){
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            }
            if (bitmap==null){
                super.onDraw(canvas);
                return;
            }
            Bitmap b = getRoundBitmap(bitmap, 30);
            final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
            final Rect rectDest = new Rect(0, 0, getWidth(), getHeight());
            paint.reset();    
            canvas.drawBitmap(b, rectSrc, rectDest, paint);    
        } else {
            super.onDraw(canvas);    
        }    
    }    
  
    /** 
     * 获取圆角矩形图片方法 
     * @param bitmap 
     * @param roundPx 这个属性是设置弧度，一般设置为14，也可以结合实际效果。本例中是30，为了实现明显一点。 
     * @return se7en 
     */  
    private Bitmap getRoundBitmap(Bitmap bitmap, int roundPx) {    
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),    
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);    
            
        final int color = 0xff424242;  
         
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());    
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);    
        canvas.drawARGB(0, 0, 0, 0);    
        paint.setColor(color);    

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);    
        return output;        
    }    
}