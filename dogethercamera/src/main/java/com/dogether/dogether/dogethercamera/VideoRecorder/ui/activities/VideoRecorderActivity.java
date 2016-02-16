package com.dogether.dogether.dogethercamera.VideoRecorder.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.dogether.dogether.dogethercamera.R;
import com.dogether.dogether.dogethercamera.VideoRecorder.ui.fragments.VideoRecorderFragment;

/**
 * Created by dogether on 16/2/16.
 */
public class VideoRecorderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.dogethercamera__CameraFullScreenTheme);
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.dogethercamera__video_recorder_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.video_recorder_fragment_container, VideoRecorderFragment.newInstance(), VideoRecorderFragment.TAG)
                    .commit();
        }
    }
}
