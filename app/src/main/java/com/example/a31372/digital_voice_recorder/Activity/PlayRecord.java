package com.example.a31372.digital_voice_recorder.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a31372.digital_voice_recorder.R;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayRecord extends Activity {

    private MediaPlayer mediaPlayer;//媒体播放器
    private Button playButton;
    private TextView name;
    private String path;
    private boolean isCellPlay;/*在挂断电话的时候，用于判断是否为是来电时中断*/
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private int currentPosition;//当前音乐播放的进度
    private SeekBar seekBar;
    private Timer timer = new Timer();

    private static final String TAG = "MediaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_record);

        final Intent intent = getIntent();
        path = intent.getStringExtra("recordPath");

        name = (TextView) findViewById(R.id.name);
        name.setText(new File(path).getName());

        //实例化媒体播放器
        mediaPlayer = new MediaPlayer();

        //监听滚动条事件
        seekBar = (SeekBar) findViewById(R.id.playSeekBar);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());

        // 监听[播放或暂停]事件
        playButton= (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new PalyListener());


        //监听来电事件
        TelephonyManager phoneyMana = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneyMana.listen(new myPhoneStateListener(),PhoneStateListener.LISTEN_CALL_STATE);

    }
    /*销毁时释资源*/
    @Override
    protected void onDestroy() {
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    /*播放或暂停事件处理*/
    private class PalyListener implements View.OnClickListener {
        public void onClick(View v) {
            if(playButton.getText().toString().equals("播放中"))
            {
                currentPosition = mediaPlayer.getCurrentPosition();//记录播放的位置
                mediaPlayer.pause();//暂停状态
                playButton.setText("暂停");
                timer.cancel();
                timer = null;
            }
            else{
                mediaPlayer.reset();
                play();
            }
        }
    }

    /*播放处理*/
    private void play() {
        File media = new File(path);
        Log.i(TAG, media.getAbsolutePath());
        if(media.exists())
        {
            try {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音频类型
                mediaPlayer.setDataSource(media.getAbsolutePath());//设置mp3数据源
                mediaPlayer.prepareAsync();//数据缓冲
                /*监听缓存 事件，在缓冲完毕后，开始播放*/
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        mp.seekTo(currentPosition);
                        playButton.setText("播放中");
                        seekBar.setMax(mediaPlayer.getDuration());
                    }
                });
                //监听播放时回调函数
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            }
                        });
                    }
                };
                if (timer == null){
                    timer = new Timer();
                }
                timer.schedule(timerTask,0,50);       //1秒执行一次
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "playError", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                System.out.println(e);
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "fileError", Toast.LENGTH_LONG).show();
        }
    }

    /*来电事件处理*/
    private class myPhoneStateListener extends PhoneStateListener
    {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://来电，应当停止音乐
                    if(mediaPlayer.isPlaying() && playButton.getText().toString().equals("播放")){
                        currentPosition = mediaPlayer.getCurrentPosition();//记录播放的位置
                        mediaPlayer.stop();
                        isCellPlay = true;//标记这是属于来电时暂停的标记
                        playButton.setText("暂停");
                        timer.purge();//移除定时器任务;
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE://无电话状态
                    if(isCellPlay){
                        isCellPlay = false;
                        mediaPlayer.reset();
                        play();
                    }
                    break;
            }
        }
    }

    /*进度条处理*/
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }
        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            mediaPlayer.seekTo(seekBar.getProgress());
        }
    }


}
