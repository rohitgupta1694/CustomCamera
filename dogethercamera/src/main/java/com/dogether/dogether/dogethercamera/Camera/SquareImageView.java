package com.dogether.dogether.dogethercamera.Camera;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by dogether on 12/2/16.
 */
public class SquareImageView extends ImageView {
    public static final String TAG = SquareImageView.class.getSimpleName();

    private static final double ASPECT_RATIO = 3.0 / 4.0;

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        Log.d(TAG, "Width: " + width + "");
        Log.d(TAG,"Height: " + height+"");
        final boolean isPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (isPortrait) {
            if (width > height * ASPECT_RATIO) {
                width = (int) (height + 0.5);
            } else {
                height = (int) (width  + 0.5);
                Log.d(TAG,"Height: " + height+"");
            }
        } else {
            if (height > width * ASPECT_RATIO) {
                height = (int) (width + 0.5);
            } else {
                width = (int) (height + 0.5);
            }
        }
        int squareLen = width > height ? width : height;
        Log.d(TAG, "Square Dimension: " + squareLen);
        setMeasuredDimension(squareLen, squareLen);
    }
}