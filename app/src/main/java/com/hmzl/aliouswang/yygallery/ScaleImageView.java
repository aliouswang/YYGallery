package com.hmzl.aliouswang.yygallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class ScaleImageView extends android.support.v7.widget.AppCompatImageView
                implements IRatio{
    private float scale = 1.0f;

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray t = context.obtainStyledAttributes(attrs,
                R.styleable.ScaleImageView, 0, 0);
        scale = t.getFloat(R.styleable.ScaleImageView_siv_scale, 1.0f);
        t.recycle();
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float width = MeasureSpec.getSize(widthMeasureSpec);
        float height = width * scale;
        setMeasuredDimension((int)width, (int)height);
    }

    @Override
    public float getRatio() {
        return this.scale;
    }

    @Override
    public void setRatio(float ratio) {
        this.scale = ratio;
        requestLayout();
        invalidate();
    }
}