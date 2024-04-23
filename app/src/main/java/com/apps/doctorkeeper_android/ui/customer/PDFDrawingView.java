package com.apps.doctorkeeper_android.ui.customer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFDrawingView extends View {
    private Bitmap bitmapPDF;
    //배경이 되는 PDF 비트맵

    private Bitmap bitmapDraw;
    //선을 그리는 비트맵

    private static final float TOUCH_TOLERANCE = 4;
    int xx;
    int yy;

    private Path path;


    private Paint paint;

    private boolean drawMode;
    private float x, y;
    private float penSize = 10;


    private float eraserSize = 50;
    private Canvas canvasOn;
    private boolean eraserMode=false;

    public boolean isEraserMode() {
        return eraserMode;
    }

    public void setEraserMode(boolean eraserMode) {
        this.eraserMode = eraserMode;
    }


    public PDFDrawingView(Context context, int xx, int yy) {
        super(context);

        this.xx = xx;
        this.yy = yy;

        init();
    }

    public PDFDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(penSize);
        drawMode = false;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }


    public void setDrawMode(boolean drawMode) {
        this.drawMode = drawMode;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmapPDF = bitmap;
        try {
            bitmapDraw = Bitmap.createBitmap(xx, yy, Bitmap.Config.ARGB_8888);
            canvasOn = new Canvas(bitmapDraw);
            canvasOn.drawColor(Color.TRANSPARENT);
        } catch (Exception e) {

        }

        invalidate(); // Request a redraw
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bitmapPDF != null) {
            canvas.drawBitmap(bitmapPDF, 0, 0, null);
        }

        if (drawMode) {
            canvas.drawBitmap(bitmapDraw, 0, 0, null);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Get the suggested dimensions from the parent
        setMeasuredDimension(xx, yy);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // Implement your custom layout logic here

    }

    private void touchStart(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        this.x = x;
        this.y = y;
        canvasOn.drawPath(path, paint);
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - this.x);
        float dy = Math.abs(y - this.y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(this.x, this.y, (x + this.x) / 2, (y + this.y) / 2);
            this.x = x;
            this.y = y;
        }
        canvasOn.drawPath(path, paint);
    }

    private void touchUp() {
        path.lineTo(x, y);
        canvasOn.drawPath(path, paint);
        path.reset();
        if (eraserMode) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (drawMode) {
            getParent().requestDisallowInterceptTouchEvent(true);
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (eraserMode) {
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    } else {
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                    }

                    touchStart(x, y);
                    invalidate();

                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(x, y);

                    if(eraserMode){
                        path.lineTo(this.x, this.y);
                        path.reset();
                        path.moveTo(x, y);
                    }


                    canvasOn.drawPath(path, paint);
                    invalidate();

                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    invalidate();
                    break;
                default:
                    break;
            }

        } else {
            return super.onTouchEvent(event);
        }


        return true;

    }

    public void initializeEraser() {
        eraserMode = true;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(eraserSize);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void initialize() {
        eraserMode = false;
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(penSize);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }
}
