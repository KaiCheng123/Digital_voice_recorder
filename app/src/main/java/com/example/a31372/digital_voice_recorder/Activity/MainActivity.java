package com.example.a31372.digital_voice_recorder.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a31372.digital_voice_recorder.Db.DBHelper;
import com.example.a31372.digital_voice_recorder.R;
import com.example.a31372.digital_voice_recorder.Record.AudioRecorder;
import com.example.a31372.digital_voice_recorder.Record.FileUtils;
import com.example.a31372.digital_voice_recorder.util.PowerRequest;
import com.githang.statusbar.StatusBarCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ImageButton stop;
    private ImageButton record;
    private ImageButton data;
    private TextView count;
    private TextView textView;
    int cnt = 0;
    private Timer timer = new Timer();
    private int flag_record = 0; //录音标志位
    private int flag_stop = 0; //录音结束标志位
    private int flag_pause = 0;//录音暂停标志位
    AudioRecorder audioRecorder;
    PowerRequest powerRequest = new PowerRequest();
    FileUtils fileUtils = new FileUtils();
    private Context context = this;

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        StatusBarCompat.setStatusBarColor(this, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        stop = (ImageButton) findViewById(R.id.stop_btn);
        record = (ImageButton) findViewById(R.id.record_btn);
        data = (ImageButton) findViewById(R.id.data_btn);
        count = (TextView) findViewById(R.id.count_brn);
        textView = (TextView) findViewById(R.id.text);
        //申请权限
        powerRequest.init_permission(getApplicationContext(),MainActivity.this);
        //创建文件夹
        fileUtils.createSDDir("record_test");

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //录音完成按钮监听事件
                switch (flag_stop){
                    case 1:
                        stop();     //结束
                        break;
                }
            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //录音按钮监听事件
                switch (flag_record){
                    case 0:
                        start();        //开始
                        break;
                    case 1:
                        pause();        //暂停
                        break;
                }
            }
        });

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //录音记录按钮监听事件
                Intent intent = new Intent(MainActivity.this,RecordListActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 当录音开始时进行的一系列操作
     */
    private void start() {
        String fileName = new SimpleDateFormat("MM月dd日 HH时mm分ss秒").format(System.currentTimeMillis());
        record.setBackgroundResource(R.drawable.pause);     //修改录音按钮图像
        flag_record = 1;
        stop.setBackgroundResource(R.drawable.stop2);     //修改录音结束图像
        flag_stop = 1;
        textView.setText("录音中...");
        startCounting();        //开始计数
        if (flag_pause == 0){
            audioRecorder = new AudioRecorder();
            audioRecorder.createDefaultAudio(fileName);
            audioRecorder.startRecord();
        }else {
            audioRecorder.startRecord();
        }
    }

    /**
     * 当录音转为暂停时进行的一系列操作
     */
    private void pause() {
        record.setBackgroundResource(R.drawable.chushi);     //修改录音按钮图像
        textView.setText("录音已暂停");
        flag_record = 0;
        flag_pause = 1;
        timer.cancel();
        timer = null;
        audioRecorder.pauseRecord();
    }

    /**
     * 当录音结束时进行的一系列操作
     */
    private void stop() {
        stop.setBackgroundResource(R.drawable.stop1);        //修改录音记录图像
        flag_record = 0;
        record.setBackgroundResource(R.drawable.chushi);     //修改录音按钮图像
        flag_stop = 0;
        flag_pause = 0;
        textView.setText("");
        Toast.makeText(this,"录音结束！",Toast.LENGTH_LONG);
        count.setText("00:00:00");
        cnt = 0;
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        audioRecorder.stopRecord();
    }

    /**
     * 开始计数功能
     */
    private void startCounting() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        count.setText(getStringTime(cnt++));
                    }
                });
            }
        };
        if (timer == null){
            timer = new Timer();
        }
        timer.schedule(timerTask,0,1000);       //1秒执行一次
    }

    /**
     * 计数功能
     * @param cnt
     * @return
     */
    private String getStringTime(int cnt){
        int hour = cnt/3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        return String.format(Locale.CHINA,"%02d:%02d:%02d",hour,min,second);
    }
}
