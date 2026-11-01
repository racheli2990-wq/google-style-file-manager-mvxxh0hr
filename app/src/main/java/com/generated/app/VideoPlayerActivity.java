package com.generated.app;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.Locale;

public class VideoPlayerActivity extends Activity {

    private VideoView videoView;
    private ImageView btnPlayPause;
    private SeekBar sbProgress;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private View layoutControls;

    private final Handler handler = new Handler();
    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            if (videoView.isPlaying()) {
                int current = videoView.getCurrentPosition();
                sbProgress.setProgress(current);
                tvCurrentTime.setText(formatTime(current));
                handler.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.video_view);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        sbProgress = findViewById(R.id.sb_progress);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        layoutControls = findViewById(R.id.layout_controls);

        TextView tvTitle = findViewById(R.id.tv_video_title);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String path = getIntent().getStringExtra("file_path");
        if (path != null) {
            File file = new File(path);
            tvTitle.setText(file.getName());

            videoView.setVideoURI(Uri.fromFile(file));
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    int duration = videoView.getDuration();
                    sbProgress.setMax(duration);
                    tvTotalTime.setText(formatTime(duration));
                    videoView.start();
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                    handler.post(updateProgressTask);
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                }
            });
        }

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                } else {
                    videoView.start();
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                    handler.post(updateProgressTask);
                }
            }
        });

        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        findViewById(R.id.video_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutControls.getVisibility() == View.VISIBLE) {
                    layoutControls.setVisibility(View.GONE);
                } else {
                    layoutControls.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String formatTime(int ms) {
        int seconds = (ms / 1000) % 60;
        int minutes = (ms / (1000 * 60)) % 60;
        int hours = ms / (1000 * 60 * 60);
        if (hours > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateProgressTask);
    }
}
