package com.destroy.ddopagoihanda;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MyView extends View {

    private int score;
    private Context context;
    private int color;

    public MyView(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if(attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.AAA);
            this.color = arr.getColor(R.styleable.AAA_customColor, Color.YELLOW);
            this.score = arr.getInt(R.styleable.AAA_customScore, 0);
        }
    }

    public void setScore(int score) {
        this.score = score;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.alpha(Color.CYAN));

        RectF rect = new RectF(15, 15, 70, 70);

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(15);
        canvas.drawArc(rect, 0, 360, false, paint);

        float endAngle = 360*score/100;
        if(color == 0) {
            paint.setColor(Color.RED);
        }
        else {
            paint.setColor(color);
        }
        canvas.drawArc(rect, -90, endAngle, false, paint);
    }
}
