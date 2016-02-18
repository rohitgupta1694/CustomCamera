package com.dogether.dogether.dogethercamera.VideoRecorder.ui.fragments;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;

import com.dogether.dogether.dogethercamera.ImageParameters;
import com.dogether.dogether.dogethercamera.ResizeAnimation;
import com.dogether.dogether.dogethercamera.R;
import com.dogether.dogether.dogethercamera.VideoRecorder.utils.CameraGLView;
import com.dogether.dogether.dogethercamera.VideoRecorder.utils.encoder.MediaAudioEncoder;
import com.dogether.dogether.dogethercamera.VideoRecorder.utils.encoder.MediaEncoder;
import com.dogether.dogether.dogethercamera.VideoRecorder.utils.encoder.MediaMuxerWrapper;
import com.dogether.dogether.dogethercamera.VideoRecorder.utils.encoder.MediaVideoEncoder;

import java.io.IOException;

/**
 * Created by dogether on 16/2/16.
 */
public class VideoRecorderFragment extends Fragment implements View.OnClickListener{

    private static final boolean DEBUG = false;	// TODO set false on release
    public static final String TAG = "VideoRecorderFragment";

    /**
     * for camera preview display
     */
    private CameraGLView mCameraView;
    /**
     * for scale mode display
     */
//    private TextView mScaleModeView;
    /**
     * button for start/stop recording
     */
    private ImageButton mRecordButton;
    /**
     * muxer for audio/video recording
     */
    private MediaMuxerWrapper mMuxer;


    private ImageParameters mImageParameters;

    public static Fragment newInstance() {
        return new VideoRecorderFragment();
    }

    public VideoRecorderFragment() {
        // need default constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.dogethercamera__fragment_main, container, false);
        if (savedInstanceState == null) {
            mImageParameters = new ImageParameters();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCameraView = (CameraGLView)view.findViewById(R.id.cameraView);
        mCameraView.setVideoSize(1280, 720);
        mCameraView.setOnClickListener(this);
//        mScaleModeView = (TextView)rootView.findViewById(R.id.scalemode_textview);
//        updateScaleModeText();
        mRecordButton = (ImageButton)view.findViewById(R.id.record_button);
        mRecordButton.setOnClickListener(this);
        final View topCoverView = view.findViewById(R.id.top_cover_view);
        final View btnCoverView = view.findViewById(R.id.bottom_cover_view);
        mImageParameters.mIsPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (savedInstanceState == null) {
            ViewTreeObserver observer = mCameraView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
//                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mCameraView.getLayoutParams();
                    mImageParameters.mPreviewWidth = mCameraView.getViewWidth();
                    mImageParameters.mPreviewHeight = mCameraView.getViewHeight();

                    mImageParameters.mCoverWidth = mImageParameters.mCoverHeight
                            = mImageParameters.calculateCoverWidthHeight();

//                    layoutParams.width = mCameraView.getViewWidth();
//                    layoutParams.height = mCameraView.getViewHeight();
//                    mCameraView.setLayoutParams(layoutParams);
                    Log.d(TAG, mCameraView.getViewWidth() + "");
                    Log.d(TAG, mCameraView.getViewHeight() + "");
                   /* Log.d(TAG, mCameraView.getWidth() + "");
                    Log.d(TAG, mCameraView.getHeight() + "");*/


//                    Log.d(TAG, "parameters: " + mImageParameters.getStringValues());
//                    Log.d(TAG, "cover height " + topCoverView.getHeight());
                    resizeTopAndBtmCover(topCoverView, btnCoverView);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mCameraView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mCameraView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        } else {
            if (mImageParameters.isPortrait()) {
                topCoverView.getLayoutParams().height = mImageParameters.mCoverHeight;
                btnCoverView.getLayoutParams().height = mImageParameters.mCoverHeight;
                Log.d(TAG,"Top & Bottom Cover Height: " + mImageParameters.mCoverHeight+"");
            } else {
                topCoverView.getLayoutParams().width = mImageParameters.mCoverWidth;
                btnCoverView.getLayoutParams().width = mImageParameters.mCoverWidth;
                Log.d(TAG,"Top & Bottom Cover Width: " + mImageParameters.mCoverHeight+"");

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) Log.v(TAG, "onResume:");
        mCameraView.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.v(TAG, "onPause:");
        stopRecording();
        mCameraView.onPause();
        super.onPause();
    }

    private void resizeTopAndBtmCover( final View topCover, final View bottomCover) {
        ResizeAnimation resizeTopAnimation
                = new ResizeAnimation(topCover, mImageParameters);
        resizeTopAnimation.setDuration(800);
        resizeTopAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        topCover.startAnimation(resizeTopAnimation);

        ResizeAnimation resizeBtmAnimation
                = new ResizeAnimation(bottomCover, mImageParameters);
        resizeBtmAnimation.setDuration(800);
        resizeBtmAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        bottomCover.startAnimation(resizeBtmAnimation);
    }

    /**
     * method when touch record button
     */
    @Override
    public void onClick(final View view) {
        int i = view.getId();
        if (i == R.id.cameraView) {
            final int scale_mode = (mCameraView.getScaleMode() + 1) % 4;
            mCameraView.setScaleMode(scale_mode);
//            updateScaleModeText();

        } else if (i == R.id.record_button) {
            if (mMuxer == null)
                startRecording();
            else
                stopRecording();

        }
    }
/*
    private void updateScaleModeText() {
        final int scale_mode = mCameraView.getScaleMode();
        mScaleModeView.setText(
                scale_mode == 0 ? "scale to fit"
                        : (scale_mode == 1 ? "keep aspect(viewport)"
                        : (scale_mode == 2 ? "keep aspect(matrix)"
                        : (scale_mode == 3 ? "keep aspect(crop center)" : ""))));
    }*/

    /**
     * start resorcing
     * This is a sample project and call this on UI thread to avoid being complicated
     * but basically this should be called on private thread because prepareing
     * of encoder is heavy work
     */
    private void startRecording() {
        if (DEBUG) Log.v(TAG, "startRecording:");
        try {
            mRecordButton.setColorFilter(0xffff0000);	// turn red
            mMuxer = new MediaMuxerWrapper(".mp4");	// if you record audio only, ".m4a" is also OK.
            if (true) {
                // for video capturing
                new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCameraView.getVideoWidth(), mCameraView.getVideoHeight());
            }
            if (true) {
                // for audio capturing
                new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            }
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            mRecordButton.setColorFilter(0);
            Log.e(TAG, "startCapture:", e);
        }
    }

    /**
     * request stop recording
     */
    private void stopRecording() {
        if (DEBUG) Log.v(TAG, "stopRecording:mMuxer=" + mMuxer);
        mRecordButton.setColorFilter(0);	// return to default color
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
            // you should not wait here
        }
    }

    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder((MediaVideoEncoder)encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder(null);
        }
    };

/*    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;

        getCamera(mCameraID);
        startCameraPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // The surface is destroyed with the visibility of the SurfaceView is set to View.Invisible
    }*/

}
