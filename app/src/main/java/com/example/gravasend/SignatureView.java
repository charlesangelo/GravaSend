package com.example.gravasend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SignatureView extends View {

    private static final float STROKE_WIDTH = 5f;
    private static final int STROKE_COLOR = Color.BLACK;

    private Paint paint;
    private Path path;
    private Bitmap signatureBitmap;
    private Canvas signatureCanvas;

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(STROKE_COLOR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);

        path = new Path();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        signatureBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        signatureCanvas = new Canvas(signatureBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(signatureBitmap, 0, 0, paint);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                // Save the drawn path to the bitmap
                signatureCanvas.drawPath(path, paint);
                path.reset();
                break;
            default:
                return false;
        }

        invalidate(); // Redraw the view
        return true;
    }

    public void clearSignature() {
        signatureBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    public Bitmap getSignatureBitmap() {
        return signatureBitmap;
    }

    public void clearCanvas() {
    }
}
