package com.dogether.dogether.dogethercamera.VideoRecorder.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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
public class VideoRecorderFragment extends Fragment implements View.OnClickListener {

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

    public static Fragment newInstance() {
        return new VideoRecorderFragment();
    }

    public VideoRecorderFragment() {
        // need default constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.dogethercamera__fragment_main, container, false);
        mCameraView = (CameraGLView)rootView.findViewById(R.id.cameraView);
        mCameraView.setVideoSize(1280, 720);
        mCameraView.setOnClickListener(this);
//        mScaleModeView = (TextView)rootView.findViewById(R.id.scalemode_textview);
//        updateScaleModeText();
        mRecordButton = (ImageButton)rootView.findViewById(R.id.record_button);
        mRecordButton.setOnClickListener(this);
        return rootView;
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

}
