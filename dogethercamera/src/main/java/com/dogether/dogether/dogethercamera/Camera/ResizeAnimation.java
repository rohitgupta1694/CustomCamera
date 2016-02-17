package com.dogether.dogether.dogethercamera.Camera;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by dogether on 12/2/16.
 */
public class ResizeAnimation extends Animation {
    public static final String TAG = ResizeAnimation.class.getSimpleName();

    final int mStartLength;
    final int mFinalLength;
    final boolean mIsPortrait;
    final View mView;

    public ResizeAnimation(@NonNull View view, final ImageParameters imageParameters) {
        mIsPortrait = imageParameters.isPortrait();
        mView = view;
        mStartLength = mIsPortrait ? mView.getHeight() : mView.getWidth();
        mFinalLength = imageParameters.getAnimationParameter();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        Log.d(TAG, "Start: " + mStartLength + " final: " + mFinalLength + " InterpolatedTime: " + interpolatedTime);
        int newLength = (int) (mStartLength + (mFinalLength - mStartLength) * interpolatedTime);
        Log.d(TAG, "New Length: " + newLength);
        if (mIsPortrait) {
            mView.getLayoutParams().height = newLength - 24;
        } else {
            mView.getLayoutParams().width = newLength - 24;
        }
        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
