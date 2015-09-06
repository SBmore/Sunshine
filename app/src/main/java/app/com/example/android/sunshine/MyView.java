package app.com.example.android.sunshine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Steven on 05/09/2015.
 */
public class MyView extends View {
    public MyView(Context context) {
        super(context);
    }
    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
    }

    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
        int hSpecMode = MeasureSpec.getMode(hMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(hMeasureSpec);
        int wSpecMode = MeasureSpec.getMode(wMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(wMeasureSpec);
        int myHeight = hSpecSize;
        int myWidth = wSpecSize;

        if (hSpecMode == MeasureSpec.EXACTLY) {
            myHeight = hSpecSize;
        } else if (hSpecMode == MeasureSpec.AT_MOST){
            //Wrap Content
        }

        if (wSpecMode == MeasureSpec.EXACTLY) {
            myWidth = wSpecSize;
        } else if (wSpecMode == MeasureSpec.AT_MOST){
            //Wrap Content
        }

        setMeasuredDimension(myWidth, myHeight);
    }

    @Override
     protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(2);
        canvas.drawCircle(10, 10, 10, paint);


    }
}
